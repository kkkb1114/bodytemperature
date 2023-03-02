package kkkb1114.sampleproject.bodytemperature;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationBarView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

import kkkb1114.sampleproject.bodytemperature.BleConnect.ConnectActivity;
import kkkb1114.sampleproject.bodytemperature.database.Bodytemp_DBHelper;
import kkkb1114.sampleproject.bodytemperature.fragment.BodyTemperatureGraphFragment;
import kkkb1114.sampleproject.bodytemperature.fragment.HomeFragment;
import kkkb1114.sampleproject.bodytemperature.fragment.SettingFragment;
import kkkb1114.sampleproject.bodytemperature.notification.AlarmReceiver;
import kkkb1114.sampleproject.bodytemperature.notification.AlarmSoundService;
import kkkb1114.sampleproject.bodytemperature.thermometer.Generator;
import kkkb1114.sampleproject.bodytemperature.tools.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    Context context;

    // 프래그먼트
    HomeFragment homeFragment;
    BodyTemperatureGraphFragment bodyTemperatureGraphFragment;
    SettingFragment settingFragment;

    // 바텀 네비게이션
    NavigationBarView navigationBarView;

    Handler handler = new Handler();
    Stack<Double> tempStack = new Stack<>();

   SharedPreferences select_user;

    //db
    public static Bodytemp_DBHelper bodytemp_dbHelper;
    SQLiteDatabase sqlDB;
    String username;
    Cursor cursor;

    // 알람 관련
    AlarmManager alarmManager_high_tempreture;
    AlarmManager alarmManager_low_tempreture;
    PendingIntent pendingIntent;
    public static PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        // DB 생성
        bodytemp_dbHelper = Bodytemp_DBHelper.getInstance(context, "Bodytemperature.db", null, 1);
        // 알람매니저 설정
        alarmManager_high_tempreture = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager_low_tempreture = (AlarmManager) getSystemService(ALARM_SERVICE);
        // MediaPlayer 객체 생성
        //mediaPlayer = MediaPlayer.create(this, R.raw.ouu);
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(
                PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "alarm_temperature:Tag");

        //날짜 측정
        setUser();
        initView();
        setToolbar();
        setFragment();
        MeasurBodyTempreture();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUser();
        setAlarmCancle(); // 어차피 알람은 설정된 체온이 되면 울리기에 알람이 울렸을때 메인이 켜질때마다 끌 예정 (음악 알람일때를 대비해서 설정함)
    }

    public void initView(){
        homeFragment = new HomeFragment();
        bodyTemperatureGraphFragment = new BodyTemperatureGraphFragment();
        settingFragment = new SettingFragment();
        toolbar = findViewById(R.id.toolbar_main);
        navigationBarView = findViewById(R.id.bottomNavigation);

    }

    /** 툴바 세팅 **/
    public void setToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.bluetoothConnect:
                Intent intent = new Intent(this, ConnectActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    /** 프래그먼트 세팅 **/
    public void setFragment(){
        // 초기 화면 세팅
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, homeFragment).commit();
        setBottomNavationClick();
    }

    /** 바텀 네비게이션 클릭 이벤트 **/
    public void setBottomNavationClick(){
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, homeFragment).commit();
                        return true;
                    case R.id.graph:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, bodyTemperatureGraphFragment).commit();
                        return true;
                    case R.id.setting:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, settingFragment).commit();
                        return true;
                }
                return false;
            }
        });
    }

    /** 체온 측정 스레드 **/
    public void MeasurBodyTempreture(){

        // 체온측정 스레드 시작.
        new Thread(new Runnable() {
            @Override
            public void run() {

                // 1분이 지나면 최대값 파일에 작성
                if (tempStack.size() >= 20){
                    Double avg=tempStack.peek();
                    Double cmp = 0.0;
                    while(!tempStack.isEmpty()) {
                        cmp += tempStack.pop();
                    }
                    avg=cmp/20;
                    cmp=0.0;
                    tempStack.clear();

                    long now =System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                    String tempDateTime = dateFormat1.format(date);

                    sqlDB = MainActivity.bodytemp_dbHelper.getWritableDatabase();
                    // todo 제한 조건이 맞지 않는다며 에러가 뜨는데 일단 PRIMARY KEY 중복을 의심해보기로 함 (테스트 필요)
                    sqlDB.execSQL("INSERT INTO TEMPDATA VALUES ('"+username+"', '"+avg+"', '"+ tempDateTime +"');");
                }

                // 3초마다 난수 받아옴
                String s = Generator.generate();
                homeFragment.setTextThermometerView(Float.valueOf(s));
                //thermometer.setValueAndStartAnim(Float.valueOf(s));
                homeFragment.setTextTvTemperature(s);
                handler.postDelayed(this, 3000);
                tempStack.add(Double.valueOf(s));

                //todo 여기서 알람 체크 하기
                setNotification(s);
                Log.d("------------", String.valueOf(tempStack.size()));

            }
        }).start();
    }

    public void setUser()
    {
        select_user = context.getSharedPreferences("login_user",MODE_PRIVATE);
        username = select_user.getString("userName","선택된 사용자 없음");


        if(username.equals("선택된 사용자 없음"))
        {
            Toast.makeText(getApplicationContext(), "사용자 등록을 완료해주세요.", Toast.LENGTH_SHORT).show();
        }
        /*
        long now =System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String str = username+dateFormat.format(date)+"tempData";

        preferences = context.getSharedPreferences(str, MODE_PRIVATE);
        editor = preferences.edit();
        */

    }

    /** 노티피케이션 세팅 **/
    public void setNotification(String s){
        PreferenceManager.PREFERENCES_NAME = "login_user";
        String select_user_name = PreferenceManager.getString(context, "userName");

        if (select_user_name != null){
            PreferenceManager.PREFERENCES_NAME = select_user_name+"Setting";
            boolean alarm_high_temperature_boolean = PreferenceManager.getBoolean(context, "alarm_high_temperature_boolean");
            boolean alarm_low_temperature_boolean = PreferenceManager.getBoolean(context, "alarm_low_temperature_boolean");
            boolean alarm_sound_temperature_boolean = PreferenceManager.getBoolean(context, "alarm_sound_temperature_boolean");

            // 고온 노티 체크
            checkNotificationTemperature(alarm_high_temperature_boolean, alarm_sound_temperature_boolean, s, "high");
            // 저온 노티 체크
            checkNotificationTemperature(alarm_low_temperature_boolean, alarm_sound_temperature_boolean, s, "low");

        }
    }

    /** 체온 노티 체크 **/
    public void checkNotificationTemperature(boolean isAlarm, boolean isSoundAlarm, String s, String high_or_low){
        if (isAlarm){
            Log.e("뭐지?", "1111111");
            int requestID = (int) System.currentTimeMillis();
            if (high_or_low.equals("high")){ // 고온 알람
                String alarm_temperature_str = PreferenceManager.getString(context, "alarm_high_temperature_value");
                double temperature_get = Double.parseDouble(alarm_temperature_str);
                double temperature_s = Double.parseDouble(s);

                if (temperature_get <= temperature_s){
                    Log.e("뭐지?", "2222222");
                    Intent intent = new Intent(context, AlarmReceiver.class);
                    intent.putExtra("now_temperature", s);
                    intent.putExtra("alarm_temperature", alarm_temperature_str);
                    intent.putExtra("alarm_mode", 0); // 0: 고온, 1: 저온
                    intent.putExtra("isSoundAlarm", isSoundAlarm);

                    pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), requestID, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);

                    alarmManager_high_tempreture.set(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
                    Log.e("뭐지?", "22222221344");
                }
            }else { // 저온 알람
                String alarm_temperature_str = PreferenceManager.getString(context, "alarm_low_temperature_value");
                double temperature_get = Double.parseDouble(alarm_temperature_str);
                double temperature_s = Double.parseDouble(s);

                if (temperature_get >= temperature_s){
                    Log.e("뭐지?", "2222222111");
                    Intent intent = new Intent(context, AlarmReceiver.class);
                    intent.putExtra("now_temperature", s);
                    intent.putExtra("alarm_temperature", alarm_temperature_str);
                    intent.putExtra("alarm_mode", 1); // 0: 고온, 1: 저온
                    intent.putExtra("isSoundAlarm", isSoundAlarm);

                    pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), requestID, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    alarmManager_low_tempreture.set(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
                    Log.e("뭐지?", "22222221333");
                }
            }
        }
    }

    /** 알람 종료 **/
    // todo mediaPlayer, wakeLock도 꺼야함.
    public void setAlarmCancle(){
        if (alarmManager_high_tempreture != null && pendingIntent != null){
            alarmManager_high_tempreture.cancel(pendingIntent);
        }
        if (alarmManager_low_tempreture != null && pendingIntent != null){
            alarmManager_low_tempreture.cancel(pendingIntent);
        }

        // 알람 서비스 동작 중인지 확인 후 중지
        Intent intent = new Intent(context, AlarmSoundService.class);
        context.stopService(intent);
    }

    /** 알람 서비스 동작 중인지 확인 **/
    public boolean isAlarmServiceRunning(){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)){
            if (AlarmSoundService.class.getName().equals(runningServiceInfo.getClass().getName())){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 메인 엑티비티가 onDestroy()될때 DB도 모두 닫아준다.
        bodytemp_dbHelper.closeDBHelper();
        setAlarmCancle();
    }



}
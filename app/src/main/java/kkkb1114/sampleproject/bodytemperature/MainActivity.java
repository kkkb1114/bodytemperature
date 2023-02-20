package kkkb1114.sampleproject.bodytemperature;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

import kkkb1114.sampleproject.bodytemperature.BleConnect.ConnectActivity;
import kkkb1114.sampleproject.bodytemperature.fragment.BodyTemperatureGraphFragment;
import kkkb1114.sampleproject.bodytemperature.fragment.HomeFragment;
import kkkb1114.sampleproject.bodytemperature.fragment.SettingFragment;
import kkkb1114.sampleproject.bodytemperature.notification.TemperatureNotification;
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

    private SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Handler handler = new Handler();
    Stack<Double> tempStack = new Stack<>();

   SharedPreferences select_user;

   // 노티 변수
    TemperatureNotification temperatureNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        //날짜 측정
        setUser();
        initView();
        setToolbar();
        setFragment();
        MeasurBodyTempreture();
    }

    protected void onResume() {

        super.onResume();
        setUser();
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
        menuInflater.inflate(R.menu.main_toolbar, menu);
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



    protected void onDestroy(Bundle savedInstanceState) throws IOException {
        super.onDestroy();
    }

    /** 체온 측정 스레드 **/
    public void MeasurBodyTempreture(){

        // 체온측정 스레드 시작.
        new Thread(new Runnable() {
            @Override
            public void run() {

                // 1분이 지나면 최대값 파일에 작성
                if (tempStack.size() >= 20){
                    Double max=tempStack.peek();

                    while(!tempStack.isEmpty()) {

                        Double cmp = tempStack.pop();
                        if(cmp>max)
                            max=cmp;

                    }
                    tempStack.clear();

                    long now =System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");

                    editor.putString(dateFormat.format(date),String.valueOf(max));
                    editor.commit();
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
        select_user = context.getSharedPreferences("user_list",MODE_PRIVATE);
        String username = select_user.getString("select_user_name","선택된 사용자 없음");


        if(username.equals("선택된 사용자 없음"))
        {
            Toast.makeText(getApplicationContext(), "사용자 등록을 완료해주세요.", Toast.LENGTH_SHORT).show();
        }

        long now =System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String str = username+dateFormat.format(date)+"tempData";

        preferences = context.getSharedPreferences(str, MODE_PRIVATE);
        editor = preferences.edit();
    }

    /** 노티피케이션 세팅 **/
    public void setNotification(String s){
        PreferenceManager.PREFERENCES_NAME = "user_list";
        String select_user_name = PreferenceManager.getString(context, "select_user_name");

        if (select_user_name != null){
            PreferenceManager.PREFERENCES_NAME = select_user_name+"Profile";
            boolean alarm_high_temperature_boolean = PreferenceManager.getBoolean(context, "alarm_high_temperature_boolean");
            boolean alarm_low_temperature_boolean = PreferenceManager.getBoolean(context, "alarm_low_temperature_boolean");

            // 고온 노티 체크
            checkNotificationTemperature(alarm_high_temperature_boolean, s, "high");
            // 저온 노티 체크
            checkNotificationTemperature(alarm_low_temperature_boolean, s, "low");

        }
    }

    /** 체온 노티 체크 **/
    public void checkNotificationTemperature(boolean isAlarm, String s, String high_or_low){
        if (isAlarm){
            if (high_or_low.equals("high")){
                String alarm_temperature_str = PreferenceManager.getString(context, "alarm_high_temperature_value");
                double temperature_get = Double.parseDouble(alarm_temperature_str);
                double temperature_s = Double.parseDouble(s);

                if (temperature_get <= temperature_s){
                    temperatureNotification = new TemperatureNotification(context);
                    temperatureNotification.setNotification_HighTemperature(s);

                    // 알람이 한번 울리면 알람 설정을 off 한다.
                    PreferenceManager.setBoolean(context, "alarm_high_temperature_boolean", false);
                }
            }else {
                String alarm_temperature_str = PreferenceManager.getString(context, "alarm_low_temperature_value");
                double temperature_get = Double.parseDouble(alarm_temperature_str);
                double temperature_s = Double.parseDouble(s);

                if (temperature_get >= temperature_s){
                    temperatureNotification = new TemperatureNotification(context);
                    temperatureNotification.setNotification_LowTemperature(s);

                    // 알람이 한번 울리면 알람 설정을 off 한다.
                    PreferenceManager.setBoolean(context, "alarm_low_temperature_boolean", false);
                }
            }
        }
    }
}
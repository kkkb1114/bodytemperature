package kkkb1114.sampleproject.bodytemperature.notification;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import kkkb1114.sampleproject.bodytemperature.MainActivity;
import kkkb1114.sampleproject.bodytemperature.tools.PreferenceManager;

public class AlarmReceiver extends BroadcastReceiver {

    Context context;
    // 노티 변수
    TemperatureNotification temperatureNotification;
    String now_temperature; // 현제 체온
    String alarm_temperature; // 알람 기준 체온
    int alarm_mode = 0; // 0: 고온, 1: 저온
    boolean isSoundAlarm = false; // 0: 고온, 1: 저온

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        PreferenceManager.PREFERENCES_NAME = "login_user";
        String select_user_name = PreferenceManager.getString(context, "userName");
        // 해당 화면에서는 알람값만 컨트롤하기에 "PREFERENCES_NAME" 설정
        PreferenceManager.PREFERENCES_NAME = select_user_name+"Setting";
        Log.e("뭐지?", "3333333");
        Log.e("뭐지?", String.valueOf(PreferenceManager.getBoolean(context, "alarm_high_temperature_boolean")));
        Log.e("뭐지?", String.valueOf(PreferenceManager.getBoolean(context, "alarm_low_temperature_boolean")));
        Log.e("뭐지?", String.valueOf(PreferenceManager.getBoolean(context, "alarm_sound_temperature_boolean")));
        Log.e("노티 확인", "1");

        now_temperature = intent.getStringExtra("now_temperature");
        alarm_temperature = intent.getStringExtra("alarm_temperature");
        alarm_mode = intent.getIntExtra("alarm_mode", 0);
        isSoundAlarm = intent.getBooleanExtra("isSoundAlarm", false);

        // 고온, 저온 알람 구분
        if (alarm_mode == 0){
            startNotification_HighTemperature(context);
        }else {
            startNotification_LowTemperature(context);
        }

        // 알람 사운드 추가 체크를 했어야 사운드 실행
        if (isSoundAlarm){
            playSound();
        }
        displayWakeUp();
    }

    /** 고온 Notification 설정 **/
    public void startNotification_HighTemperature(Context context){
        temperatureNotification = new TemperatureNotification(context);
        temperatureNotification.setNotification_HighTemperature(now_temperature, alarm_temperature);

        // 알람이 한번 울리면 알람 설정을 off 한다.
        PreferenceManager.setBoolean(context, "alarm_high_temperature_boolean", false);
    }

    /** 저온 Notification 설정 **/
    public void startNotification_LowTemperature(Context context){
        temperatureNotification = new TemperatureNotification(context);
        temperatureNotification.setNotification_LowTemperature(now_temperature, alarm_temperature);

    }

    /** 사운드 시작 **/
    public void playSound(){
        Log.e("뭐지?_playSound", String.valueOf(PreferenceManager.getBoolean(context, "alarm_sound_temperature_boolean")));
        // 사운드 서비스가 이미 동작중이면 제거하고 생성
        if (isAlarmServiceRunning()){
            stopSound();
        }

        if (PreferenceManager.getBoolean(context, "alarm_sound_temperature_boolean")){
            Intent intent = new Intent(context, AlarmSoundService.class);
            // 오레오 이상은 startForegroundService() / 이하는 startService()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.e("뭐지?_playSound", ".......");
                context.startForegroundService(intent);
            }else {
                Log.e("뭐지?_playSound", ".....111..");
                context.startService(intent);
            }
            // 알람이 한번 울리면 알람 설정을 off 한다.
            PreferenceManager.setBoolean(context, "alarm_sound_temperature_boolean", false);
        }
        // 고온, 저온 알람 둘중 하나라도 true면 사운드 알람을 끄지 않는다.
        /*if (!PreferenceManager.getBoolean(context, "alarm_high_temperature_boolean") &&
                !PreferenceManager.getBoolean(context, "alarm_low_temperature_boolean")){

        }*/
    }

    /** 사운드 정지 **/
    public void stopSound(){
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

    /** 화면 강제 기상 (환자를 상대로 알람을 울리는 것이기에 화면을 강제로 켤 필요가 있다고 보았다.) **/
    // todo PowerManager.FULL_WAKE_LOCK 대신 쓸 것을 찾아야함
    public void displayWakeUp(){
        MainActivity.wakeLock.acquire(); // 화면 즉시 기상
        MainActivity.wakeLock.release(); // wakeLock 자원 해제
        Log.e("노티 확인", "4");
    }
}
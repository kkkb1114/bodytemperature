package kkkb1114.sampleproject.bodytemperature.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import kkkb1114.sampleproject.bodytemperature.tools.PreferenceManager;

public class AlarmReceiver extends BroadcastReceiver {

    Context context;
    // 노티 변수
    TemperatureNotification temperatureNotification;
    String now_temperature; // 현제 체온
    String alarm_temperature; // 알람 기준 체온
    int alarm_mode = 0; // 0: 고온, 1: 저온

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        Log.e("노티 확인", "1");

        now_temperature = intent.getStringExtra("now_temperature");
        alarm_temperature = intent.getStringExtra("alarm_temperature");
        alarm_mode = intent.getIntExtra("alarm_mode", 0);

        // 고온, 저온 알람 구분
        if (alarm_mode == 0){
            startNotification_HighTemperature(context);
        }else {
            startNotification_LowTemperature(context);
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

        // 알람이 한번 울리면 알람 설정을 off 한다.
        PreferenceManager.setBoolean(context, "alarm_low_temperature_boolean", false);
    }

    /** 화면 강제 기상 (환자를 상대로 알람을 울리는 것이기에 화면을 강제로 켤 필요가 있다고 보았다.) **/
    // todo PowerManager.FULL_WAKE_LOCK 대신 쓸 것을 찾아야함
    public void displayWakeUp(){
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "alarm_temperature:Tag");

        wakeLock.acquire(); // 화면 즉시 기상
        wakeLock.release(); // wakeLock 자원 해제
        Log.e("노티 확인", "4");
    }
}
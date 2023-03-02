package kkkb1114.sampleproject.bodytemperature.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import kkkb1114.sampleproject.bodytemperature.R;

public class AlarmSoundService extends Service {

    MediaPlayer mediaPlayer;
    final String CHANNEL_ID = "Alarm_sound_notification_channel";
    final int NOTIFICATION_ID = 10;
    Notification notification;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("체온 알람 사운드")
                .setContentText("체온 알람 사운드가 실행 중입니다.")
                .build();
        startForeground(NOTIFICATION_ID, notification);
        mediaPlayer = MediaPlayer.create(this, R.raw.ouu);
        mediaPlayer.setLooping(false); // 반복재생 false
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer.start();
        return super.onStartCommand(intent, flags, startId);
    }

    /** 오레오 이상 버전부턴 startForegroundService()로 서비스 실행시 Notificaiton Channel과 Notification를 만들어 Notification를 등록해야한다. **/
    public void createNotificationChannel(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alarm_sound_notification",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("하하하하");

            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy_sound", "onDestroy_sound");
        mediaPlayer.stop();
        stopForeground(true);
    }
}

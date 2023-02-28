package kkkb1114.sampleproject.bodytemperature.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import kkkb1114.sampleproject.bodytemperature.MainActivity;
import kkkb1114.sampleproject.bodytemperature.R;

public class TemperatureNotification {

    Context context;
    public static final String NOTIFICATION_CHANNEL_ID_TEMPERATURE_HIGH = "10001"; // 고온 알람 노티 채널 ID
    public static final String NOTIFICATION_CHANNEL_ID_TEMPERATURE_LOW = "10002"; // 고온 알람 노티 채널 ID

    public TemperatureNotification(Context context){
        this.context = context;
    }

    // 고온 Notification 설정
    public void setNotification_HighTemperature(String now_temperature, String high_temperature){

        // 채널을 생성 및 전달해 줄수 있는 NotificationManager 생성
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // 이동하려는 액티비티를 작성해준다.
        Intent notificationIntent = new Intent(context, MainActivity.class);
        // 노티에 전달 값을 담는다.
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 고온 노티
        NotificationCompat.Builder builder_high_temperature = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID_TEMPERATURE_HIGH)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.pill))
                .setContentTitle("고온 알림")
                .setContentText("현재 체온이"+ high_temperature +"°C 를 넘었습니다.\n" +
                        "(현재 체온: "+now_temperature+"°C)")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // 노티 클릭시 위에 생성한 PendingIntent 실행 (설정한 데이터 담고 MainActivity로 이동)
                .setAutoCancel(true); // 눌러야 꺼짐

        // OREO API 26 이상부터 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            builder_high_temperature.setSmallIcon(R.drawable.pill);
            CharSequence channelName = "노티피케이션 채널";
            String description = "오레오 이상";
            int importance = NotificationManager.IMPORTANCE_HIGH; // 우선순위 설정

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID_TEMPERATURE_HIGH,
                    channelName, importance);
            notificationChannel.setDescription(description);

            // 노티피케이션 채널 시스템에 등록
            if (notificationManager != null){
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }else {
            builder_high_temperature.setSmallIcon(R.mipmap.ic_launcher_round); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남
        }

        if (notificationManager != null){
            notificationManager.notify(1234, builder_high_temperature.build());
        }
    }

    // 저온 Notification 설정
    public void setNotification_LowTemperature(String now_temperature, String low_temperature){

        // 채널을 생성 및 전달해 줄수 있는 NotificationManager 생성
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // 이동하려는 액티비티를 작성해준다.
        Intent notificationIntent = new Intent(context, MainActivity.class);
        // 노티에 전달 값을 담는다.
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 저온 노티
        NotificationCompat.Builder builder_low_temperature = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID_TEMPERATURE_LOW)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.pill))
                .setContentTitle("저온 알림")
                .setContentText("현재 체온이"+ low_temperature +"°C 이하로 떨어졌습니다.\n" +
                        "(현재 체온: "+now_temperature+"°C)")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // 노티 클릭시 위에 생성한 PendingIntent 실행 (설정한 데이터 담고 MainActivity로 이동)
                .setAutoCancel(true); // 눌러야 꺼짐

        // OREO API 26 이상부터 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            builder_low_temperature.setSmallIcon(R.drawable.pill);
            CharSequence channelName = "노티피케이션 채널";
            String description = "오레오 이상";
            int importance = NotificationManager.IMPORTANCE_HIGH; // 우선순위 설정

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID_TEMPERATURE_LOW,
                    channelName, importance);
            notificationChannel.setDescription(description);

            // 노티피케이션 채널 시스템에 등록
            if (notificationManager != null){
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }else {
            builder_low_temperature.setSmallIcon(R.mipmap.ic_launcher_round); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남
        }

        if (notificationManager != null){
            notificationManager.notify(12345, builder_low_temperature.build());
        }
    }

    /** 큰알람 세팅 **/
    public void setBigAlarm(){

    }
}

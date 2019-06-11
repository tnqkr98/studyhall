package com.example.test3;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyMs";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //super.onMessageReceived(remoteMessage);
        Log.i(TAG,"onMessaggeReceived() 호출됨");
        String from = remoteMessage.getFrom();
        Map<String, String> data = remoteMessage.getData();
        String contents = data.get("contents");
        String mode = data.get("mode");

        Log.i(TAG,"from : "+from+", contents : "+contents);
        pushAlarm(getApplicationContext(),from,contents,mode);
    }

    @Override
    public void onCreate() { // 서비스의 시작

        Log.d("서비스 : ", "onCreate 호출");
        super.onCreate();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d("서비스 : ", "onRebind 호출");
    }

    private void pushAlarm(Context context, String from, String contents,String mode){
        //Intent intent = new Intent(context, MainActivity.class);
        //intent.putExtra("from",from);
        //intent.putExtra("contents",contents);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);

        //context.startActivity(intent); //MainActiviy의 onNewIntent 함수로 전달

        Log.i("DATA : ","["+from+"]으로 부터 수신받은 메시지 : "+contents);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,PendingIntent.FLAG_ONE_SHOT);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {   //studyhall 푸쉬알림 채널 생성
            NotificationChannel mChannel = new NotificationChannel("studyhall_channel","my_channel", NotificationManager.IMPORTANCE_HIGH);
            mChannel.setDescription("This is my channel");
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        int modenum = Integer.parseInt(mode);

        Log.i("받은 modenum ->>>>>>>>>",modenum+"");
        if(modenum == 1) {
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, "studyhall_channel")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("당신은 다른 회원으로부터 경고를 받았습니다.")
                            .setContentText(contents)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);
            notificationManager.notify(0, notificationBuilder.build());
        }
        else if(modenum == 2){
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, "studyhall_channel")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("회원으로부터 컴플레인이 들어왔습니다.")
                            .setContentText(contents)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);
            notificationManager.notify(0, notificationBuilder.build());
        }
    }
}

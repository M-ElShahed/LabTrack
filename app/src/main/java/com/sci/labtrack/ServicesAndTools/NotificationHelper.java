package com.sci.labtrack.ServicesAndTools;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.sci.labtrack.LoginActivity;
import com.sci.labtrack.R;

public class NotificationHelper extends ContextWrapper {

    public static final String Channel_ID= "LabTrack_Channel_ID";
    public static final String Channel_Name= "Labtrack";
    private NotificationManager manger;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationHelper(Context base) {
        super(base);
        CreateChannels();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void CreateChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel labTrackChannel = new NotificationChannel(Channel_ID, Channel_Name, NotificationManager.IMPORTANCE_HIGH);
            labTrackChannel.enableLights(true);
            labTrackChannel.enableVibration(true);
            labTrackChannel.setLightColor(Color.GREEN);
            labTrackChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManger().createNotificationChannel(labTrackChannel);
        }
    }

    public NotificationManager getManger() {
        if(manger == null){
            manger = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manger;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getChannelNotiication(String Title, String body){
        Intent intent = new Intent(this, LoginActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        return new Notification.Builder(getApplicationContext(),Channel_ID)
                .setContentText(body)
                .setStyle(new Notification.BigTextStyle().bigText(body))
                .setContentTitle(Title)
                .setContentIntent(pIntent)
                .setSmallIcon(R.drawable.ic_edit);

    }
}

package com.driverconnex.parking;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.driverconnex.app.AppConfig;
import com.driverconnex.app.R;

/**
 * Activity used to display notification to remind driver about parked vehicle
 * @author Adrian Klimczak
 */

public class DisplayNotification extends Activity 
{
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        // Give default sound to notification
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        
        // Construct notification
        NotificationCompat.Builder mBuilder = 
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle(getResources().getString(R.string.app_name))
		        .setContentText("Your parking ticket will expire shortly")
		        .setVibrate(new long[] { 100, 250, 100, 500})
		        .setSound(notificationSound);
        
        NotificationManager mNotificationManager =
    		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(AppConfig.PARKING_NOTIFICATION_ID, mBuilder.build());
        
        finish();
    }
}

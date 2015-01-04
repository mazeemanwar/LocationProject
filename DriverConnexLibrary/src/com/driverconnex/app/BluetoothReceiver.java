package com.driverconnex.app;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.widget.Toast;

import com.driverconnex.journeys.TrackJourneyActivity;
import com.parse.ParseUser;

/**
 * Broadcast receiver for the Bluetooth. When device is close enough to paired device it will open DriverConnex app and it will start tracking.
 * 
 * NOTE:
 * For some reason when it connects to Bluetooth device after short time it disconnects from it. 
 * 
 * @author Yin Lee (SGI)
 * @author Adrian Klimczak
 *
 */

public class BluetoothReceiver extends BroadcastReceiver
{
	protected PowerManager powerManager = null;
	protected WakeLock wakeLock = null;

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		// When device is connected
		if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED)) 
		{
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			
			if (device != null) 
			{
				// Checks if this is the device that is saved in user preferences as a default Bluetooth device
				if (device.getName().equals(DriverConnexApp.getUserPref().getBluetoothDeviceName())) 
				{
					powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
					wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK	| PowerManager.ACQUIRE_CAUSES_WAKEUP, "driverConnex BT Starter");
					wakeLock.acquire();
				
					Toast.makeText(context, "Starting Tracker...", Toast.LENGTH_LONG).show();
					Intent i = new Intent();
					
					if (ParseUser.getCurrentUser() == null)
						i.setClass(context, LoginActivity.class);
					else 
					{
						i.setAction(context.getPackageName() + ".TrackActivity.start");
						i.setClass(context, TrackJourneyActivity.class);
					}
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
							| Intent.FLAG_ACTIVITY_SINGLE_TOP
							| Intent.FLAG_ACTIVITY_NEW_TASK);
					
					context.startActivity(i);
					wakeLock.release();
				}
			}
		}
		// When device is disconnected
		else if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) 
		{
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			
			if (device != null) 
			{
				// Checks if this is the device that is saved in user preferences as a default Bluetooth device
				if (device.getName().equals(DriverConnexApp.getUserPref().getBluetoothDeviceName())) 
				{
					Toast.makeText(context, "Pausing Tracker...", Toast.LENGTH_LONG).show();
					powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
					wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "driverConnex BT Starter");
					wakeLock.acquire();

					Intent i = new Intent();
					if (ParseUser.getCurrentUser() == null)
						i.setClass(context, LoginActivity.class);
					else 
					{
						i.setAction(context.getPackageName() + ".TrackActivity.pause");
						i.setClass(context, TrackJourneyActivity.class);
					}
					
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
							| Intent.FLAG_ACTIVITY_SINGLE_TOP
							| Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(i);

					wakeLock.release();
				}
			}
		}
	}
}

package com.driverconnex.singletons;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.OrientationEventListener;
import android.widget.Chronometer;

import com.driverconnex.journeys.ActivityRecognitionIntentService;
import com.driverconnex.journeys.DCBehaviourPoint;
import com.driverconnex.journeys.DCJourney;
import com.driverconnex.journeys.DCJourneyPoint;
import com.driverconnex.journeys.JourneyDataSource;
import com.driverconnex.utilities.Utilities;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

/**
 * Singleton for tracking a journey in the background.
 * 
 * @author Adrian Klimczak
 * 
 */
public class TrackJourneySingleton {
	// Variables for the DCJourney
	// ---------------------------------
	private static DCJourney journey = null;
	private static JourneyDataSource dataSource;
	private static int journeyPoints = 0;

	private static LocationClient locationClient = null;
	private static LocationRequest locationRequest = null;

	private static Chronometer chronometer = null;
	private static long timeWhenStopped = 0;

	private static String distance = "0.0";
	private static float totalDistance = 0; // Total travelled distance
	private static float topSpeed = 0; // the highest speed
	private static Location previousLocation = null;
	private static long startJourneyTime = 0;
	private static long endJourneyTime = 0;

	// Variables for the DCBehaviourPoint
	// ---------------------------------
	private static final int DETECTION_INTERVAL_MILLISECONDS = 1000;

	// Activity Recognition
	private static ActivityRecognitionClient recognitionClient;
	private static PendingIntent recognitionPendingIntent;

	// Sensors
	private static SensorManager sensorManager;
	private static SensorEventListener sensorEventListener;
	private static OrientationEventListener myOrientationEventListener;

	// Variables put into DCBehaviourPoint
	private static float[] accelerometer = new float[3];
	private static float speed = 0; // current speed
	private static boolean deviceFlat;
	private static boolean deviceLandscape;
	private static boolean devicePortrait;
	private static String activity;

	// Timer
	private static long timeElapsed = 0;
	private static Handler timerHandler = new Handler();

	// ---------------------------------
	private static ArrayList<LatLng> routePoints = null;
	private static boolean isTracking = false;

	/**
	 * Starts tracking journey
	 */
	public static void startTracking(Context context) {
		// Check if it's not already tracking
		if (!isTracking) {
			// Initialise variables if they are not yet initialised
			// -----------------------------------
			if (chronometer == null)
				chronometer = new Chronometer(context);

			// Create a new journey
			if (journey == null) {
				if (dataSource == null)
					dataSource = new JourneyDataSource(context);
				dataSource.open();
				journey = new DCJourney();
				journey.setId(dataSource.createJourney(journey, null));
				dataSource.close();
			}

			if (routePoints == null)
				routePoints = new ArrayList<LatLng>();

			// Check if Google Play Services are available
			if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS) {
				if (locationClient == null)
					locationClient = new LocationClient(context,
							locationConnectionCallbacks,
							mConnectionFailedListener);
				if (recognitionClient == null) {
					recognitionClient = new ActivityRecognitionClient(context,
							recognitionConnectionCallbacks,
							mConnectionFailedListener);
				}
				if (recognitionPendingIntent == null) {
					Intent intent = new Intent(context,
							ActivityRecognitionIntentService.class);

					recognitionPendingIntent = PendingIntent.getService(
							context, 0, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
				}

				locationClient.connect();
				recognitionClient.connect();
			}

			// -----------------------------------

			if (startJourneyTime == 0)
				startJourneyTime = System.currentTimeMillis();

			// Set chronometer
			chronometer
					.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
			chronometer.start();

			// Restart timer
			timeElapsed = System.currentTimeMillis();
			timerHandler.postDelayed(timerRunnable, 0);

			startSensor(context);

			isTracking = true;
		}
	}

	/**
	 * Stops tracking journey
	 */
	public static void stopTracking() {
		if (isTracking) {
			endJourneyTime = System.currentTimeMillis();

			timeWhenStopped = chronometer.getBase()
					- SystemClock.elapsedRealtime();
			chronometer.stop();

			isTracking = false;

			// Stop all listeners
			sensorManager.unregisterListener(sensorEventListener);
			locationClient.removeLocationUpdates(locationListener);
			recognitionClient.removeActivityUpdates(recognitionPendingIntent);

			locationClient.disconnect();
			recognitionClient.disconnect();

			if (timerHandler != null)
				timerHandler.removeCallbacks(timerRunnable);
		}
	}

	/**
	 * Resets tracker.
	 */
	public static void reset() {
		stopTracking();

		if (chronometer != null)
			chronometer.stop();

		journey = null;

		timeWhenStopped = 0;
		isTracking = false;
		distance = "0.0";
		topSpeed = 0;
		totalDistance = 0;
		startJourneyTime = 0;
		endJourneyTime = 0;
		journeyPoints = 0;
		previousLocation = null;

		if (routePoints != null)
			routePoints.clear();

		// Reset DC Behaviour variables
		for (int i = 0; i < accelerometer.length; i++)
			accelerometer[i] = 0;

		speed = 0;
		deviceFlat = false;
		deviceLandscape = false;
		devicePortrait = false;
		activity = null;
	}

	private static LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			if (journey != null) {
				distance = calDistance(location);

				// m/s
				topSpeed = Utilities.roundTwoDecimals(location.getSpeed()) > topSpeed ? Utilities
						.roundTwoDecimals(location.getSpeed()) : topSpeed;

				speed = location.getSpeed();

				// Do not draw polyline if accuracy is too low
				// if(location.getAccuracy() <= 50)
				{
					// Record journey point
					// -------------------------------
					DCJourneyPoint point = new DCJourneyPoint();
					point.setLat(location.getLatitude());
					point.setLng(location.getLongitude());

					dataSource.open();
					dataSource.createJourneyPoint(journey.getId(), point);
					dataSource.close();
					journeyPoints++;
					// -------------------------------

					// Record polyline
					routePoints.add(new LatLng(location.getLatitude(), location
							.getLongitude()));
				}
			}
		}
	};

	// ---------------------------------------------
	/**
	 * Starts sensor to read data.
	 */
	private static void startSensor(final Context context) {
		myOrientationEventListener = new OrientationEventListener(context,
				SensorManager.SENSOR_DELAY_NORMAL) {
			@Override
			public void onOrientationChanged(int orientation) {
				// Log.d("TEST","Orientation: " + String.valueOf(arg0));

				if ((orientation > 45 && orientation < 135)
						|| (orientation > 225 && orientation < 315)) {
					deviceLandscape = true;
					devicePortrait = false;
				} else {
					devicePortrait = true;
					deviceLandscape = false;
				}

				if (orientation == -1) {
					deviceFlat = true;
					devicePortrait = false;
					deviceLandscape = false;
				} else
					deviceFlat = false;
			}
		};

		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		sensorEventListener = new SensorEventListener() {
			@Override
			public void onSensorChanged(SensorEvent event) {
				int type = event.sensor.getType();

				if (type == Sensor.TYPE_MAGNETIC_FIELD) {
				} else if (type == Sensor.TYPE_LINEAR_ACCELERATION) {
					accelerometer = event.values.clone();
				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};

		// Set listeners
		myOrientationEventListener.enable();
		sensorManager
				.registerListener(sensorEventListener, sensorManager
						.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
						SensorManager.SENSOR_DELAY_UI);
	}

	private static ConnectionCallbacks locationConnectionCallbacks = new ConnectionCallbacks() {
		@Override
		public void onDisconnected() {
		}

		@Override
		public void onConnected(Bundle arg0) {
			locationRequest = LocationRequest.create();
			locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			locationRequest.setInterval(DETECTION_INTERVAL_MILLISECONDS);
			locationRequest.setFastestInterval(DETECTION_INTERVAL_MILLISECONDS);

			locationClient.requestLocationUpdates(locationRequest,
					locationListener);
		}
	};

	private static ConnectionCallbacks recognitionConnectionCallbacks = new ConnectionCallbacks() {
		@Override
		public void onDisconnected() {
		}

		@Override
		public void onConnected(Bundle arg0) {
			recognitionClient.requestActivityUpdates(
					DETECTION_INTERVAL_MILLISECONDS, recognitionPendingIntent);
		}
	};

	private static OnConnectionFailedListener mConnectionFailedListener = new OnConnectionFailedListener() {
		@Override
		public void onConnectionFailed(ConnectionResult arg0) {
			Log.e("Connection", "ConnectionFailed");
		}
	};
	// ---------------------------------------------

	/**
	 * Sets timer. Every second DCBehaviourPoint should be saved in the
	 * database.
	 */
	private static Runnable timerRunnable = new Runnable() {
		@Override
		public void run() {
			long millis = System.currentTimeMillis() - timeElapsed;
			int seconds = (int) (millis / 1000);

			// Every second save point to the database
			if (seconds > DETECTION_INTERVAL_MILLISECONDS / 1000) {
				saveBehaviourPoint();
				timeElapsed = System.currentTimeMillis();
			}

			timerHandler.postDelayed(this, 500);
		}
	};

	/**
	 * Saves DCBehaviourPoint into database
	 */
	private static void saveBehaviourPoint() {
		if (activity != null && journey != null) {
			DCBehaviourPoint point = new DCBehaviourPoint();

			for (int i = 0; i < accelerometer.length; i++)
				accelerometer[i] /= 9.81f;

			point.setAccelerationX(accelerometer[0]);
			point.setAccelerationY(accelerometer[1]);
			point.setAccelerationZ(accelerometer[2]);

			point.setSpeed(speed);
			point.setDeviceFlat(deviceFlat);
			point.setDeviceLandscape(deviceLandscape);
			point.setDevicePortrait(devicePortrait);
			point.setActivity(activity);

			dataSource.open();
			dataSource.createBehaviourPoint(journey.getId(), point);
			dataSource.close();
		}
	}

	public static long getTrackingDuration() {
		if (isTracking)
			return chronometer.getBase();

		return SystemClock.elapsedRealtime() + timeWhenStopped;
	}

	/**
	 * Calculates total travelled distance
	 * 
	 * @param curLoc
	 * @return
	 */
	private static String calDistance(Location curLoc) {
		// Check if there is previous location
		if (previousLocation != null) {
			// Convert meters into miles
			totalDistance += previousLocation.distanceTo(curLoc) * 0.0006;
		}

		previousLocation = curLoc;
		return String.valueOf(Utilities.roundTwoDecimals(totalDistance));
	}

	// Getters and Setters
	// ====================================
	public static boolean isTracking() {
		return isTracking;
	}

	public static Chronometer getChronometer() {
		return chronometer;
	}

	public static String getDistance() {
		return distance;
	}

	/**
	 * Gets number of recorded journey points for the current journey.
	 * 
	 * @return
	 */
	public static int getJourneyPoints() {
		return journeyPoints;
	}

	public static long getStartTime() {
		return startJourneyTime;
	}

	public static long getEndTime() {
		return endJourneyTime;
	}

	public static float getTopSpeed() {
		return topSpeed;
	}

	public static ArrayList<LatLng> getRoutePoints() {
		return routePoints;
	}

	public static String getActivity() {
		return activity;
	}

	public static void setActivity(String activity) {
		TrackJourneySingleton.activity = activity;
	}

	public static DCJourney getJourney() {
		return journey;
	}
}

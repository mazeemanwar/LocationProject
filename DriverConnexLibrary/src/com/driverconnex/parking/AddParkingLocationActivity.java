package com.driverconnex.parking;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TimePicker;
import android.widget.Toast;

import com.driverconnex.app.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Activity for adding a parked location of the vehicle.
 * 
 * @author Adrian Klimczak
 * 
 */

public class AddParkingLocationActivity extends Activity {
	private GoogleMap map;
	private LocationManager locationManager;
	private String provider;

	private TimePickerDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_parking_location);

		// Get the map
		map = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.mapView)).getMap();
		map.setMyLocationEnabled(true);

		// Initialise location of the user
		locationInit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.action_save, menu);
		return true;
	}

	/**
	 * Items on the action bar
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			// Finish this activity
			finish();
			overridePendingTransition(R.anim.null_anim, R.anim.slide_out);
			return true;
		} else if (item.getItemId() == R.id.action_save) {
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			dialog = new TimePickerDialog(AddParkingLocationActivity.this,
					new TimePickerDialog.OnTimeSetListener() {
						int callCount = 0; // To track number of calls to
											// onTimeSet()

						@Override
						public void onTimeSet(TimePicker timePicker,
								int selectedHour, int selectedMinute) {
							callCount++;

							// For some reason onTimeSet is called twice, so
							// check if this is a first call and ignore the
							// second
							if (callCount == 1) {
								dialog.dismiss();

								// Save parked location
								// --------------------
								// Prepare parking location for the database
								ParkingLocation parkingLocation = prepareForDatabase();

								if (parkingLocation == null)
									return;

								ParkingLocationDataSource dataSource = new ParkingLocationDataSource(
										AddParkingLocationActivity.this);
								// Open database
								dataSource.open();
								// Insert parking location into database
								dataSource
										.createParkingLocation(parkingLocation);
								// Close database
								dataSource.close();
								// --------------------

								// Creates parking notification
								createNotification(selectedHour, selectedMinute);

								// Finish this activity
								finish();
								overridePendingTransition(R.anim.null_anim,
										R.anim.slide_out);
							}

						}
					}, hour, minute, DateFormat
							.is24HourFormat(AddParkingLocationActivity.this));

			dialog.setTitle("Set Reminder");
			dialog.show();
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Initialises location of the user on the map
	 */
	private void locationInit() {
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		boolean gpsEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean networkEnabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		// Checks if network and GPS is not enabled
		if (!networkEnabled && !gpsEnabled) {
			AlertDialog.Builder builder = new Builder(this);
			builder.setMessage("Please turn on Location Services in your System Settings.");
			builder.setTitle("Notification");
			builder.setPositiveButton("Settings",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(
									Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivity(intent);
						}
					});

			builder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

			builder.setCancelable(false);
			builder.create().show();
		} else {
			// Sets the criteria for a fine and low power provider
			Criteria crit = new Criteria();
			crit.setAccuracy(Criteria.ACCURACY_FINE);
			crit.setPowerRequirement(Criteria.POWER_LOW);

			// Gets the best matched provider, and only if it's on
			provider = locationManager.getBestProvider(crit, true);
			locationManager.removeUpdates(new LocationListener() {

				@Override
				public void onStatusChanged(String provider, int status,
						Bundle extras) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onProviderEnabled(String provider) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onProviderDisabled(String provider) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onLocationChanged(Location location) {
					// TODO Auto-generated method stub

				}
			});

			Location myLocation = locationManager
					.getLastKnownLocation(provider);
			if (myLocation == null) {
				myLocation = locationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

			}

			if (myLocation != null) {
				double dLatitude = myLocation.getLatitude();
				double dLongitude = myLocation.getLongitude();
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
						dLatitude, dLongitude), 15), 2000, null);
			} else {
				Toast.makeText(this, "Unable to fetch your current location",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * Creates a new parking location object.
	 * 
	 * @return ParkingLocation
	 */
	private ParkingLocation prepareForDatabase() {
		ParkingLocation location = new ParkingLocation();

		// Get the date and format it
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String date = sdf.format(Calendar.getInstance().getTime());

		// Set data for parking location
		location.setDate(date);

		// Make sure it's not null
		if (map.getMyLocation() != null) {
			location.setLatitude(map.getMyLocation().getLatitude());
			location.setLongitude(map.getMyLocation().getLongitude());

			return location;
		}

		return null;
	}

	/**
	 * Creates parking notification
	 */
	private void createNotification(int hour, int minute) {
		// ---get current date and time---
		Calendar calendar = Calendar.getInstance();

		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		// ---sets the time for the alarm to trigger---
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);

		// ---PendingIntent to launch activity when the alarm triggers---
		Intent i = new Intent("com.driverconnex.parking.DisplayNotification");

		PendingIntent displayIntent = PendingIntent.getActivity(
				getBaseContext(), 0, i, 0);

		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				displayIntent);
	}
}

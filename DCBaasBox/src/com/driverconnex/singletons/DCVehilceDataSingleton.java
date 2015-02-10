package com.driverconnex.singletons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasQuery;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.json.JsonObject;
import com.driverconnex.data.Message;
import com.driverconnex.utilities.BaasboxUtilities;
import com.driverconnex.vehicles.DCVehicle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class DCVehilceDataSingleton {

	// Static member holds only one instance of the

	// SingletonExample class

	private static DCVehilceDataSingleton singletonInstance;
	private static ArrayList<String> moduleFromServer = new ArrayList<String>();
	private static ArrayList<Message> messageUpdatedList = new ArrayList<Message>();
	private static ArrayList<DCVehicle> vehiclesList = new ArrayList<DCVehicle>();
	private static LocationClient locationClient = null;
	private static Location myLocation = null;
	private static HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
	private static int totalAlerts = 0;

	// SingletonExample prevents any other class from instantiating

	private DCVehilceDataSingleton() {

	}

	// Providing Global point of access

	public static DCVehilceDataSingleton getDCModuleSingleton(
			final Context context) {

		if (null == singletonInstance) {

			singletonInstance = new DCVehilceDataSingleton();
			System.out.println("with out creating object");
			int size = vehiclesList.size();
			System.out.println(size);
			getVehicleList();
		}

		return singletonInstance;

	}

	public ArrayList<DCVehicle> getVehilesList() {
		// getMessage();

		System.out.println("with out creating object");
		getVehicleList();
		getTotalAlerts();
		return vehiclesList;

	}

	private static void getVehicleList() {

		String user = BaasUser.current().getName();
		final BaasQuery PREPARED_QUERY = BaasQuery.builder()
				.collection("BAAVehicle").where("_author = ?")
				.whereParams(user).build();
		// then
		PREPARED_QUERY.query(new BaasHandler<List<JsonObject>>() {

			@Override
			public void handle(BaasResult<List<JsonObject>> res) {
				// Loop through all vehicles that we got from the query
				if (res.isSuccess()) {
					vehiclesList = new ArrayList<DCVehicle>();
					for (int i = 0; i < res.value().size(); i++) {

						DCVehicle vehicleReturn = BaasboxUtilities
								.convertVehicle(res.value().get(i));

						vehiclesList.add(vehicleReturn);
					}
					// TODO Auto-generated method stub

				} else {
				}
			}

		});

	}

	public static HashMap<String, ArrayList<String>> getTotalAlerts() {
		if (vehiclesList.size() > 0) {
			ArrayList<String> tempArray = new ArrayList<String>();
			totalAlerts = 0;
			for (int i = 0; i < vehiclesList.size(); i++) {
				tempArray = new ArrayList<String>();

				tempArray = BaasboxUtilities.getNumberOfAllAlerts(vehiclesList
						.get(i));
				map.put(vehiclesList.get(i).getRegistration(), tempArray);
			}
			totalAlerts = map.size();
			System.out.println(totalAlerts);
		}
		return map;
	}

	public static void getCurrentLocation(Context context) {
		System.out.println(myLocation);
		// while (locationClient.isConnected()) {
		if (locationClient == null) {

			locationClient = new LocationClient(context,
					locationConnectionCallbacks, connectionFailedListener);
			// myLocation = locationManager
			// .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		}
		locationClient.connect();
		while (!locationClient.isConnected()) {
			if (locationClient.isConnected()) {
				myLocation = locationClient.getLastLocation();
			}
		}

		// }

		System.out.println(myLocation);
	}

	private static ConnectionCallbacks locationConnectionCallbacks = new ConnectionCallbacks() {
		@Override
		public void onDisconnected() {
			locationClient.removeLocationUpdates(locationListener);
		}

		@Override
		public void onConnected(Bundle arg0) {
			Location myLocation = locationClient.getLastLocation();

			if (myLocation != null) {
				double dLatitude = myLocation.getLatitude();
				double dLongitude = myLocation.getLongitude();
				// map.animateCamera(CameraUpdateFactory.newLatLngZoom(new
				// LatLng(
				// dLatitude, dLongitude), 15), 2000, null);
			} else {
				// Toast.makeText(ServiceViewActivity.this,
				// "Unable to fetch your current location",
				// Toast.LENGTH_SHORT).show();
			}

			LocationRequest locationRequest = LocationRequest.create();
			locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			locationRequest.setInterval(1000);
			locationRequest.setFastestInterval(1000);

			locationClient.requestLocationUpdates(locationRequest,
					locationListener);
		}
	};

	private static OnConnectionFailedListener connectionFailedListener = new OnConnectionFailedListener() {
		@Override
		public void onConnectionFailed(ConnectionResult arg0) {
			Log.e("Connection", "ConnectionFailed");
		}
	};
	private static LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			// if (TrackJourneySingleton.getJourneyPoints() >= 3
			// && !stop.isEnabled()) {
			// stop.setEnabled(true);
			// stop.setImageResource(R.drawable.stop_white);
			// }

			// Do not draw polyline if accuracy is too low
			// if(location.getAccuracy() <= 50)

			// map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
			// location.getLatitude(), location.getLongitude()), 15),
			// 1000, null);

			if (TrackJourneySingleton.getRoutePoints() != null) {
				// Makes sure route exists
				// if (route != null) {
				// // Makes sure that any route points were recorded
				// if (TrackJourneySingleton.getRoutePoints() != null)
				// route.setPoints(TrackJourneySingleton.getRoutePoints());
				// }

				// disVal.setText(TrackJourneySingleton.getDistance());
			}
		}
	};

}
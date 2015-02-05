package com.driverconnex.singletons;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.driverconnex.adapter.VehicleListAdapter;
import com.driverconnex.data.Message;
import com.driverconnex.utilities.ParseUtilities;
import com.driverconnex.vehicles.DCVehicle;
import com.driverconnex.vehicles.VehiclesListActivity;
import com.google.gson.JsonObject;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class DCVehilceDataSingleton {

	// Static member holds only one instance of the

	// SingletonExample class

	private static DCVehilceDataSingleton singletonInstance;
	private static ArrayList<String> moduleFromServer = new ArrayList<String>();
	private static ArrayList<Message> messageUpdatedList = new ArrayList<Message>();
	private static ArrayList<DCVehicle> vehiclesList = new ArrayList<DCVehicle>();

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
		return vehiclesList;
	}

	private static void getVehicleList() {
		final ParseUser user = ParseUser.getCurrentUser();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("DCVehicle");

		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
		query.whereEqualTo("vehiclePrivateOwner", user);
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(final List<ParseObject> vehicleList,
					ParseException e) {
				if (e == null) {
					vehiclesList = new ArrayList<DCVehicle>();

					// Loop through all vehicles that we got from the query

					for (int i = 0; i < vehicleList.size(); i++) {
						// Convert parse object (DC Vehicle) into vehicle
						DCVehicle vehicle = ParseUtilities
								.convertVehicle(vehicleList.get(i));

						// Get photo from the parse
						ParseFile photo = (ParseFile) vehicleList.get(i).get(
								"vehiclePhoto");
						byte[] data = null;

						try {
							if (photo != null)
								data = photo.getData();
						} catch (ParseException e1) {
							e1.printStackTrace();
						}

						if (data == null) {
							vehicle.setPhotoSrc(null);
							vehicle.setPhoto(false);
						} else {
							vehicle.setPhotoSrc(data);
							vehicle.setPhoto(true);
						}

						vehiclesList.add(vehicle);
						// vehicleObjects.add(vehicleList.get(i));
					}

					// // Set adapter
					// adapter = new
					// VehicleListAdapter(VehiclesListActivity.this,
					// vehicles, vehiclePicker, addJournyActivity);
					// list.setAdapter(adapter);

				} else {
					Log.e("Get Vehicle", e.getMessage());
				}

				// Disable loading bar
				// loading.setVisibility(View.GONE);
			}
		});
	}
}
package com.driverconnex.singletons;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.location.LocationManager;

import com.baasbox.android.BaasAsset;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.json.JsonObject;
import com.driverconnex.data.Retailer;
import com.google.android.gms.location.LocationClient;

public class DCRetailerSingleton {
	private static DCRetailerSingleton singletonInstance;
	private static LocationManager locationManager;
	private static LocationClient locationClient = null;
	private static ArrayList<Retailer> tempList = new ArrayList<Retailer>();
	private static ArrayList<Retailer> retailersList = new ArrayList<Retailer>();
	private static JSONArray jsonRetailerList = new JSONArray();

	// SingletonExample prevents any other class from instantiating

	private DCRetailerSingleton() {

	}

	// Providing Global point of access

	public static DCRetailerSingleton getDCRetailerSingleeton(
			final Context context) {

		if (null == singletonInstance) {

			singletonInstance = new DCRetailerSingleton();
			System.out.println("with out creating object");
			int size = retailersList.size();

			getRetailer(context);
			// if (moduleMenuList.size() < 1) {
			// getMessage();

			// }
		}

		return singletonInstance;

	}

	public JSONArray getRetailers() {
		// getMessage();

		System.out.println("with out creating object");
		return jsonRetailerList;

	}

	//

	private static void getRetailer(Context context) {

		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		System.out.println(locationManager);
		BaasAsset.fetchData("Retailers", new BaasHandler<JsonObject>() {

			@Override
			public void handle(BaasResult<JsonObject> res) {

				// TODO Auto-generated method stub
				int s = res.value().size();

				try {
					// setWelcomeMsg();
					JsonObject vehicleJson = res.get().get("data");

					System.out.println(vehicleJson.size());
					String jsonString = vehicleJson.toString();
					JSONObject jsonResponse = new JSONObject(jsonString);
					jsonRetailerList = jsonResponse.getJSONArray("meta");
					// for (int i = 0; i < jsonRetailerList.length(); i++) {
					// Retailer retailer = new Retailer();
					// JSONObject retailerJosn = jsonRetailerList
					// .getJSONObject(i);
					// String name = retailerJosn.getString("name");
					// retailer.setName(name);
					// String address1 = retailerJosn.getString("address1");
					// retailer.setAddress(address1);
					//
					// String address2 = retailerJosn.getString("address2");
					// retailer.setAddress2(address2);
					//
					// String town = retailerJosn.getString("town");
					// retailer.setTown(town);
					//
					// String postalCode = retailerJosn.getString("postcode");
					// retailer.setPostalCode(postalCode);
					//
					// String lat = retailerJosn.getString("lat");
					// retailer.setLat(Float.valueOf(lat));
					//
					// String log = retailerJosn.getString("long");
					// retailer.setLog(Float.valueOf(log));
					//
					// System.out.println("company name is ==== " + name + ""
					// + lat + "" + log);
					// Float distancefromHere = distanceFromCurrentLocation(
					// lat, log);
					// retailer.setDistance(distancefromHere);
					//
					// tempList.add(retailer);
					//
					// }

				} catch (Exception e) {
				}

				System.out.println(tempList.size());
			}
		});

	}
}

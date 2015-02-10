package com.driverconnex.basicmodules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.baasbox.android.BaasAsset;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.json.JsonObject;
import com.driverconnex.adapter.ServiceListAdapter;
import com.driverconnex.app.R;
import com.driverconnex.data.Retailer;
import com.driverconnex.data.ServiceListItem;
import com.driverconnex.data.ServiceListItems;
import com.driverconnex.data.XMLModuleConfigParser;
import com.driverconnex.vehicles.ServiceListActivity;

public class ServiceViewActivity extends Activity {
	private ListView list;
	private ArrayList<ServiceListItem> helpItems = new ArrayList<ServiceListItem>();
	private ArrayList<ServiceListItem> listItem = new ArrayList<ServiceListItem>();
	private RelativeLayout loading;
	private RelativeLayout bottomBar;
	ArrayList<ServiceListItems> helpList = new ArrayList<ServiceListItems>();
	private int listIndex;
	private boolean isEmail = false;
	private int listType = 0;
	private LocationManager locationManager;
	ArrayList<Retailer> tempList = new ArrayList<Retailer>();
	ArrayList<Retailer> retailersList = new ArrayList<Retailer>();
	Double startLat = 0d;
	Double startLog = 0d;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_policy);
		list = (ListView) findViewById(R.id.policylist);
		loading = (RelativeLayout) findViewById(R.id.loadSpinner);
		bottomBar = (RelativeLayout) findViewById(R.id.bottom_bar);
		bottomBar.setVisibility(View.GONE);

		list.setOnItemClickListener(itemClickListener);
		if (getIntent().getExtras() != null) {
			listIndex = getIntent().getExtras().getInt("index");
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		getActionBar().setTitle("Retailers");

		// Enable loading bar
		loading.setVisibility(View.VISIBLE);
		// listItem = new ArrayList<ServiceListItem>();
		getRetailer();
		// getHelpFromAsset();
		// displayAdapter();

	}

	/**
	 * Handles what happens when item from the list is clicked/touched
	 */
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long index) {
			{
				Intent returnIntent = new Intent();
				// returnIntent.putExtra("vehicle", vehicles.get(position));
				setResult(RESULT_OK, returnIntent);

				finish();
				overridePendingTransition(R.anim.slide_out, R.anim.slide_in);
			}
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			overridePendingTransition(R.anim.null_anim, R.anim.slide_out);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			Retailer retailer = retailersList.get(position);
			Bundle bundle = new Bundle();
			Intent intent = new Intent(ServiceViewActivity.this,
					ServiceListActivity.class);

			String add = retailer.getAddress();
			System.out.println(add);
			intent.putExtra("name", retailer.getName());
			intent.putExtra("distance", String.valueOf(retailer.getDistance()));
			intent.putExtra("lat", String.valueOf(retailer.getLat()));
			intent.putExtra("log", String.valueOf(retailer.getLog()));
			intent.putExtra("add1", retailer.getAddress());
			intent.putExtra("add2", retailer.getAddress2());
			intent.putExtra("town", retailer.getTown());
			intent.putExtra("postcode", retailer.getPostalCode());
			intent.putExtra("startlat", startLat);
			intent.putExtra("startlog", "startLog");
			startActivity(intent);
			overridePendingTransition(R.anim.slide_left_sub,
					R.anim.slide_left_main);

		}
	};

	private void getHelpFromAsset() {
		loading.setVisibility(View.GONE);
		helpList = XMLModuleConfigParser.getServiceItemsFromXML(
				ServiceViewActivity.this, "service.xml");

		for (int i = 0; i < helpList.size(); i++) {
			ServiceListItem item = new ServiceListItem();
			// item.setCategory(helpList.get(i).getName());
			// item.setCategory(true);
			helpItems.add(item);

			for (int j = 0; j < helpList.get(i).getSubitems().size(); j++) {
				helpItems.add(helpList.get(i).getSubitems().get(j));

			}

		}

	}

	private void displayAdapter() {
		System.out.println();
		listItem = new ArrayList<ServiceListItem>();

		for (int k = 0; k < helpList.get(listIndex).getSubitems().size(); k++) {
			String heading = helpList.get(listIndex).getSubitems().get(k)
					.getPhone();

			ServiceListItem item = new ServiceListItem();
			item.setPhone(helpList.get(listIndex).getSubitems().get(k)
					.getPhone());
			item.setPhonename(helpList.get(listIndex).getSubitems().get(k)
					.getPhonename());
			item.setEmailname(helpList.get(listIndex).getSubitems().get(k)
					.getEmailname());
			item.setEmail(helpList.get(listIndex).getSubitems().get(k)
					.getEmail());
			listItem.add(item);

		}
	}

	private String selectedAnswer(int position) {
		System.out.println();
		listItem = new ArrayList<ServiceListItem>();
		String heading = "";
		for (int k = 0; k < helpList.get(listIndex).getSubitems().size(); k++) {
			heading = helpList.get(listIndex).getSubitems().get(position)
					.getPhone();
			if (heading != null && !heading.equals("")) {

			} else {

				heading = helpList.get(listIndex).getSubitems().get(position)
						.getEmail();
				isEmail = true;
				return heading;

			}
		}
		return heading;

	}

	// get retailer from asset
	private void getRetailer() {
		if (!isConnected(ServiceViewActivity.this)) {

			new AlertDialog.Builder(ServiceViewActivity.this)

					.setTitle("Error")
					.setMessage(
							getResources().getString(
									R.string.connection_not_found))
					.setPositiveButton("Okay",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
									overridePendingTransition(R.anim.null_anim,
											R.anim.slide_out);

								}
							}).show();

			return;

		}

		locationManager = (LocationManager) this
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
					JSONArray jsonRetailerList = jsonResponse
							.getJSONArray("meta");
					int count = 0;
					for (int i = 0; i < jsonRetailerList.length(); i++) {
						Retailer retailer = new Retailer();
						JSONObject retailerJosn = jsonRetailerList
								.getJSONObject(i);
						String name = retailerJosn.getString("name");
						retailer.setName(name);
						String address1 = retailerJosn.getString("address1");
						retailer.setAddress(address1);

						String address2 = retailerJosn.getString("address2");
						retailer.setAddress2(address2);

						String town = retailerJosn.getString("town");
						retailer.setTown(town);

						String postalCode = retailerJosn.getString("postcode");
						retailer.setPostalCode(postalCode);

						String lat = retailerJosn.getString("lat");
						retailer.setLat(Float.valueOf(lat));

						String log = retailerJosn.getString("long");
						retailer.setLog(Float.valueOf(log));

						System.out.println("company name is ==== " + name + ""
								+ lat + "" + log);
						Float distancefromHere = distanceFromCurrentLocation(
								lat, log);
						retailer.setDistance(distancefromHere);

						tempList.add(retailer);

					}

				} catch (Exception e) {
				}

				sorList();
			}
		});

	}

	private Float distanceFromCurrentLocation(String lat, String log) {
		Float latitude = Float.valueOf(lat);
		Float longitude = Float.valueOf(log);
		Criteria crit = new Criteria();
		crit.setAccuracy(Criteria.ACCURACY_FINE);
		crit.setPowerRequirement(Criteria.POWER_LOW);

		// Gets the best matched provider, and only if it's on
		String provider = locationManager.getBestProvider(crit, true);
		Location myLocation = locationManager.getLastKnownLocation(provider);
		if (myLocation == null) {
			myLocation = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		}
		startLat = myLocation.getLatitude();
		startLog = myLocation.getLongitude();
		Location targetLocation = new Location(provider);// provider
															// name
															// is
															// unecessary
		targetLocation.setLatitude(latitude);// your coords of
												// course
		System.out.println(longitude + "" + latitude);
		targetLocation.setLongitude(longitude);
		System.out.println(targetLocation);
		float distanceInMeters = targetLocation.distanceTo(myLocation) / 1000;
		System.out.println(myLocation);
		System.out.println(distanceInMeters);

		return distanceInMeters;
	}

	private ArrayList<Retailer> sorList() {
		System.out.println(tempList.size());

		Collections.sort(tempList);
		for (Retailer retailerObj : tempList) {
			retailersList.add(retailerObj);
			if (retailersList.size() > 14) {
				break;
			}
		}
		loading.setVisibility(View.GONE);

		list.setAdapter(new ServiceListAdapter(ServiceViewActivity.this,
				retailersList));

		return retailersList;
	}

	private static boolean isConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = null;
		if (connectivityManager != null) {
			networkInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		}
		return networkInfo == null ? false : networkInfo.isConnected();
	}
}
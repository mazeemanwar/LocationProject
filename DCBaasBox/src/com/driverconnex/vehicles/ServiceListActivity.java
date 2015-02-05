package com.driverconnex.vehicles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.driverconnex.adapter.ServicesListAdapter;
import com.driverconnex.app.AppConfig;
import com.driverconnex.app.R;
import com.driverconnex.basicmodules.ServiceViewActivity;
import com.driverconnex.data.ServiceListItem;
import com.driverconnex.data.ServiceListItems;
import com.driverconnex.data.XMLModuleConfigParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Activity for displaying list of services and service providers. User chooses
 * a type of service and then they choose a provider, which it will take them to
 * the provider's website.
 * 
 * @author Adrian Klimczak7
 * 
 * 
 */

public class ServiceListActivity extends Activity {
	private ListView list;
	private ArrayList<ServiceListItem> helpItems = new ArrayList<ServiceListItem>();
	private ArrayList<String> listItem = new ArrayList<String>();
	private RelativeLayout loading;
	ArrayList<ServiceListItems> helpList = new ArrayList<ServiceListItems>();
	public static final int CATEGORY = 0;
	public static final int QUESTION = 1;
	public static final int ANSWER = 2;
	private int listType = 0;
	private GoogleMap map;
	private LocationManager locationManager;
	private String provider;
	String lotBundle;
	String logBundle;
	TextView retailerName, distance, adress1, adress2, town, postalCode;
	double dLat;
	double dLong;
	double startLong, startLat;

	private LinearLayout addressLayout = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_retailer);
		// list = (ListView) findViewById(R.id.policylist);
		// loading = (RelativeLayout) findViewById(R.id.loadSpinner);
		// list.setOnItemClickListener(itemClickListener);
		retailerName = (TextView) findViewById(R.id.nameView);
		distance = (TextView) findViewById(R.id.distanceView);

		adress1 = (TextView) findViewById(R.id.address1View);
		adress2 = (TextView) findViewById(R.id.address2View);
		town = (TextView) findViewById(R.id.townView);
		postalCode = (TextView) findViewById(R.id.postalView);
		map = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.mapView)).getMap();
		addressLayout = (LinearLayout) findViewById(R.id.address_layout);
		addressLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println();
				String uri = String
						.format(Locale.ENGLISH,
								"http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)",
								startLat, startLong, ".", dLat, dLong, ".");
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				intent.setClassName("com.google.android.apps.maps",
						"com.google.android.maps.MapsActivity");
				startActivity(intent);

			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		// getCurrentLocationName();
		getActionBar().setTitle("Retailer");
		Bundle bundle = getIntent().getExtras();
		if (getIntent().getExtras() != null) {

			String nameBundle = getIntent().getExtras().getString("name");
			lotBundle = getIntent().getExtras().getString("lat");
			logBundle = getIntent().getExtras().getString("log");
			String distanceBundle = getIntent().getExtras().getString(
					"distance")
					+ " Km";

			String address1Bundle = getIntent().getExtras().getString("add1");
			String address1Bundle2 = getIntent().getExtras().getString("add2");
			String townBundle = getIntent().getExtras().getString("town");
			startLat = getIntent().getExtras().getDouble("startlat");
			startLong = getIntent().getExtras().getDouble("startlog");

			String postCodeBundle = getIntent().getExtras().getString(
					"postcode");
			dLat = Double.valueOf(lotBundle);

			dLong = Double.valueOf(logBundle);
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
					dLat, dLong), 15), 2000, null);
			// MarkerOptions markers = new MarkerOptions().position(
			// new LatLng(dLatitude, dLatitude)).title(nameBundle);
			// //
			// map.addMarker(markers);

			MarkerOptions marker = new MarkerOptions();
			LatLng lat = new LatLng(dLat, dLong);
			marker.position(lat);
			map.addMarker(marker);
			// init();

			retailerName.setText(nameBundle);

			String dis = distanceBundle;
			dis = dis.substring(0, dis.lastIndexOf(".") + 2) + " Miles";
			System.out.println(dis);

			distance.setText(dis);
			adress1.setText(address1Bundle);
			adress2.setText(address1Bundle2);
			town.setText(townBundle);
			postalCode.setText(postCodeBundle);
		}

		// Enable loading bar
		// loading.setVisibility(View.VISIBLE);
		// listItem = new ArrayList<String>();

		// getHelpFromAsset();
		// displayAdapter(0);

	}

	/**
	 * Handles what happens when item from the list is clicked/touched
	 */
	// private OnItemClickListener onItemClickListener = new
	// OnItemClickListener() {
	// @Override
	// public void onItemClick(AdapterView<?> parent, View view, int position,
	// long index) {
	// {
	// Intent returnIntent = new Intent();
	// // returnIntent.putExtra("vehicle", vehicles.get(position));
	// setResult(RESULT_OK, returnIntent);
	//
	// finish();
	// overridePendingTransition(R.anim.slide_right_main,
	// R.anim.slide_right_sub);
	// }
	// }
	// };

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.retailers_menu, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			overridePendingTransition(R.anim.null_anim, R.anim.slide_out);
			return true;
		} else if (item.getItemId() == R.id.retailers_number) {
			Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ AppConfig.getDRIVERLINE()));
			startActivity(callIntent);
		}
		return super.onOptionsItemSelected(item);
	}

	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Bundle bundle = new Bundle();
			bundle.putInt("index", position);
			Intent intent = new Intent(ServiceListActivity.this,
					ServiceViewActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			ServiceListActivity.this.overridePendingTransition(R.anim.slide_in,
					R.anim.null_anim);
		}
	};

	private void getHelpFromAsset() {
		loading.setVisibility(View.GONE);
		helpList = XMLModuleConfigParser.getServiceItemsFromXML(
				ServiceListActivity.this, "service.xml");

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

	private void displayAdapter(int index) {
		System.out.println();
		if (listType == CATEGORY) {
			listItem = new ArrayList<String>();
			for (int i = 0; i < helpList.size(); i++) {
				String heading = helpList.get(i).getName();
				listItem.add(heading);
			}

		} else if (listType == QUESTION) {
			for (int k = 0; k < helpList.get(index).getSubitems().size(); k++) {
				String heading = helpList.get(index).getSubitems().get(k)
						.getPhone();
				listItem.add(heading);

			}
		} else if (listType == ANSWER) {
			for (int k = 0; k < helpList.get(index).getSubitems().size(); k++) {
				String heading = helpList.get(index).getSubitems().get(k)
						.getEmail();
				listItem.add(heading);

			}

		}

		list.setAdapter(new ServicesListAdapter(ServiceListActivity.this,
				listItem, true));

	}

	private void init() {
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
				double dLatitude = Double.valueOf(lotBundle);

				double dLongitude = Double.valueOf(logBundle);
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
						dLatitude, dLongitude), 15), 2000, null);
			} else {
				Toast.makeText(this, "Unable to fetch your current location",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void getCurrentLocationName() {
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

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
		Geocoder geocoder;
		List<Address> addresses;
		geocoder = new Geocoder(this, Locale.getDefault());
		try {

			addresses = geocoder.getFromLocation(myLocation.getAltitude(),
					myLocation.getLongitude(), 1);

			String area = addresses.get(0).getSubAdminArea();
			System.out.println(area);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
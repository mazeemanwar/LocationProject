package com.driverconnex.vehicles;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.driverconnex.adapter.VehicleListAdapter;
import com.driverconnex.app.DriverConnexApp;
import com.driverconnex.app.HomeActivity;
import com.driverconnex.app.R;
import com.driverconnex.journeys.AddJourneyActivity;
import com.driverconnex.journeys.JourneyDetailsActivity;
import com.driverconnex.singletons.DCVehilceDataSingleton;
import com.driverconnex.utilities.ParseUtilities;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Activity for displaying list of vehicles that belong to user. This activity
 * is used to select a default vehicle and display details of the vehicle or
 * pick a vehicle if previous activity requested it by sending "vehiclePicker"
 * to true.
 * 
 * @author Yin Li (SGI)
 * @author Adrian Klimczak
 * @author Muhammad Azeem Anwar
 * 
 */

public class VehiclesListActivity extends Activity {
	private ListView list;
	private RelativeLayout loading;

	private ArrayList<DCVehicle> vehicles = new ArrayList<DCVehicle>();
	private VehicleListAdapter adapter;
	private ArrayList<ParseObject> vehicleObjects = new ArrayList<ParseObject>();

	private boolean vehiclePicker = false;
	private Boolean addJournyActivity = false;
	private boolean isPoolPressed = false;
	private String journeyDetail = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_divider);

		list = (ListView) findViewById(R.id.list);
		loading = (RelativeLayout) findViewById(R.id.loadSpinner);

		list.setOnItemClickListener(onItemClickListener);

		if (getIntent().getExtras() != null) {
			vehiclePicker = getIntent().getExtras().getBoolean("vehiclePicker");
			addJournyActivity = getIntent().getExtras().getBoolean(
					"fromActivity");
			journeyDetail = getIntent().getExtras().getString("key");

		}

		if (journeyDetail != null
				&& journeyDetail.equals("journeyDetailActivity")) {
			// fromActivity = true;

		}
		// if user comes add journey activity subtitle should be disabled
		if (!vehiclePicker && !addJournyActivity)
			getActionBar().setSubtitle("Tap to select a default vehicle");
		//
		// LinearLayout tabBar = (LinearLayout)
		// findViewById(R.id.vehicle_tabBar);
		//
		// tabBar.setVisibility(View.GONE);
		// System.out.println(tabBar);
		// //
		//
		// createTabBar(VehiclesListActivity.this, tabBar, "dc_mileage.xml");
	}

	@Override
	protected void onResume() {
		super.onResume();

		// if (vehicles.size() != 0) {
		// vehicles.clear();
		// }

		// Enable loading bar
		loading.setVisibility(View.VISIBLE);
		getVehiclesByBaasbox();
		// getVehicleByParse();
	}

	/**
	 * Handles what happens when item from the list is clicked/touched
	 */
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long index) {
			// Checks if this activity is not used for picking a vehicle
			if (!vehiclePicker) {
				for (int i = 0; i < parent.getChildCount(); i++) {
					TextView tag = (TextView) parent.getChildAt(i)
							.findViewById(R.id.defaultTag);
					if (i == position) {

						tag.setVisibility(View.VISIBLE);
						String defaultReg = vehicles.get(position)
								.getRegistration();
						DriverConnexApp.getUserPref().setDefaultVehicleReg(
								defaultReg);
						DriverConnexApp.getUserPref().updateSharedPreferences();
						System.out.println(defaultReg);
						// ParseUser.getCurrentUser().put("userDefaultVehicle",
						// vehicleObjects.get(position));
						// ParseUser.getCurrentUser().saveInBackground();
						// // here we can set the behaviour of on press if user
						// comes from add journey.
						if (addJournyActivity) {
							Intent returnIntent = new Intent();

							returnIntent.putExtra("vehicleReg",
									vehicles.get(position).getRegistration());
							setResult(RESULT_OK, returnIntent);
							DriverConnexApp.getUserPref().setDefaultVehicleReg(
									vehicles.get(i).getRegistration());
							finish();
							overridePendingTransition(R.anim.slide_right_main,
									R.anim.slide_right_sub);

						} else if (journeyDetail
								.equals("journeyDetailActivity")) {
							Intent returnIntent = new Intent();
							String veh = vehicles.get(i).getRegistration();
							System.out.println(veh);
							DriverConnexApp.getUserPref().setDefaultVehicleReg(
									vehicles.get(i).getRegistration());
							returnIntent.putExtra("vehicleReg",
									vehicles.get(position).getRegistration());
							setResult(RESULT_OK, returnIntent);

							finish();
							overridePendingTransition(R.anim.slide_right_main,
									R.anim.slide_right_sub);
						}
					} else
						tag.setVisibility(View.INVISIBLE);
				}
			}
			// Otherwise, picks the vehicle and goes back to previous screen
			else {
				Intent returnIntent = new Intent();
				returnIntent.putExtra("vehicle", vehicles.get(position));
				setResult(RESULT_OK, returnIntent);

				finish();
				overridePendingTransition(R.anim.slide_right_main,
						R.anim.slide_right_sub);
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!vehiclePicker) {
			MenuInflater inflater = getMenuInflater();
			// if user comes add journey add vehicle button should disable

			if (!addJournyActivity) {
				inflater.inflate(R.menu.action_save, menu);

			}
			// inflater.inflate(R.menu.vehicle, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			// Checks if this activity is not used for picking a vehicle
			if (!vehiclePicker) {
				// When user will come to this activity from an activity where
				// they add a vehicle
				// finish() would make them come back to that activity, which is
				// incorrect.
				// Instead let it launch intent to be 100 % sure that it will
				// take user to HomeActivity.
				// if user comes add journey activity should return that screen
				if (addJournyActivity) {
					Intent intent = new Intent(VehiclesListActivity.this,
							AddJourneyActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);

					// finish();

					overridePendingTransition(R.anim.null_anim,
							R.anim.slide_out);

				} else if (journeyDetail.equals("journeyDetailActivity")) {
					Intent intent = new Intent(VehiclesListActivity.this,
							JourneyDetailsActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);

					// finish();

					overridePendingTransition(R.anim.null_anim,
							R.anim.slide_out);
				} else {
					Intent intent = new Intent(VehiclesListActivity.this,
							HomeActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);

					// finish();

					overridePendingTransition(R.anim.null_anim,
							R.anim.slide_out);
				}
			}
			// Otherwise it finish this activity an go to previous one
			else {
				finish();
				overridePendingTransition(R.anim.slide_right_main,
						R.anim.slide_right_sub);
			}

			return true;
		} else if (item.getItemId() == R.id.action_save) {
			Intent intent = new Intent(VehiclesListActivity.this,
					AddVehicleActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_left_sub,
					R.anim.slide_left_main);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Gets list of vehicles from the Parse database.
	 */

	private void getVehicleByParse() {
		vehicles = new ArrayList<DCVehicle>();
		final ParseUser user = ParseUser.getCurrentUser();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("DCVehicle");

		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
		query.whereEqualTo("vehiclePrivateOwner", user);
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(final List<ParseObject> vehicleList,
					ParseException e) {
				if (e == null) {
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

						vehicles.add(vehicle);
						vehicleObjects.add(vehicleList.get(i));
					}

					// Set adapter
					adapter = new VehicleListAdapter(VehiclesListActivity.this,
							vehicles, vehiclePicker, addJournyActivity);
					list.setAdapter(adapter);
				} else {
					Log.e("Get Vehicle", e.getMessage());
				}

				// Disable loading bar
				loading.setVisibility(View.GONE);
			}
		});
	}

	private void getVehiclesByBaasbox() {
		loading.setVisibility(View.GONE);
		vehicles = DCVehilceDataSingleton.getDCModuleSingleton(
				VehiclesListActivity.this).getVehilesList();
		System.out.println(vehicles);
		adapter = new VehicleListAdapter(VehiclesListActivity.this, vehicles,
				vehiclePicker, addJournyActivity);
		list.setAdapter(adapter);

		// final BaasQuery PREPARED_QUERY = BaasQuery.builder()
		// .collection("BAAVehicle")
		// // .projection("field","aggreateOp")
		// // .where("Author").whereParams("m.azeemanwar@gmail.com")
		// // .groupBy("field")
		// // .orderBy("field asc")
		// // .pagination(2,20)
		// .build();
		// // then
		// PREPARED_QUERY.query(new BaasHandler<List<JsonObject>>() {
		//
		// @Override
		// public void handle(BaasResult<List<JsonObject>> res) {
		// int a = res.value().size();
		// // Loop through all vehicles that we got from the query
		// for (int i = 0; i < res.value().size(); i++) {
		//
		// DCVehicle vehicleReturn = BaasboxUtilities
		// .convertVehicle(res.value().get(i));
		//
		// vehicles.add(vehicleReturn);
		// }
		// // TODO Auto-generated method stub
		// adapter = new VehicleListAdapter(VehiclesListActivity.this,
		// vehicles, vehiclePicker, addJournyActivity);
		// list.setAdapter(adapter);
		// loading.setVisibility(View.GONE);
		//
		// System.out.println("" + vehiclePicker + addJournyActivity);
		//
		// }
		// });
	}

	private void getVehicleFromServer() {
		vehicles = new ArrayList<DCVehicle>();
		final ParseUser user = ParseUser.getCurrentUser();
		ParseObject userOrganisation = user.getParseObject("userOrganisation");
		ParseObject userGroup = user.getParseObject("userGroup");
		ParseQuery<ParseObject> query = ParseQuery.getQuery("DCVehicle");

		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
		// query.whereEqualTo("vehiclePrivateOwner", user);
		if (isPoolPressed) {
			query.whereEqualTo("vehicleGroup", userGroup);
			query.whereEqualTo("vehicleIsPool", true);
			query.whereEqualTo("vehicleOrganisation", userOrganisation);
			isPoolPressed = false;
		} else {
			query.whereEqualTo("vehiclePrivateOwner", user);
			query.whereEqualTo("vehicleGroup", userGroup);
			query.whereEqualTo("vehicleIsPool", false);
			query.whereEqualTo("vehicleOrganisation", userOrganisation);
		}
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(final List<ParseObject> vehicleList,
					ParseException e) {
				if (e == null) {
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

						vehicles.add(vehicle);
						vehicleObjects.add(vehicleList.get(i));
					}

					// Set adapter
					adapter = new VehicleListAdapter(VehiclesListActivity.this,
							vehicles, vehiclePicker, addJournyActivity);
					list.setAdapter(adapter);

				} else {
					Log.e("Get Vehicle", e.getMessage());
				}
				// Disable loading bar
				loading.setVisibility(View.GONE);
			}
		});

	}

}

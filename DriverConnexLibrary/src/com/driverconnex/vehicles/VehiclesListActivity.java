package com.driverconnex.vehicles;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.driverconnex.adapter.VehicleListAdapter;
import com.driverconnex.app.DriverConnexApp;
import com.driverconnex.app.HomeActivity;
import com.driverconnex.app.R;
import com.driverconnex.data.Tab;
import com.driverconnex.data.XMLModuleConfigParser;
import com.driverconnex.journeys.AddJourneyActivity;
import com.driverconnex.journeys.JourneyDetailsActivity;
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
			journeyDetail = getIntent().getExtras().getString("key");

			addJournyActivity = getIntent().getExtras().getBoolean(
					"fromActivity");
			// comes from journey detail

		}

		if (journeyDetail != null
				&& journeyDetail.equals("journeyDetailActivity")) {
			// fromActivity = true;

		}
		// if user comes add journey activity subtitle should be disabled
		if (!vehiclePicker && !addJournyActivity)
			getActionBar().setSubtitle("Tap to select a default vehicle");
		//
		LinearLayout tabBar = (LinearLayout) findViewById(R.id.vehicle_tabBar);
		System.out.println(tabBar);

		createTabBar(VehiclesListActivity.this, tabBar, "dc_mileage.xml");
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (vehicles.size() != 0) {
			vehicles.clear();
		}

		// Enable loading bar
		loading.setVisibility(View.VISIBLE);
		getVehicleByParse();
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
						ParseUser.getCurrentUser().put("userDefaultVehicle",
								vehicleObjects.get(position));
						ParseUser.getCurrentUser().saveInBackground();
						// here we can set the behaviour of on press if user
						// comes from add journey.
						if (addJournyActivity) {
							Intent returnIntent = new Intent();
							DriverConnexApp.getUserPref().setDefaultVehicleReg(
									vehicles.get(position).getRegistration());
							returnIntent.putExtra("vehicleReg",
									vehicles.get(position).getRegistration());
							setResult(RESULT_OK, returnIntent);

							finish();
							overridePendingTransition(R.anim.slide_right_main,
									R.anim.slide_right_sub);

						} else if (journeyDetail
								.equals("journeyDetailActivity")) {
							DriverConnexApp.getUserPref().setDefaultVehicleReg(
									vehicles.get(position).getRegistration());
							Intent intent = new Intent(
									VehiclesListActivity.this,
									JourneyDetailsActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);

							// finish();

							overridePendingTransition(R.anim.null_anim,
									R.anim.slide_out);
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

	public void createTabBar(final Context context, LinearLayout tabBar,
			String path) {
		// Get tabs from XML file
		final ArrayList<Tab> tabs = XMLModuleConfigParser.getTabsFromXML(
				context, path);

		int piority = 0;
		int tabIndex = 0;
		boolean added[] = new boolean[tabs.size()];

		// Loops through all tabs we got
		for (int i = 0; i < tabs.size(); i++) {
			// Gets priority of the tab
			piority = tabs.get(i).getPriority();

			// Gets the index of tab with the highest priority
			tabIndex = i;

			// Loops again through all tabs
			for (int j = 0; j < tabs.size(); j++) {
				// Compares priorities and makes sure tab has not been yet added
				System.out.println("getpriority =  "
						+ tabs.get(j).getPriority() + "added? = " + added[j]);
				if (piority > tabs.get(j).getPriority() && !added[j]) {
					// This tab has higher priority, so it should be displayed
					// before the previous one
					piority = tabs.get(j).getPriority();
					tabIndex = j;
				}
			}

			added[tabIndex] = true;

			// Creates layout for the tab
			LinearLayout tab = new LinearLayout(context);

			// Creates icon for the tab
			LinearLayout imageLayout = new LinearLayout(context);
			imageLayout.setGravity(Gravity.CENTER_HORIZONTAL);
			ImageView icon = new ImageView(context);

			icon.setImageResource(context.getResources().getIdentifier(
					tabs.get(tabIndex).getIcon(), "drawable",
					context.getPackageName()));

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			// lp.setMargins(25, 15, 25, 0);
			lp.setMargins(15, 10, 15, 0);
			icon.setLayoutParams(lp);
			imageLayout.addView(icon);

			// Creates title for the tab
			LinearLayout textLayout = new LinearLayout(context);
			textLayout.setGravity(Gravity.CENTER_HORIZONTAL);
			TextView title = new TextView(context);
			title.setText(tabs.get(tabIndex).getName());

			System.out.println("title is " + tabs.get(tabIndex).getName());
			// Determines screen size and sets size of the title to match the
			// screen
			if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
				title.setTextSize(18);
			} else if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
				title.setTextSize(11);
			} else if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
				title.setTextSize(9);
			} else {
				title.setTextSize(11);
			}
			title.setTextColor(context.getResources().getColor(R.color.white));

			textLayout.addView(title);

			// Creates tab
			LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 0, 0.60f);
			LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 0, 0.30f);
			titleParams.setMargins(0, 3, 0, 3);

			tab.setOrientation(LinearLayout.VERTICAL);
			tab.addView(imageLayout, iconParams);
			tab.addView(textLayout, titleParams);

			final int tabIndexFinal = tabIndex;

			// Sets listener for the tab. When it's clicked it will open
			// assigned to the tab activity
			tab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					System.out.println(tabs.get(tabIndexFinal).getName()
							+ " just clicked");
					String tabClicked = tabs.get(tabIndexFinal).getName();
					if (tabClicked.equals("Company")) {
						getVehicleFromServer();
					} else if (tabClicked.equals("Pool")) {
						isPoolPressed = true;
						getVehicleFromServer();
					} else {
						getVehicleByParse();
					}
					// ModulesUtilities moduleUtil = new
					// ModulesUtilities(context);

					/**
					 * i have a library project with parse.com. when
					 */
					//
					// // Gets the tab's activity
					// Intent intent = new Intent(context, moduleUtil
					// .getModuleClass(tabs.get(tabIndexFinal).getName()));
					//
					// // Starts the activity
					// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					// context.startActivity(intent);
					// ((Activity) context).overridePendingTransition(
					// R.anim.slide_in, R.anim.null_anim);
				}
			});
			// set for equal space
			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0,
					LinearLayout.LayoutParams.MATCH_PARENT, 1);
			// Add tabs to the tab bar
			tabBar.addView(tab, param);

		}
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

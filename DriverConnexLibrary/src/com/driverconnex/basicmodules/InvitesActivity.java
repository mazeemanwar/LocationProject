package com.driverconnex.basicmodules;

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

import com.driverconnex.adapter.VehicleListAdapter;
import com.driverconnex.app.HomeActivity;
import com.driverconnex.app.LoginActivity;
import com.driverconnex.app.R;
import com.driverconnex.app.SignInActivity;
import com.driverconnex.utilities.ParseUtilities;
import com.driverconnex.vehicles.DCVehicle;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * 
 * 
 * 
 * @author Muhammad Azeem Anwar
 * 
 */
public class InvitesActivity extends Activity {
	private ListView list;
	private RelativeLayout loading;

	private ArrayList<DCVehicle> vehicles = new ArrayList<DCVehicle>();
	private VehicleListAdapter adapter;
	private ArrayList<ParseObject> vehicleObjects = new ArrayList<ParseObject>();

	private boolean vehiclePicker = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invites);

		list = (ListView) findViewById(R.id.list);
		loading = (RelativeLayout) findViewById(R.id.loadSpinner);

		list.setOnItemClickListener(onItemClickListener);

		if (getIntent().getExtras() != null) {
			vehiclePicker = getIntent().getExtras().getBoolean("Invites");
		}

		if (!vehiclePicker)
			getActionBar().setSubtitle("Select an Organisation to join");
	}

	@Override
	protected void onResume() {
		super.onResume();

		// if (vehicles.size() != 0) {
		// vehicles.clear();
		// }
		//
		// // Enable loading bar
		// loading.setVisibility(View.VISIBLE);
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
			// if(!vehiclePicker)
			// {
			// for (int i = 0; i < parent.getChildCount(); i++)
			// {
			// TextView tag = (TextView)
			// parent.getChildAt(i).findViewById(R.id.defaultTag);
			// if (i == position)
			// {
			// tag.setVisibility(View.VISIBLE);
			// ParseUser.getCurrentUser().put("userDefaultVehicle",
			// vehicleObjects.get(position));
			// ParseUser.getCurrentUser().saveInBackground();
			// }
			// else
			// tag.setVisibility(View.INVISIBLE);
			// }
			// }
			// Otherwise, picks the vehicle and goes back to previous screen
			// else
			// {
			// Intent returnIntent = new Intent();
			// returnIntent.putExtra("vehicle", vehicles.get(position));
			// setResult(RESULT_OK, returnIntent);
			//
			//
			// finish();
			// overridePendingTransition(R.anim.slide_right_main,
			// R.anim.slide_right_sub);
			// }
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!vehiclePicker) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.action_invites, menu);
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
				Intent intent = new Intent(InvitesActivity.this,
						SignInActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);

				// finish();

				overridePendingTransition(R.anim.null_anim, R.anim.slide_out);
			}
			// Otherwise it finish this activity an go to previous one
			else {
				finish();
				overridePendingTransition(R.anim.slide_right_main,
						R.anim.slide_right_sub);
			}

			return true;
		} else if (item.getItemId() == R.id.action_plus) {
			// Intent intent = new Intent(InvitesActivity.this,
			// AddVehicleActivity.class);
			// startActivity(intent);
			// overridePendingTransition(R.anim.slide_left_sub,
			// R.anim.slide_left_main);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Gets list of vehicles from the Parse database.
	 */
	private void getVehicleByParse() {
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
					adapter = new VehicleListAdapter(InvitesActivity.this,
							vehicles, vehiclePicker, false);
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

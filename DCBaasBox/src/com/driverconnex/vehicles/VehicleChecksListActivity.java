package com.driverconnex.vehicles;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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

import com.driverconnex.adapter.ListAdapter;
import com.driverconnex.adapter.ListAdapterItem;
import com.driverconnex.app.DriverConnexApp;
import com.driverconnex.app.R;
import com.driverconnex.data.XMLVehicleChecksParser;
import com.driverconnex.singletons.DCVehicleSingleton;
import com.driverconnex.utilities.ThemeUtilities;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Activity for displaying a list of vehicle checks and items of the individual
 * check. When user clicks on the vehicle check it will display items that
 * belong to that check in the same activity.
 * 
 * @author Adrian Klimczak
 * 
 */

public class VehicleChecksListActivity extends Activity {
	private ListAdapter adapter;
	private ArrayList<VehicleCheck> vehicleChecks; // Vehicle checks
	private ArrayList<String[]> items; // Items of the vehicle check
	private ArrayList<ListAdapterItem> adapterData = new ArrayList<ListAdapterItem>(); // Data
																						// used
																						// for
																						// the
																						// adapter

	private boolean displayItems = false;
	private int countChecks = 0;
	private String vehicleId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		ListView list = (ListView) findViewById(R.id.list);

		list.setOnItemClickListener(onItemClickListener);
		adapter = new ListAdapter(VehicleChecksListActivity.this, adapterData);
		list.setAdapter(adapter);

		// We will get some extras if we launched this activity again, from
		// vehicleChecks list to vehicleChecks
		// items list
		if (getIntent().getExtras() != null) {
			items = (ArrayList<String[]>) getIntent().getExtras()
					.getSerializable("items");
			vehicleId = getIntent().getExtras().getString("vehicleID");

			// Indicate that we are in items list now
			if (items != null)
				displayItems = true;
		}

		// Otherwise we are displaying list of services
		if (!displayItems) {
			// Get services from the file
			vehicleChecks = XMLVehicleChecksParser
					.getVehicleChecksFromXML(this);
		}

		// Prepare data for the adapter
		// --------------------------------
		// Check if it displays items of the vehicle check
		if (displayItems) {
			if (!adapterData.isEmpty())
				adapterData.clear();

			for (int i = 0; i < items.size(); i++) {
				ListAdapterItem item = new ListAdapterItem();
				item.title = items.get(i)[0];
				item.subtitle = "Incomplete";
				adapterData.add(item);
			}

			// There is no icon for this list's items
			adapter.setImageResource(-1);
		}
		// Otherwise it displays vehicle checks
		else {
			if (!adapterData.isEmpty())
				adapterData.clear();

			for (int i = 0; i < vehicleChecks.size(); i++) {
				ListAdapterItem item = new ListAdapterItem();
				item.title = vehicleChecks.get(i).getName();
				item.subtitle = "";

				adapterData.add(item);
			}

			adapter.setImageResource(R.drawable.ic_294);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!displayItems) {
			if (!DriverConnexApp.getUserPref()
					.getMonthlyVehicleCheckExpiryDate(vehicleId).isEmpty()) {
				// Check if vehicle check expired
				if (DCVehicleSingleton.isMonthlyCheckExired(vehicleId)) {
					adapterData.get(0).subtitle = "Check expired: "
							+ DriverConnexApp
									.getUserPref()
									.getMonthlyVehicleCheckExpiryDate(vehicleId);
				} else {
					adapterData.get(0).subtitle = "Check valid until: "
							+ DriverConnexApp
									.getUserPref()
									.getMonthlyVehicleCheckExpiryDate(vehicleId);
				}

				adapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 100) {
			// Make sure the request was successful
			if (resultCode == RESULT_OK) {
				boolean passedCheck = data
						.getBooleanExtra("passedCheck", false);
				int checkIndex = data.getIntExtra("checkIndex", -1);

				if (checkIndex != -1) {
					if (!adapterData.get(checkIndex).subtitle
							.equals("Complete")
							&& !adapterData.get(checkIndex).subtitle
									.equals("Failed"))
						countChecks++;

					if (passedCheck) {
						adapterData.get(checkIndex).subtitle = "Complete";
						adapterData.get(checkIndex).subtitleColor = ThemeUtilities
								.getGreenTextColor();
					} else {
						adapterData.get(checkIndex).subtitle = "Failed";
						adapterData.get(checkIndex).subtitleColor = ThemeUtilities
								.getRedTextColor();
					}

					adapter.notifyDataSetChanged();
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (displayItems) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.action_save, menu);
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			overridePendingTransition(R.anim.slide_right_main,
					R.anim.slide_right_sub);
			return true;
		} else if (item.getItemId() == R.id.action_save) {
			// Checks if user checked all items before saving
			if (countChecks < items.size()) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						VehicleChecksListActivity.this);
				builder.setTitle(R.string.title_error);
				builder.setMessage(R.string.vehicle_check_error);
				builder.setPositiveButton(android.R.string.ok,
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						});
				builder.create();
				builder.show();
			} else {
				Calendar c = Calendar.getInstance();
				c.set(Calendar.MONTH, c.get(Calendar.MONTH) + 1);

				SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
				final String expiryDate = format.format(c.getTime());

				// Set expire date in the shared preferences
				DriverConnexApp.getUserPref().setMonthlyVehicleCheckExpiryDate(
						expiryDate, vehicleId);

				// Save vehicle last checked date in the Parse
				// ParseQuery<ParseObject> query =
				// ParseQuery.getQuery("DCVehicle");
				//
				// query.getInBackground(vehicleId, new
				// GetCallback<ParseObject>()
				// {
				// @Override
				// public void done(ParseObject vehicle, ParseException e)
				// {
				// if(e == null)
				// {
				// vehicle.put("vehicleLastChecked",
				// Calendar.getInstance().getTime());
				// vehicle.saveInBackground();
				// }
				// else
				// Log.e("get vehicle", e.getMessage());
				// }
				// });
				//
				finish();
				overridePendingTransition(R.anim.slide_right_main,
						R.anim.slide_right_sub);
			}
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * On click listener for the items in the list
	 */
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				final int position, long id) {
			if (!displayItems) {
				// Ask user for confirmation
				AlertDialog.Builder builder = new AlertDialog.Builder(
						VehicleChecksListActivity.this);

				builder.setTitle("Start Check")
						.setMessage(
								getResources().getString(
										R.string.vehicle_new_check))
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Intent intent = new Intent(
												VehicleChecksListActivity.this,
												VehicleChecksListActivity.class);

										Bundle extras = new Bundle();
										extras.putSerializable("items",
												vehicleChecks.get(position)
														.getItems());
										extras.putString("vehicleID", vehicleId);

										intent.putExtras(extras);

										startActivity(intent);
										overridePendingTransition(
												R.anim.slide_left_sub,
												R.anim.slide_left_main);
									}
								})
						.setNegativeButton(android.R.string.cancel,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								});

				builder.show();
			}
			// Otherwise some item of the check was clicked
			else {
				Intent intent = new Intent(VehicleChecksListActivity.this,
						VehicleCheckActivity.class);

				Bundle extras = new Bundle();
				extras.putSerializable("item", items.get(position));
				extras.putInt("checkIndex", position);
				intent.putExtras(extras);

				startActivityForResult(intent, 100);
				overridePendingTransition(R.anim.slide_left_sub,
						R.anim.slide_left_main);
			}
		}
	};
}

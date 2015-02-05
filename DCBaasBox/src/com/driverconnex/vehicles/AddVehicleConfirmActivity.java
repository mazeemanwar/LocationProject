package com.driverconnex.vehicles;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasQuery;
import com.baasbox.android.BaasResult;
import com.baasbox.android.json.JsonObject;
import com.baasbox.android.net.HttpRequest;
import com.driverconnex.app.R;

/**
 * Activity for confirming adding a new vehicle by the user. It displays
 * information about the car. If user confirms, then it will save the vehicle to
 * Parse database and it will take him to details activity of the vehicle he
 * added.
 * 
 * @author Yin Li (SGI)
 * @author Adrian Klimczak
 * @author Muhammad Azeem Anwar
 * 
 */

public class AddVehicleConfirmActivity extends Activity {
	private int[] textIDs = { R.id.makeText, R.id.modelText,
			R.id.derivativeText, R.id.ccText, R.id.bhpText, R.id.bodyText,
			R.id.gearboxText, R.id.feulText, R.id.exturbText, R.id.commgpText,
			R.id.sixmonthrflText, R.id.yearrflText, R.id.co2Text };

	private DCVehicle vehicle;

	private RelativeLayout loading;
	private String dictionary;

	// final ParseObject mVehicle = new ParseObject("DCVehicle");
	// final ParseUser user = ParseUser.getCurrentUser();
	private Boolean isPrivate = false;

	// ParseObject usersGroup = null;
	// ParseObject usersOrganisation = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vehicle_add_confirm);

		loading = (RelativeLayout) findViewById(R.id.loadSpinner);

		Bundle extras = getIntent().getExtras();
		if (extras != null)

		{
			vehicle = (DCVehicle) extras.getSerializable("vehicle");
			dictionary = extras.getString("jsonDictionary");

			for (int i = 0; i < textIDs.length; i++) {
				TextView textView = (TextView) findViewById(textIDs[i]);
				if (vehicle.getAddValuesByOrder(i) != null) {
					textView.setText(vehicle.getAddValuesByOrder(i).toString());
				}
			}
			System.out.println();
			// fetchUserData();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_confirm, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			overridePendingTransition(R.anim.slide_right_main,
					R.anim.slide_right_sub);
			return true;
		} else if (item.getItemId() == R.id.action_confirm) {
			loading.setVisibility(View.VISIBLE);

			// BaasUser user = BaasUser.current();
			//
			// String a = user.getName();
			// System.out.println(a);
			// getVehiclesByBaasbox();
			// System.out.println(dictionary);
			String regID = dictionary.trim().replaceAll("\\s+", "");
			regID = regID.replace("\\n+", "");
			System.out.println(regID);
			JsonObject vehicleDictionary = new JsonObject();
			//
			vehicleDictionary.put("MAKE", vehicle.getMake());
			vehicleDictionary.put("MODEL", vehicle.getModel());

			vehicleDictionary
					.put("MODEL_VARIANT_NAME", vehicle.getDerivative());
			vehicleDictionary.put("BODY", vehicle.getBody());

			vehicleDictionary.put("GEARBOX_TYPE", vehicle.getGearBox());
			vehicleDictionary.put("FUEL", vehicle.getFuel());
			vehicleDictionary.put("CO2", vehicle.getCo2Value());
			vehicleDictionary.put("COLOUR", vehicle.getColour());

			vehicleDictionary.put("COMBINED_MPG", vehicle.getComMPG());
			vehicleDictionary.put("CT_MARKER", 0);
			vehicleDictionary.put("DATASET", "");
			vehicleDictionary.put("DOOR_COUNT", vehicle.getDoorCount());

			vehicleDictionary.put("DVLA ", "HEAVY OIL");
			vehicleDictionary.put("ENGINE_SIZE", vehicle.getEngineSize());
			vehicleDictionary.put("EXTRA_URBAN_MPG", vehicle.getExturbMPG());
			vehicleDictionary.put("FIRST_YEAR ", vehicle.getYearRFL());

			// vehicleDictionary.put("lookup", dictionary);
			vehicleDictionary.put("REGISTRATION", vehicle.getRegistration());
			vehicleDictionary.put("ANNUAL_MILEAGE", vehicle.getAnnualMileage());
			vehicleDictionary.put("ODOMETER_READING",
					vehicle.getCurrentMileage());
			System.out.println(vehicleDictionary);

			HashMap<String, Object> params = new HashMap<String, Object>();

			BaasBox box = BaasBox.getDefault();

			box.rest(HttpRequest.POST, "plugin/dc.addVehicle",
					vehicleDictionary, true, new BaasHandler<JsonObject>() {
						@Override
						public void handle(BaasResult<JsonObject> res) {
							System.out.println();
							loading.setVisibility(View.INVISIBLE);
							Intent intent = new Intent(
									AddVehicleConfirmActivity.this,
									VehicleDetailsActivity.class);

							Bundle extras = new Bundle();
							extras.putSerializable("vehicle", vehicle);
							extras.putBoolean("vehicleAdded", true);
							intent.putExtras(extras);

							startActivity(intent);
							overridePendingTransition(R.anim.slide_in,
									R.anim.null_anim);
						}
					});
			// new AlertDialog.Builder(AddVehicleConfirmActivity.this)
			// .setTitle("Vehicle Type")
			// .setPositiveButton("Private",
			// new DialogInterface.OnClickListener() {
			// public void onClick(DialogInterface dialog,
			// int which) {
			// System.out.println();
			// isPrivate = true;
			// // addFriend(position);
			// new UpdateTask().execute();
			// }
			// })
			// .setNegativeButton("Group",
			// new DialogInterface.OnClickListener() {
			// @Override
			// public void onClick(DialogInterface dialog,
			// int which) {
			// new UpdateTask().execute();
			// }
			// }).show();

		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Passes data about vehicle to Parse database
	 * 
	 */
	private class UpdateTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			loading.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {
			// // Get current logged in user
			//
			// // Create a new DC Vehicle object
			//
			// // Put data into Parse database
			// ArrayList<Map<String, String>> arr = new ArrayList<Map<String,
			// String>>();
			//
			// mVehicle.put("vehiclePrivateOwner", user);
			//
			// if (vehicle.getInfodump() != null) {
			// arr.add(vehicle.getInfodump());
			// mVehicle.put("vehicleInfoDump", arr);
			// }
			//
			// mVehicle.put("vehicleOdometer", vehicle.getCurrentMileage());
			// System.out
			// .println("CURRENT MILAGE =" + vehicle.getCurrentMileage());
			//
			// if (vehicle.getAnnualMileage() > 0)
			// mVehicle.put("vehicleAnnualMileage", vehicle.getAnnualMileage());
			// else
			// mVehicle.put("vehicleAnnualMileage", 10000);
			// System.out.println("ANNUAL MILAGE =" +
			// vehicle.getAnnualMileage());
			//
			// if (vehicle.getRegistration() != null)
			// mVehicle.put("vehicleRegistration", vehicle.getRegistration());
			// System.out.println("REGISTRAION ID =" +
			// vehicle.getRegistration());
			// mVehicle.put("vehicleAddedBy", user);
			// if (isPrivate) {
			// mVehicle.put("vehiclePrivateOwner", user);
			// } else {
			// // ParseObject usersGroup = user.getParseObject("userGroup");
			// // System.out.println("USER  ="+user.getObjectId());`
			// // String usersGroups = user.getString("userFirsName");
			// // ParseObject usersGroups =
			// // mVehicle.getParseObject("vehicleAddedBy");
			// if ((usersGroup != null) && (usersOrganisation != null)) {
			//
			// mVehicle.put("vehicleIsPool", isPrivate);
			// // System.out.println("value = " + usersGroups + " org ="
			// // + usersOrganisation);
			// mVehicle.put("vehicleGroup", usersGroup);
			//
			// mVehicle.put("vehicleOrganisation", usersOrganisation);
			// }
			// }
			// // Save the object and put it into Parse database
			// mVehicle.saveInBackground(new SaveCallback() {
			// @Override
			// public void done(ParseException e) {
			// if (e == null) {
			// // We are passing vehicle to next activity, we need id
			// // of the vehicle
			// vehicle.setId(mVehicle.getObjectId());
			// updateUserVehicle(mVehicle.getObjectId());
			// loading.setVisibility(View.INVISIBLE);
			// Intent intent = new Intent(
			// AddVehicleConfirmActivity.this,
			// VehicleDetailsActivity.class);
			//
			// Bundle extras = new Bundle();
			// extras.putSerializable("vehicle", vehicle);
			// extras.putBoolean("vehicleAdded", true);
			// intent.putExtras(extras);
			//
			// startActivity(intent);
			// overridePendingTransition(R.anim.slide_in,
			// R.anim.null_anim);
			// } else
			// Log.e("save new DCVehicle", e.getMessage());
			// }
			// });
			//
			// try {
			// user.save();
			// } catch (ParseException e1) {
			//
			// e1.printStackTrace();
			// }
			//
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}
	}

	// fetch user data in start because for some how user retung null when we
	// try to get directy user.getUserObject
	private void fetchUserData() {
		// user.fetchInBackground(new GetCallback<ParseObject>() {
		// public void done(ParseObject object, ParseException e) {
		// if (e == null) {
		// usersGroup = object.getParseObject("userGroup");
		// usersOrganisation = object
		// .getParseObject("userOrganisation");
		//
		// } else {
		// System.out.println();
		// // Failure!
		// }
		// }
		// });

	}

	private void updateUserVehicle(final String userVehicle) {
		// ParseUser user = ParseUser.getCurrentUser();
		// user.put("userDefaultVehicle", mVehicle);
		// user.saveInBackground(new SaveCallback() {
		//
		// @Override
		// public void done(ParseException ee) {
		// // TODO Auto-generated method stub
		// if (ee == null) {
		//
		// System.out.println(ee);
		// } else {
		// System.out.println(ee);
		// }
		// }
		// });
	}

	// for private user
	private void getVehiclesByBaasbox() {
		final BaasQuery GETVEHICLES = BaasQuery.builder()
				.collection("BAAVehicle")

				.build();
		// then
		GETVEHICLES.query(new BaasHandler<List<JsonObject>>() {

			@Override
			public void handle(BaasResult<List<JsonObject>> res) {

				// TODO Auto-generated method stub
				JsonObject tObject = res.value().get(1);
				String make = res.value().get(1).getString("vehicleMake");
				System.out.println(make);

				System.out.println(res);
			}
		});

		// BaasUser user = BaasUser.current();
		// System.out.println(user);
		// BaasDocument.fetchAll("BAAVehicle", onRefresh);
		// }
		//
		// private final BaasHandler<List<BaasDocument>> onRefresh = new
		// BaasHandler<List<BaasDocument>>() {
		// @Override
		// public void handle(BaasResult<List<BaasDocument>> result) {
		//
		// // mListFragment.refresh(result.get());
		// System.out.println(result);
		// }
	};

}

package com.driverconnex.vehicles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.driverconnex.app.DriverConnexApp;
import com.driverconnex.app.R;
import com.driverconnex.utilities.Utilities;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Activity for adding a new vehicle. It takes registration number from the
 * user, then it checks if vehicle has not been already registered in the
 * database. If it wasn't it will go to confirmation activity.
 * 
 * @author Yin Li (SGI)
 * @author Adrian Klimczak
 * @author Muhammad Azeem Anwar
 * 
 */

public class AddVehicleActivity extends Activity {
	private EditText regNumberEdit;
	private EditText currentMileageEdit;
	private EditText annualMileageEdit;

	private DCVehicle vehicle = new DCVehicle();
	private RelativeLayout loading;
	private AlertDialog.Builder builder;

	private ArrayList<String> valueList;
	private ArrayList<String> keyList;
	private boolean isExist = false; // Indicates if vehicle exists in the
										// database
	private boolean isFirstVehicle = false; // Indicates if user is adding their
											// first vehicle or in another words
											// they don't poses any.

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DriverConnexApp.getUserPref();
		setContentView(R.layout.activity_vehicle_add);

		loading = (RelativeLayout) findViewById(R.id.loadSpinner);
		regNumberEdit = (EditText) findViewById(R.id.regNumberEdit);
		currentMileageEdit = (EditText) findViewById(R.id.currentMileageEdit);
		annualMileageEdit = (EditText) findViewById(R.id.annualMileageEdit);

		regNumberEdit.setOnClickListener(onClickListener);
		currentMileageEdit.setOnClickListener(onClickListener);
		annualMileageEdit.setOnClickListener(onClickListener);

		regNumberEdit.setOnFocusChangeListener(onFocusChangeListener);
		currentMileageEdit.setOnFocusChangeListener(onFocusChangeListener);
		annualMileageEdit.setOnFocusChangeListener(onFocusChangeListener);

		regNumberEdit.setOnEditorActionListener(onEditorActionListener);
		currentMileageEdit.setOnEditorActionListener(onEditorActionListener);
		annualMileageEdit.setOnEditorActionListener(onEditorActionListener);

		if (getIntent().getExtras() != null)
			isFirstVehicle = getIntent().getExtras().getBoolean(
					"isFirstVehicle", false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_add, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			// Check if user is adding his first vehicle
			// If, so that means user came from LoginActivity or
			// RegisterActivity
			// this has to be set to false to avoid logging again.
			if (isFirstVehicle)
				DriverConnexApp.getUserPref().setLogin(false);

			finish();
			overridePendingTransition(R.anim.slide_right_main,
					R.anim.slide_right_sub);
			return true;
		} else if (item.getItemId() == R.id.action_add) {
			if (regNumberEdit.getText().toString().isEmpty()) {
				// Display dialogue to inform user that he can't add existing
				// vehicle
				builder = new AlertDialog.Builder(AddVehicleActivity.this);
				builder.setTitle("Error")
						.setMessage(
								getResources().getString(
										R.string.vehicle_error_reg_number))
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).show();
			} else if (currentMileageEdit.getText().toString().isEmpty()) {
				// Display dialogue to inform user that he can't add existing
				// vehicle
				builder = new AlertDialog.Builder(AddVehicleActivity.this);
				builder.setTitle("Error")
						.setMessage(
								getResources().getString(
										R.string.vehicle_error_current_mileage))
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).show();
			} else {
				vehicle.setRegistration(regNumberEdit.getText().toString());
				addVehicleToCloud(regNumberEdit.getText().toString());
			}
		}

		return super.onOptionsItemSelected(item);
	}

	// new method of vehicle add on cloud .
	private String removeSpaceAndUpperCase(String reg) {
		String regID = reg.replaceAll("\\s+", "").toUpperCase();
		return regID;

	}

	private void addVehicleToCloud(String reg) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("vehicleReg", removeSpaceAndUpperCase(reg));
		ParseCloud.callFunctionInBackground("vehicleLookupForReg", params,
				new FunctionCallback<ArrayList<ArrayList<String>>>() {

					@SuppressWarnings({ "rawtypes", "unchecked" })
					@Override
					public void done(ArrayList<ArrayList<String>> result,
							ParseException e) {
						if (e == null) {
							// getting map from result at first index
							Map<String, String> infoDumpMap = ((Map<String, String>) result
									.get(0));
							vehicle.setInfodump(infoDumpMap);
							// Iterate
							List mainList = (ArrayList<String>) result.get(1);
							// Populate key value and value list from inner list
							keyList = new ArrayList<String>();
							valueList = new ArrayList<String>();

							for (int i = 0; i < mainList.size(); i++) {

								ArrayList<String> innerList = (ArrayList<String>) mainList
										.get(i);

								String value = "";
								String key = "";
								for (int j = 0; j < innerList.size(); j++) {
									try {
										value = String.valueOf(innerList.get(j));
									} catch (Exception exception) {
										value = String.valueOf(innerList.get(j));
									}
									key = innerList.get(j + 1);
									j += 1;

								}
								valueList.add(value);
								keyList.add(key);
							}
							isExist = checkVehicle();
							postVehicleValue(isExist);

							Log.d("ADDVEHICLE", "No exception");
						} else {
							Log.d("ADDVEHICLE", "adding vehicle failed");
						}
					}
				});
	}

	// vehicleReg

	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			startEditingText((EditText) v, true);
		}
	};

	private OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus)
				startEditingText((EditText) v, true);
			else
				startEditingText((EditText) v, false);
		}
	};

	private OnEditorActionListener onEditorActionListener = new OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
					|| (actionId == EditorInfo.IME_ACTION_DONE)) {
				startEditingText((EditText) v, false);
			}
			return false;
		}
	};

	/**
	 * Handles what happens when text is being edited or if it stopped being
	 * edited.
	 * 
	 * @param editText
	 * @param editing
	 */
	private void startEditingText(EditText editText, boolean editing) {
		// Starts editing
		if (editing) {
			editText.setCursorVisible(true);

			if (editText != regNumberEdit)
				editText.setText(Utilities
						.getFirstNumberDecimalFromText(editText.getText()
								.toString()));
		}
		// Stops editing
		else {
			editText.setCursorVisible(false);

			if (editText == currentMileageEdit) {
				if (!currentMileageEdit.getText().toString().contains("Miles")
						&& !currentMileageEdit.getText().toString().isEmpty())
					currentMileageEdit.setText(currentMileageEdit.getText()
							+ " Miles");
			} else if (editText == annualMileageEdit) {
				if (!annualMileageEdit.getText().toString().contains("Miles")
						&& !annualMileageEdit.getText().toString().isEmpty())
					annualMileageEdit.setText(annualMileageEdit.getText()
							+ " Miles");
			}
		}
	}

	public DCVehicle setVechileRegValues(ArrayList<String> keyList,
			ArrayList<String> valueList) {
		String name = "";
		String value = "";
		int size = keyList.size();
		int index = 0;
		for (int i = 0; i < size; i++) {
			index = i;
			value = keyList.get(index);
			name = valueList.get(index);
			if (name.equals("Make")) {
				if (!value.equals("Uknown Make")) {
					vehicle.setMake(value);
				}
			} else if (name.equals("Model")) {
				vehicle.setModel(value);
			} else if (name.equals("Derivative")) {
				vehicle.setDerivative(value);
			} else if (name.equals("CC")) {
				vehicle.setCC(value);
			} else if (name.equals("BHP")) {
				vehicle.setBhp(value);
			} else if (name.equals("Fuel")) {
				vehicle.setFuel(value);
			} else if (name.equals("Body")) {
				vehicle.setBody(value);
			} else if (name.equals("Gearbox")) {
				vehicle.setGearBox(value);
			} else if (name.equals("Ext.Urb MPG")) {
				vehicle.setExturbMPG(value);
			} else if (name.equals("6 Month RFL")) {
				vehicle.setSixMonthRFL(value);
			} else if (name.equals("CO2")) {
				vehicle.setCo2Value(value);
			} else if (name.equals("Year RFL")) {
				vehicle.setYearRFL(value);
			} else if (name.equals("Com. MPG")) {
				vehicle.setComMPG(value);
			}
		}
		return vehicle;
	}

	public Boolean checkVehicle() {

		final String regId = regNumberEdit.getText().toString();

		try {
			vehicle = setVechileRegValues(valueList, keyList);

			String currentMileage;
			String annualMileage;

			currentMileage = Utilities
					.getFirstNumberDecimalFromText(currentMileageEdit.getText()
							.toString());
			annualMileage = Utilities
					.getFirstNumberDecimalFromText(annualMileageEdit.getText()
							.toString());

			if (!currentMileage.isEmpty())
				vehicle.setCurrentMileage(Long.parseLong(currentMileage));
			if (!annualMileage.isEmpty())
				vehicle.setAnnualMileage(Long.parseLong(annualMileage));

			// Make a query
			ParseQuery<ParseObject> query = ParseQuery.getQuery("DCVehicle");

			// Get a list of vehicles that belong to user
			List<ParseObject> vehicleList = query.find();

			if (vehicleList != null) {
				// Loop through all vehicles
				for (int i = 0; i < vehicleList.size(); i++) {
					// Make sure that the vehicle from the table belongs to
					// user
					if (vehicleList
							.get(i)
							.getParseObject("vehiclePrivateOwner")
							.getObjectId()
							.contentEquals(
									ParseUser.getCurrentUser().getObjectId())) {
						// Check if vehicle that user wants to register is
						// not already registered in database
						if (vehicleList.get(i).getString("vehicleRegistration")
								.equals(regId)) {
							isExist = true;
							break;
						} else
							isExist = false;
					}

				}
			} else
				isExist = false;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return isExist;

	}

	private void postVehicleValue(Boolean result) {

		loading.setVisibility(View.GONE);

		// Check if vehicle was found
		if (result) {
			// Display dialogue to inform user that he can't add existing
			// vehicle
			builder = new AlertDialog.Builder(AddVehicleActivity.this);
			builder.setTitle("Found Vehicle")
					.setMessage(
							getResources().getString(R.string.vehicle_exist))
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();
		}
		// Vehicle wasn't found
		else {

			if (vehicle.getMake() != null) {
				Intent intent = new Intent(AddVehicleActivity.this,
						AddVehicleConfirmActivity.class);
				Bundle extras = new Bundle();
				extras.putSerializable("vehicle", vehicle);
				intent.putExtras(extras);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_left_sub,
						R.anim.slide_left_main);
			} else {
				builder = new AlertDialog.Builder(AddVehicleActivity.this);
				builder.setTitle("Error")
						.setMessage(
								getResources().getString(
										R.string.vehicle_not_found))
						.setPositiveButton("Okay",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).show();
			}
		}

	}

}

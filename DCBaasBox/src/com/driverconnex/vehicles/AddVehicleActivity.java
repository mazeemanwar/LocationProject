package com.driverconnex.vehicles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasException;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasQuery;
import com.baasbox.android.BaasResult;
import com.baasbox.android.json.JsonArray;
import com.baasbox.android.json.JsonObject;
import com.baasbox.android.net.HttpRequest;
import com.driverconnex.app.AppConfig;
import com.driverconnex.app.DriverConnexApp;
import com.driverconnex.app.R;
import com.driverconnex.data.XMLVehiclDetailParser;
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
	String dictionary;
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
		inflater.inflate(R.menu.action_save, menu);
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
		} else if (item.getItemId() == R.id.action_save) {
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
				addVehicleToBassbox(regNumberEdit.getText().toString());
				// addVehicleToCloud(regNumberEdit.getText().toString());
			}
		}

		return super.onOptionsItemSelected(item);
	}

	// new method of vehicle add on cloud .
	private String removeSpaceAndUpperCase(String reg) {
		String regID = reg.replaceAll("\\s+", "").toUpperCase();
		return regID;

	}

	//
	private void addVehicleToBassbox(final String reg) {

		if (!isConnected(AddVehicleActivity.this)) {
			builder = new AlertDialog.Builder(AddVehicleActivity.this);

			builder.setTitle("Error")
					.setMessage(
							getResources().getString(
									R.string.connection_not_found))
					.setPositiveButton("Okay",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();

			return;
		}
		JsonArray jsonBody = new JsonArray();
		// jsonBody.add("plugin/dc.vehicleLookup");
		isExist = checkVehicle();
		jsonBody.add(reg);
		BaasBox box = BaasBox.getDefault();

		box.rest(HttpRequest.POST, "plugin/dc.vehicleLookup",
				new JsonObject().put("reg", reg), true,
				new BaasHandler<JsonObject>() {
					@Override
					public void handle(BaasResult<JsonObject> res) {

						Log.d("TAG", "Ok: " + res.isSuccess());
						if (!res.isSuccess()) {
							builder = new AlertDialog.Builder(
									AddVehicleActivity.this);

							builder.setTitle("Error")
									.setMessage(
											getResources().getString(
													R.string.vehicle_not_found))
									.setPositiveButton(
											"Okay",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int which) {
												}
											}).show();
							return;
						}
						try {
							// setWelcomeMsg();
							JsonObject vehicleJson = res.get().get("data");

							String vehicleXmlstring = vehicleJson
									.getString("lookup");
							System.out.println(res.value());
							vehicle = XMLVehiclDetailParser
									.getBasicMenuItemsFromXML(
											AddVehicleActivity.this, "",
											vehicleXmlstring);
							System.out.println(vehicle.getColour());

							vehicle.setRegistration(reg);
							if (!annualMileageEdit.getText().toString()
									.endsWith("")) {
								String annualMileage = Utilities
										.getFirstNumberDecimalFromText(currentMileageEdit
												.getText().toString());
								vehicle.setAnnualMileage(Long
										.parseLong(annualMileage));

							} else {
								vehicle.setAnnualMileage(1000);

							}
							String currentMileage = Utilities
									.getFirstNumberDecimalFromText(currentMileageEdit
											.getText().toString());
							vehicle.setCurrentMileage(Long
									.parseLong(currentMileage));
							// JsonArray test = my.getArray("lookup");
							dictionary = vehicleXmlstring;
							System.out.println(dictionary);
							System.out.println(isExist);
							postVehicleValue(isExist);

						} catch (BaasException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				});

	}

	// parse
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
			if (AppConfig.getIsOnlineModuleRequired().equals("yes")) {
				vehicle = setVechileRegValues(valueList, keyList);
				ParseQuery<ParseObject> query = ParseQuery
						.getQuery("DCVehicle");

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
										ParseUser.getCurrentUser()
												.getObjectId())) {
							// Check if vehicle that user wants to register is
							// not already registered in database
							if (vehicleList.get(i)
									.getString("vehicleRegistration")
									.equals(regId)) {
								isExist = true;
								break;
							} else
								isExist = false;
						}

					}
				} else
					isExist = false;

			} else {

				final BaasQuery PREPARED_QUERY = BaasQuery.builder()
						.collection("BAAVehicle").build();
				// then
				PREPARED_QUERY.query(new BaasHandler<List<JsonObject>>() {

					@Override
					public void handle(BaasResult<List<JsonObject>> res) {
						// TODO Auto-generated method stub
						for (int i = 0; i < res.value().size(); i++) {
							if (res.value().get(i)
									.getString("vehicleRegistration")
									.equals(regId)) {
								isExist = true;
								break;
							} else
								isExist = false;

						}
					}

				});

			}
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

		} catch (Exception e) {
			e.printStackTrace();
		}

		return isExist;

	}

	private void postVehicleValue(Boolean result) {

		loading.setVisibility(View.GONE);

		// Check if vehicle was found
		System.out.println(result);
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

			System.out.println(vehicle.getMake());

			if (vehicle.getMake() != null) {
				Intent intent = new Intent(AddVehicleActivity.this,
						AddVehicleConfirmActivity.class);
				Bundle extras = new Bundle();
				extras.putSerializable("vehicle", vehicle);
				extras.putString("jsonDictionary", dictionary);
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

	// for testing purpose
	private void setWelcomeMsg() {
		BaasBox box = BaasBox.getDefault();

		box.rest(HttpRequest.POST, "plugin/dc.welcomeMessage",
				new JsonObject(), true, new BaasHandler<JsonObject>() {
					@Override
					public void handle(BaasResult<JsonObject> res) {
						System.out.println(res);
					}
				});

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

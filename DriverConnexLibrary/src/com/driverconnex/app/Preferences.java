package com.driverconnex.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.driverconnex.data.Organisation;
import com.google.gson.Gson;

/**
 * User and global preferences. Every user has it's own SharedPreferences.
 * 
 * @author Adrian Klimczak
 * @author Muhammad Azeem Anwar
 * 
 */

public class Preferences {
	private Context context;
	private static SharedPreferences userPreferences;
	private SharedPreferences globalPreferences;

	public Preferences(Context context) {
		this.context = context;

		// Creates global and user preferences
		globalPreferences = context.getSharedPreferences(
				context.getPackageName() + ".settings.pref",
				Context.MODE_PRIVATE);
		userPreferences = context.getSharedPreferences(context.getPackageName()
				+ "." + getUserName() + ".settings.pref", Context.MODE_PRIVATE);
	}

	/**
	 * Updates user preferences to make sure that it's pointing at the shared
	 * preferences of the currently using the app user
	 */
	public void updateSharedPreferences() {
		userPreferences = context.getSharedPreferences(context.getPackageName()
				+ "." + getUserName() + ".settings.pref", Context.MODE_PRIVATE);
	}

	/**
	 * Gets value of expense per mile.
	 * 
	 * @return
	 */
	public float getExpensePerMile() {
		// Default expense is £0.15
		return userPreferences.getFloat("expense_per_mile", 0.15f);
	}

	/**
	 * Sets value of expense per mile.
	 * 
	 * @param value
	 */
	public void setExpensePerMile(float value) {
		userPreferences.edit().putFloat("expense_per_mile", value).apply();
	}

	/**
	 * Sets averages for Journey Rating
	 * 
	 * @param cornering
	 * @param acceleration
	 * @param braking
	 */
	public void setAveragesTotal(float cornering, float acceleration,
			float braking) {
		userPreferences.edit().putFloat("cornering_total", cornering).apply();
		userPreferences.edit().putFloat("acceleration_total", acceleration)
				.apply();
		userPreferences.edit().putFloat("braking_total", braking).apply();
	}

	/**
	 * Gets total value of average cornering
	 */
	public float getAverageCorneringTotal() {
		return userPreferences.getFloat("cornering_total", 0);
	}

	/**
	 * Gets total value of average acceleration
	 */
	public float getAverageAccelerationTotal() {
		return userPreferences.getFloat("cornering_total", 0);
	}

	/**
	 * Gets total value of average braking
	 */
	public float getAverageBrakingTotal() {
		return userPreferences.getFloat("cornering_total", 0);
	}

	/**
	 * Sets user name
	 */
	public void setUserName(String username) {
		globalPreferences.edit().putString("username", username).apply();
	}

	/**
	 * Gets user name
	 * 
	 * @return
	 */
	public String getUserName() {
		return globalPreferences.getString("username", "");
	}

	/**
	 * Sets user id
	 * 
	 * @param userID
	 */
	public void setUserID(String userID) {
		globalPreferences.edit().putString("user_id", userID).apply();
	}

	/**
	 * Gets users id
	 * 
	 * @return
	 */
	public String getUserID() {
		return globalPreferences.getString("user_id", "");
	}

	/**
	 * Sets if user is logged in
	 * 
	 * @param login
	 */
	public void setLogin(boolean login) {
		globalPreferences.edit().putBoolean("login", login).apply();
	}

	/**
	 * Checks if user is logged in
	 * 
	 * @return
	 */
	public boolean isLogin() {
		return globalPreferences.getBoolean("login", false);
	}

	/**
	 * Sets a name of the default Bluetooth device.
	 * 
	 * @param bluetoothDeviceName
	 */
	public void setBluetoothDeviceName(String bluetoothDeviceName) {
		userPreferences.edit()
				.putString("default_bluetooth_device", bluetoothDeviceName)
				.apply();
	}

	/**
	 * Gets a name of the default Bluetooth device.
	 * 
	 * @return
	 */
	public String getBluetoothDeviceName() {
		return userPreferences.getString("default_bluetooth_device", "");
	}

	/**
	 * Sets monthly vehicle check expiry date.
	 * 
	 * @param expiryDate
	 * @param vehicleId
	 */
	public void setMonthlyVehicleCheckExpiryDate(String expiryDate,
			String vehicleId) {
		userPreferences
				.edit()
				.putString("monthly_vehicle_check_expiry_date_" + "vehicleId",
						expiryDate).apply();
	}

	/**
	 * Gets monthly vehicle check expiry date.
	 * 
	 * @param vehicleId
	 * @return
	 */
	public String getMonthlyVehicleCheckExpiryDate(String vehicleId) {
		return userPreferences.getString("monthly_vehicle_check_expiry_date_"
				+ "vehicleId", "");
	}

	/**
	 * Gets savings scale. By default it's "Monthly".
	 * 
	 * @return
	 */
	public String getSavingsScale() {
		return userPreferences.getString("savings_scale", "Monthly");
	}

	/**
	 * Sets savings scale.
	 * 
	 * @param savingsScale
	 */
	public void setSavingsScale(String savingsScale) {
		userPreferences.edit().putString("savings_scale", savingsScale).apply();
	}

	// Includes for SavingsSectionFragment
	// --------------------------------
	/**
	 * Checks if fuel is included.
	 * 
	 * @return
	 */
	public boolean isFuelCostsIncluded() {
		return userPreferences.getBoolean("savings_include_fuel", true);
	}

	public void setFuelCostsIncluded(boolean isIncludeFuel) {
		userPreferences.edit()
				.putBoolean("savings_include_fuel", isIncludeFuel).apply();
	}

	/**
	 * Checks if standing costs are included.
	 * 
	 * @return
	 */
	public boolean isStandingCostsIncluded() {
		return userPreferences.getBoolean("savings_include_standing", true);
	}

	public void setStandingCostsIncluded(boolean isIncludeStanding) {
		userPreferences.edit()
				.putBoolean("savings_include_standing", isIncludeStanding)
				.apply();
	}

	/**
	 * Checks if maintenance costs are included.
	 * 
	 * @return
	 */
	public boolean isMaintenanceCostsIncluded() {
		return userPreferences.getBoolean("savings_include_maintenance", true);
	}

	public void setMaintenanceCostsIncluded(boolean isIncludeMaintenance) {
		userPreferences
				.edit()
				.putBoolean("savings_include_maintenance", isIncludeMaintenance)
				.apply();
	}

	/**
	 * Sets organisation configuration.
	 * 
	 * @param value
	 */
	public void setOrganisationConfig(Organisation org) {
		userPreferences.edit().putString("breakDown", new Gson().toJson(org))
				.apply();
	}

	public Organisation getOrganizationConfig() {
		String breakDownKey = userPreferences.getString("breakDown", "");

		if (breakDownKey.equals(""))
			return null;
		else
			return new Gson().fromJson(breakDownKey, Organisation.class);

	}

	// public ArrayList<ArrayList<String>> getBreakDown() {
	// ArrayList<ArrayList<String>> myList = new ArrayList<ArrayList<String>>();
	// try {
	// JSONArray jsonArray = new
	// JSONArray(userPreferences.getString("breakDown", "[]"));
	// for (int i = 0; i < jsonArray.length(); i++) {
	//
	// }
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return myList;
	//
	// }
	//
	/**
	 * Gets Vehicle Registration locally .
	 * 
	 * @return
	 */
	public String getDefaultVehicleReg() {
		return userPreferences.getString("vehicle_reg", "");
	}

	/**
	 * Set Default Vehicle Registration locally
	 * 
	 * @param savingsScale
	 */
	public void setDefaultVehicleReg(String reg) {
		userPreferences.edit().putString("vehicle_reg", reg).apply();
	}
}

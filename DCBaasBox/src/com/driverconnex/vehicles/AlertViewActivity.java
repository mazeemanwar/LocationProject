package com.driverconnex.vehicles;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.driverconnex.app.DriverConnexApp;
import com.driverconnex.app.R;
import com.driverconnex.data.Alert;
import com.driverconnex.singletons.DCVehilceDataSingleton;

public class AlertViewActivity extends Activity {
	TextView vehicleName;
	TextView vehicleReg;
	TextView alertStringView;
	int selectedAlert;
	private ArrayList<DCVehicle> vehiclesList = new ArrayList<DCVehicle>();

	ArrayList<Alert> alertList = new ArrayList<Alert>();
	HashMap<String, ArrayList<String>> alertMap = new HashMap<String, ArrayList<String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alert_view);

		vehicleName = (TextView) findViewById(R.id.main);
		vehicleReg = (TextView) findViewById(R.id.sub);
		alertStringView = (TextView) findViewById(R.id.alertString);

		if (getIntent().getExtras() != null) {
			selectedAlert = getIntent().getExtras().getInt("index");
		}
	}

	@Override
	protected void onResume() {

		super.onResume();
		setTextValue();
		// if (selectedAnswer != null && !selectedAnswer.equals("")) {
		// policyDetail.setText(selectedAnswer);
		// getActionBar().setTitle("Help Detail");
		//
		// } else {
		// Bundle bundle = getIntent().getExtras();
		// policyDetail.setText(bundle.getString("policy_detail"));
		// }

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == android.R.id.home) {
			finish();
			overridePendingTransition(R.anim.null_anim, R.anim.slide_out);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setTextValue() {
		vehiclesList = new ArrayList<DCVehicle>();
		ArrayList<String> tempList = new ArrayList<String>();
		alertMap = new HashMap<String, ArrayList<String>>();
		alertMap = DCVehilceDataSingleton.getDCModuleSingleton(
				AlertViewActivity.this).getTotalAlerts();

		for (String key : alertMap.keySet()) {
			// String s = alertMap.get(key);
			tempList = alertMap.get(key);
			for (int i = 0; i < tempList.size(); i++) {
				Alert alert = new Alert();
				alert.setVehicleId(key);
				alert.setMsg(tempList.get(i));
				alertList.add(alert);
			}

		}
		String reg = "";
		for (int i = 0; i < alertList.size(); i++) {
			Alert alert = new Alert();
			if (i == selectedAlert) {
				reg = alertList.get(i).getVehicleId();
				vehicleReg.setText(alertList.get(i).getVehicleId());
				String msg = alertList.get(i).getMsg();
				if (msg.equals("MOT Alert")) {
					alertStringView
							.setText("Please check this vehicle's MOT expiry date.");

				} else if (msg.equals("Insurance Alert")) {
					alertStringView
							.setText("Please check this vehicles insurance cover is correct.");

				} else if (msg.equals("TAX Alert")) {
					alertStringView
							.setText("Please check this vehicle's Tax expiry date.");

				} else if (msg.equals("Vehicle Check Alert")) {
					alertStringView
							.setText("This vehicle needs to have checks carried out.");

				}
				// alertStringView.setText(alertList.get(i).getMsg());
			}
		}

		vehiclesList = DCVehilceDataSingleton.getDCModuleSingleton(
				AlertViewActivity.this).getVehilesList();

		if (vehiclesList != null && vehiclesList.size() > 0) {

			for (int i = 0; i < vehiclesList.size(); i++) {
				String defaultVehilceId = reg;

				if (defaultVehilceId != null && !defaultVehilceId.equals("")) {
					if (defaultVehilceId.equals(vehiclesList.get(i)
							.getRegistration())) {
						vehicleName
								.setText(vehiclesList.get(i).getDerivative());
					}
				}
			}
		}

	}
}

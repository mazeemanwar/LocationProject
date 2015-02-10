package com.driverconnex.vehicles;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.driverconnex.adapter.AlertListAdapter;
import com.driverconnex.adapter.PolicyListAdapter;
import com.driverconnex.app.R;
import com.driverconnex.app.R.array;
import com.driverconnex.basicmodules.HelpListActivity;
import com.driverconnex.data.Alert;
import com.driverconnex.data.HelpListItem;
import com.driverconnex.data.HelpListItems;
import com.driverconnex.singletons.DCVehilceDataSingleton;

public class AlertsActivity extends Activity {
	private ListView list;
	private ArrayList<HelpListItem> helpItems = new ArrayList<HelpListItem>();
	private ArrayList<String> listItem = new ArrayList<String>();
	private RelativeLayout loading;
	ArrayList<HelpListItems> helpList = new ArrayList<HelpListItems>();
	ArrayList<Alert> alertList = new ArrayList<Alert>();
	HashMap<String, ArrayList<String>> alertMap = new HashMap<String, ArrayList<String>>();

	public static final int CATEGORY = 0;
	public static final int QUESTION = 1;
	public static final int ANSWER = 2;
	private int listType = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_policy);
		list = (ListView) findViewById(R.id.policylist);
		loading = (RelativeLayout) findViewById(R.id.loadSpinner);
		list.setOnItemClickListener(itemClickListener);

	}

	@Override
	protected void onResume() {
		super.onResume();

		// Enable loading bar
		// loading.setVisibility(View.VISIBLE);
		listItem = new ArrayList<String>();

		getHelpFromAsset();
		displayAdapter(0);

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
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			overridePendingTransition(R.anim.null_anim, R.anim.slide_out);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Bundle bundle = new Bundle();
			bundle.putInt("index", position);
			Intent intent = new Intent(AlertsActivity.this,
					AlertViewActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			AlertsActivity.this.overridePendingTransition(R.anim.slide_in,
					R.anim.null_anim);
		}
	};

	private void getHelpFromAsset() {
		HashMap<String, ArrayList<String>> test = new HashMap<String, ArrayList<String>>();
		alertList = new ArrayList<Alert>();
		ArrayList<String> tempList = new ArrayList<String>();
		alertMap = DCVehilceDataSingleton.getDCModuleSingleton(
				AlertsActivity.this).getTotalAlerts();

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
		int sd = alertList.size();
		System.out.println();
		list.setAdapter(new AlertListAdapter(AlertsActivity.this, alertList));

	}

	private void displayAdapter(int index) {
		// System.out.println();
		// if (listType == CATEGORY) {
		// listItem = new ArrayList<String>();
		// for (int i = 0; i < helpList.size(); i++) {
		// String heading = helpList.get(i).getName();
		// listItem.add(heading);
		// }
		//
		// } else if (listType == QUESTION) {
		// for (int k = 0; k < helpList.get(index).getSubitems().size(); k++) {
		// String heading = helpList.get(index).getSubitems().get(k)
		// .getQuesiton();
		// listItem.add(heading);
		//
		// }
		// } else if (listType == ANSWER) {
		// for (int k = 0; k < helpList.get(index).getSubitems().size(); k++) {
		// String heading = helpList.get(index).getSubitems().get(k)
		// .getAnswer();
		// listItem.add(heading);
		//
		// }
		//
		// }
		//

	}
}
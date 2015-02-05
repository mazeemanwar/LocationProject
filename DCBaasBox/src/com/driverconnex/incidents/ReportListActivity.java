package com.driverconnex.incidents;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.driverconnex.adapter.ListAdapter;
import com.driverconnex.adapter.ListAdapterItem;
import com.driverconnex.app.R;
import com.driverconnex.expenses.DCExpense;
import com.driverconnex.expenses.ExpensesDataSource;
import com.driverconnex.expenses.ReviewExpensesActivity;
import com.driverconnex.utilities.LocationUtilities;
import com.driverconnex.utilities.ParseUtilities;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Activity used to display a list of all incident reports that user saved in
 * the Parse database.
 * 
 * @author Adrian Klimczak
 * 
 */

public class ReportListActivity extends Activity {
	private final int REQUEST_REPORT_POSITION = 100;
	private ListView list;
	private ListAdapter adapter;
	private RelativeLayout loading;
	private ArrayList<IncidentLocation> data = new ArrayList<IncidentLocation>();
	private ArrayList<IncidentLocation> incidentList = new ArrayList<IncidentLocation>();
	private ArrayList<DCIncident> incidents = new ArrayList<DCIncident>();
	private ArrayList<ListAdapterItem> adapterData = new ArrayList<ListAdapterItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		loading = (RelativeLayout) findViewById(R.id.loadSpinner);
		list = (ListView) findViewById(R.id.list);

		list.setOnItemClickListener(onItemClickListener);

		// Enable loading bar
		loading.setVisibility(View.VISIBLE);
		// getIncidentsByParse();
		getInidentsLocally();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			overridePendingTransition(R.anim.slide_right_main,
					R.anim.slide_right_sub);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == this.REQUEST_REPORT_POSITION) {
			// Make sure the request was successful
			if (resultCode == RESULT_OK) {
				// Get the position of clicked item from the previous activity
				int position = data.getIntExtra("reportPosition", -1);

				if (position != -1) {
					// This item was deleted in previous activity, so update the
					// adapter
					incidents.remove(position);
					adapterData.remove(position);
					adapter.notifyDataSetChanged();
				}
			}
		}
	}

	/**
	 * On click listener for the items in the list
	 */
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent = new Intent(ReportListActivity.this,
					ReviewReportActivity.class);
			IncidentLocation currentIncident = incidentList.get(position);
			String Date = currentIncident.getDate();

			String lat = String.valueOf(currentIncident.getLatitude());
			String log = String.valueOf(currentIncident.getLongitude());

			intent.putExtra("lat", lat);
			intent.putExtra("log", log);
			intent.putExtra("veh", currentIncident.getVehicleReg());
			intent.putExtra("des", currentIncident.getDescriptioin());
			intent.putExtra("date", currentIncident.getDate());
			startActivity(intent);
			// intent.putExtra("report", currentIncident);
			// // Pass position of the clicked item
			// intent.putExtra("reportPosition", position);
			// startActivityForResult(intent, REQUEST_REPORT_POSITION);
			overridePendingTransition(R.anim.slide_left_sub,
					R.anim.slide_left_main);
		}
	};

	private void getInidentsLocally() {

		IncidentDataSource mDataSource = new IncidentDataSource(
				ReportListActivity.this);
		mDataSource.open();
		data = mDataSource.getAllIncidentReports();

		mDataSource.close();
		System.out.println(data);

		for (int i = 0; i < data.size(); i++) {
			System.out.println(data);
			// String dataString = data.get(i).getDate();
			// String date = data.get(i).getDescriptioin();
			String tests = data.get(i).getLatitude() + "";
			String n = String.valueOf(data.get(i).getLatitude());
			System.out.println(n);
			incidentList.add(data.get(i));
			ListAdapterItem incidentItem = new ListAdapterItem();
			incidentItem.title = data.get(i).getDate();
			incidentItem.subtitle = "";
			adapterData.add(incidentItem);
		}
		loading.setVisibility(View.GONE);

		adapter = new ListAdapter(ReportListActivity.this, adapterData,
				R.drawable.warning_grey_56x52);
		list.setAdapter(adapter);

	}

	/**
	 * Gets list of DC Incidents from the parse database
	 */
	private void getIncidentsByParse() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("DCIncident");
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(final List<ParseObject> incidentsList,
					ParseException e) {
				if (e == null) {
					// Loop through all DC Incidents that we got from the query
					for (int i = 0; i < incidentsList.size(); i++) {
						// Make sure that this DC Incident object belongs to the
						// user
						if (incidentsList
								.get(i)
								.getParseObject("incidentUser")
								.getObjectId()
								.contentEquals(
										ParseUser.getCurrentUser()
												.getObjectId())) {
							// Convert parse DC Incident object into something
							// we can read
							DCIncident incident = ParseUtilities
									.convertIncident(incidentsList.get(i));

							incidents.add(incident);

							ListAdapterItem incidentItem = new ListAdapterItem();
							incidentItem.title = incident.getDate();
							incidentItem.subtitle = "";
							adapterData.add(incidentItem);
						}
					}

					// Set adapter
					adapter = new ListAdapter(ReportListActivity.this,
							adapterData, R.drawable.warning_grey_56x52);
					list.setAdapter(adapter);
				} else
					Log.e("Get Incidents List", e.getMessage());

				loading.setVisibility(View.GONE);
			}
		});
	}
}

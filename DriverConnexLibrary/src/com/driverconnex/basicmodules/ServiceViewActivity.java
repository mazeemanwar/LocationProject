package com.driverconnex.basicmodules;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.driverconnex.adapter.PolicyListAdapter;
import com.driverconnex.adapter.ServiceListAdapter;
import com.driverconnex.app.R;
import com.driverconnex.data.HelpListItem;
import com.driverconnex.data.ServiceListItem;
import com.driverconnex.data.ServiceListItems;
import com.driverconnex.data.XMLModuleConfigParser;
import com.driverconnex.incidents.IncidentActivity;

public class ServiceViewActivity extends Activity {
	private ListView list;
	private ArrayList<ServiceListItem> helpItems = new ArrayList<ServiceListItem>();
	private ArrayList<ServiceListItem> listItem = new ArrayList<ServiceListItem>();
	private RelativeLayout loading;
	ArrayList<ServiceListItems> helpList = new ArrayList<ServiceListItems>();
	private int listIndex;
	private boolean isEmail = false;
	private int listType = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_policy);
		list = (ListView) findViewById(R.id.policylist);
		loading = (RelativeLayout) findViewById(R.id.loadSpinner);
		list.setOnItemClickListener(itemClickListener);
		if (getIntent().getExtras() != null) {
			listIndex = getIntent().getExtras().getInt("index");
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		getActionBar().setTitle("Service Providers");

		// Enable loading bar
		loading.setVisibility(View.VISIBLE);
		listItem = new ArrayList<ServiceListItem>();

		getHelpFromAsset();
		displayAdapter();

	}

	/**
	 * Handles what happens when item from the list is clicked/touched
	 */
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long index) {
			{
				Intent returnIntent = new Intent();
				// returnIntent.putExtra("vehicle", vehicles.get(position));
				setResult(RESULT_OK, returnIntent);

				finish();
				overridePendingTransition(R.anim.slide_out, R.anim.slide_in);
			}
		}
	};

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
			String answer = selectedAnswer(position);
			if (isEmail) {
				isEmail = false;
				Intent i = new Intent(Intent.ACTION_SEND);
				String emailBody = "driverconnex";

				// Prepare intent
				i.setType("message/rfc822");
				i.putExtra(Intent.EXTRA_EMAIL,
						new String[] { "recipient@example.com" });
				i.putExtra(Intent.EXTRA_SUBJECT, "testing ");
				i.putExtra(Intent.EXTRA_TEXT, emailBody);

				try {
					startActivity(Intent.createChooser(i, "Send mail..."));
				} catch (android.content.ActivityNotFoundException ex) {
					Toast.makeText(ServiceViewActivity.this,
							"There are no email clients installed.",
							Toast.LENGTH_SHORT).show();
				}

			} else {
				Intent callIntent = new Intent(Intent.ACTION_DIAL,
						Uri.parse("tel:" + answer));
				startActivity(callIntent);

			}

		}
	};

	private void getHelpFromAsset() {
		loading.setVisibility(View.GONE);
		helpList = XMLModuleConfigParser.getServiceItemsFromXML(
				ServiceViewActivity.this, "service.xml");

		for (int i = 0; i < helpList.size(); i++) {
			ServiceListItem item = new ServiceListItem();
			// item.setCategory(helpList.get(i).getName());
			// item.setCategory(true);
			helpItems.add(item);

			for (int j = 0; j < helpList.get(i).getSubitems().size(); j++) {
				helpItems.add(helpList.get(i).getSubitems().get(j));

			}

		}

	}

	private void displayAdapter() {
		System.out.println();
		listItem = new ArrayList<ServiceListItem>();

		for (int k = 0; k < helpList.get(listIndex).getSubitems().size(); k++) {
			String heading = helpList.get(listIndex).getSubitems().get(k)
					.getPhone();

			ServiceListItem item = new ServiceListItem();
			item.setPhone(helpList.get(listIndex).getSubitems().get(k)
					.getPhone());
			item.setPhonename(helpList.get(listIndex).getSubitems().get(k)
					.getPhonename());
			item.setEmailname(helpList.get(listIndex).getSubitems().get(k)
					.getEmailname());
			item.setEmail(helpList.get(listIndex).getSubitems().get(k)
					.getEmail());
			listItem.add(item);

		}
		list.setAdapter(new ServiceListAdapter(ServiceViewActivity.this,
				listItem));
	}

	private String selectedAnswer(int position) {
		System.out.println();
		listItem = new ArrayList<ServiceListItem>();
		String heading = "";
		for (int k = 0; k < helpList.get(listIndex).getSubitems().size(); k++) {
			heading = helpList.get(listIndex).getSubitems().get(position)
					.getPhone();
			if (heading != null && !heading.equals("")) {

			} else {

				heading = helpList.get(listIndex).getSubitems().get(position)
						.getEmail();
				isEmail = true;
				return heading;

			}
		}
		return heading;

	}
}
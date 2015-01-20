package com.driverconnex.basicmodules;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;

import com.driverconnex.adapter.PolicyListAdapter;
import com.driverconnex.app.R;
import com.driverconnex.data.HelpListItem;
import com.driverconnex.data.HelpListItems;
import com.driverconnex.data.XMLModuleConfigParser;

public class HelpListActivity extends Activity {
	private ListView list;
	private ArrayList<HelpListItem> helpItems = new ArrayList<HelpListItem>();
	private ArrayList<String> listItem = new ArrayList<String>();
	private RelativeLayout loading;
	ArrayList<HelpListItems> helpList = new ArrayList<HelpListItems>();
	private int listIndex;

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

		// Enable loading bar
		loading.setVisibility(View.VISIBLE);
		listItem = new ArrayList<String>();

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

			System.out.println(position);

			String answer = selectedAnswer(position);

			Bundle bundle = new Bundle();
			bundle.putString("answer", answer);
			Intent intent = new Intent(HelpListActivity.this,
					PolicyViewActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			HelpListActivity.this.overridePendingTransition(R.anim.slide_in,
					R.anim.null_anim);
		}
	};

	private void getHelpFromAsset() {
		loading.setVisibility(View.GONE);
		helpList = XMLModuleConfigParser.getHelpItemsFromXML(
				HelpListActivity.this, "help.xml");

		for (int i = 0; i < helpList.size(); i++) {
			HelpListItem item = new HelpListItem();
			item.setCategory(helpList.get(i).getName());
			item.setCategory(true);
			helpItems.add(item);

			for (int j = 0; j < helpList.get(i).getSubitems().size(); j++) {
				helpItems.add(helpList.get(i).getSubitems().get(j));

			}

		}

	}

	private void displayAdapter() {
		System.out.println();
		listItem = new ArrayList<String>();

		for (int k = 0; k < helpList.get(listIndex).getSubitems().size(); k++) {
			String heading = helpList.get(listIndex).getSubitems().get(k)
					.getQuesiton();
			listItem.add(heading);

		}
		list.setAdapter(new PolicyListAdapter(HelpListActivity.this, listItem,
				true));
	}

	private String selectedAnswer(int position) {
		System.out.println();
		listItem = new ArrayList<String>();
		String heading = "";
		for (int k = 0; k < helpList.get(listIndex).getSubitems().size(); k++) {
			heading = helpList.get(listIndex).getSubitems().get(position)
					.getAnswer();

		}
		return heading;
	}
}
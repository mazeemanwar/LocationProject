package com.driverconnex.basicmodules;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.driverconnex.adapter.PolicyListAdapter;
import com.driverconnex.app.R;
import com.driverconnex.data.HelpListItem;
import com.driverconnex.data.HelpListItems;
import com.driverconnex.data.XMLModuleConfigParser;

/**
 * 
 * @author Muhammad Azeem Anwar
 * 
 */

public class HelpActivity extends Activity {
	private ListView list;
	private ArrayList<HelpListItem> helpItems = new ArrayList<HelpListItem>();
	private ArrayList<String> listItem = new ArrayList<String>();
	private RelativeLayout loading;
	private RelativeLayout bottomBar;

	ArrayList<HelpListItems> helpList = new ArrayList<HelpListItems>();
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
		bottomBar = (RelativeLayout) findViewById(R.id.bottom_bar);
		bottomBar.setVisibility(View.GONE);

		list.setOnItemClickListener(itemClickListener);

	}

	@Override
	protected void onResume() {
		super.onResume();

		// Enable loading bar
		loading.setVisibility(View.VISIBLE);
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
			Intent intent = new Intent(HelpActivity.this,
					HelpListActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			HelpActivity.this.overridePendingTransition(R.anim.slide_in,
					R.anim.null_anim);
		}
	};

	private void getHelpFromAsset() {
		loading.setVisibility(View.GONE);
		helpList = XMLModuleConfigParser.getHelpItemsFromXML(HelpActivity.this,
				"help.xml");

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

	private void displayAdapter(int index) {
		System.out.println();
		if (listType == CATEGORY) {
			listItem = new ArrayList<String>();
			for (int i = 0; i < helpList.size(); i++) {
				String heading = helpList.get(i).getName();
				listItem.add(heading);
			}

		} else if (listType == QUESTION) {
			for (int k = 0; k < helpList.get(index).getSubitems().size(); k++) {
				String heading = helpList.get(index).getSubitems().get(k)
						.getQuesiton();
				listItem.add(heading);

			}
		} else if (listType == ANSWER) {
			for (int k = 0; k < helpList.get(index).getSubitems().size(); k++) {
				String heading = helpList.get(index).getSubitems().get(k)
						.getAnswer();
				listItem.add(heading);

			}

		}

		list.setAdapter(new PolicyListAdapter(HelpActivity.this, listItem, true));

	}
}
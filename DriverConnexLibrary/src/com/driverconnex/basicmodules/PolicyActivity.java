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
import com.driverconnex.data.Message;
import com.driverconnex.data.Policy;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

/**
 * 
 * @author Muhammad Azeem Anwar
 * 
 */
public class PolicyActivity extends Activity {
	private ListView list;
	private ArrayList<Policy> policies;
	private RelativeLayout loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_policy);
		list = (ListView) findViewById(R.id.policylist);
		loading = (RelativeLayout) findViewById(R.id.loadSpinner);
		list.setOnItemClickListener(itemClickListener);

		// if(getIntent().getExtras() != null)
		// {
		// vehiclePicker = getIntent().getExtras().getBoolean("vehiclePicker");
		// }
		//
		// if(!vehiclePicker)
		// getActionBar().setSubtitle("Tap to select a default vehicle");

	}

	@Override
	protected void onResume() {
		super.onResume();

		// Enable loading bar
		loading.setVisibility(View.VISIBLE);

		getPolicyFromParse();

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
				overridePendingTransition(R.anim.slide_right_main,
						R.anim.slide_right_sub);
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
			Policy companyPolicy = policies.get(position);
			Bundle bundle = new Bundle();
			bundle.putString("policy_detail", companyPolicy.getDetail());
			Intent intent = new Intent(PolicyActivity.this,
					PolicyViewActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			PolicyActivity.this.overridePendingTransition(R.anim.slide_in,
					R.anim.null_anim);
		}
	};

	private void getPolicyFromParse() {
		loading.setVisibility(View.GONE);

		ParseObject userOrganisation = ParseUser.getCurrentUser()
				.getParseObject("userOrganisation");
		ParseRelation<ParseObject> relation = userOrganisation
				.getRelation("organisationPolicies");
		ParseQuery<ParseObject> query = relation.getQuery();
		query.getFirstInBackground(new GetCallback<ParseObject>() {

			@Override
			public void done(ParseObject policyObject, ParseException arg1) {
				// TODO Auto-generated method stub
				if (arg1 == null) {
					// because we have only one policy object if we have more
					// then one we have to implement regarding this
					policies = new ArrayList<Policy>();
					for (int i = 0; i < 1; i++) {

						Policy policy = new Policy();

						policy.setTitle(policyObject.getString("policyName"));
						policy.setDetail(policyObject.getString("policyText"));
						System.out.println();
						policies.add(policy);
						System.out.println();
						list.setAdapter(new PolicyListAdapter(
								PolicyActivity.this, policies));
					}

				} else {
				}

			}

		});

	}

}

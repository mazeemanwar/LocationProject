package com.driverconnex.basicmodules;

import com.driverconnex.app.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
/**
 * 
 * @author Muhammad Azeem Anwar
 * 
 */
public class PolicyViewActivity extends Activity {
	TextView policyDetail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_policy_detail);
		policyDetail = (TextView) findViewById(R.id.policyTextView);
	}

	@Override
	protected void onResume() {

		super.onResume();
		Bundle bundle = getIntent().getExtras();

		policyDetail.setText(bundle.getString("policy_detail"));
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
}

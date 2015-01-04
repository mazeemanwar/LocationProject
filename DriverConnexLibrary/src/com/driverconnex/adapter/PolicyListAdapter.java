package com.driverconnex.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.driverconnex.app.R;
import com.driverconnex.data.Policy;

/**
 * Not yet implemented.
 * 
 * @author Muhammad Azeem Anwar
 * 
 */

public class PolicyListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Policy> myPolicies;

	public PolicyListAdapter(Context context, ArrayList<Policy> policies) {
		super();
		this.context = context;
		this.myPolicies = policies;
	}

	@Override
	public int getCount() {
		return myPolicies.size();
	}

	@Override
	public Object getItem(int position) {
		return myPolicies.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(context.LAYOUT_INFLATER_SERVICE);
			row = (View) inflater.inflate(R.layout.list_item_policy, null);
		}

		// TextView title = (TextView) row.findViewById(R.id.listpolicyheading);
		TextView policyTitle = (TextView) row
				.findViewById(R.id.listpolicyheading);

		policyTitle.setText(myPolicies.get(position).getTitle());
		//

		// View topDivider = (View) row.findViewById(R.id.listTopDivider);
		// if (position == 0) {
		// topDivider.setVisibility(View.VISIBLE);
		// } else {
		// topDivider.setVisibility(View.INVISIBLE);
		// }

		return row;
	}

}

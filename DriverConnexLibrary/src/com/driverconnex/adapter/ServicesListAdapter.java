package com.driverconnex.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.driverconnex.app.R;
import com.driverconnex.data.Policy;

public class ServicesListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Policy> myPolicies;
	private ArrayList<String> helpList;
	private boolean fromService = false;

	public ServicesListAdapter(Context context, ArrayList<Policy> policies) {
		super();
		this.context = context;
		this.myPolicies = policies;
	}

	public ServicesListAdapter(Context context, ArrayList<String> helpList,
			boolean fromHelp) {
		super();
		this.context = context;
		this.helpList = helpList;
		this.fromService = fromHelp;
	}

	@Override
	public int getCount() {

		if (fromService) {
			return helpList.size();

		} else {
			return myPolicies.size();
		}
	}

	@Override
	public Object getItem(int position) {
		if (fromService) {
			return helpList.get(position);

		} else {
			return myPolicies.get(position);

		}
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
		ImageView pic = (ImageView) row.findViewById(R.id.picview);

		if (!fromService) {
			System.out.println();
			pic.setVisibility(View.GONE);
		} else {

		}
		TextView policyTitle = (TextView) row
				.findViewById(R.id.listpolicyheading);

		policyTitle.setText(helpList.get(position));

		return row;
	}

}

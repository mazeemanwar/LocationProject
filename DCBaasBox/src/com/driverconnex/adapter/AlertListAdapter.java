package com.driverconnex.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.driverconnex.app.R;
import com.driverconnex.app.R.color;
import com.driverconnex.data.Alert;
import com.driverconnex.data.Retailer;

public class AlertListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Alert> myServices;
	private ArrayList<String> helpList;
	private boolean fromHelp = false;
	private LayoutInflater mInflater;

	public AlertListAdapter(Context context, ArrayList<Alert> serivces) {
		super();
		this.context = context;
		this.myServices = serivces;
	}

	@Override
	public int getCount() {
		return myServices.size();
	}

	@Override
	public Object getItem(int position) {
		return myServices.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.list_alert_item, null);
		}

		TextView title = (TextView) convertView.findViewById(R.id.main);
		TextView subTitle = (TextView) convertView.findViewById(R.id.sub);
		// View divider = (View) convertView.findViewById(R.id.listDivider);
		// divider.setVisibility(View.GONE);

		String emailName = myServices.get(position).getMsg();
		String dis = String.valueOf(myServices.get(position).getVehicleId());

		String email = dis;
		// Set texts
		if (email != null && !email.equals("")) {
			title.setText(emailName);
			subTitle.setText(email);

		} else {
			// title.setText(myServices.get(position).getPhonename());
			// subTitle.setText(myServices.get(position).getPhone());

		}

		return convertView;
	}

}
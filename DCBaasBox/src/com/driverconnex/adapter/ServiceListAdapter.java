package com.driverconnex.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.driverconnex.app.R;
import com.driverconnex.data.Retailer;

public class ServiceListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Retailer> myServices;
	private ArrayList<String> helpList;
	private boolean fromHelp = false;
	private LayoutInflater mInflater;

	public ServiceListAdapter(Context context, ArrayList<Retailer> serivces) {
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
			convertView = mInflater.inflate(R.layout.list_item_divider, null);
		}

		TextView title = (TextView) convertView
				.findViewById(R.id.listMainTitle);
		TextView subTitle = (TextView) convertView
				.findViewById(R.id.listSubTitle);
		View divider = (View) convertView.findViewById(R.id.listDivider);
		// divider.setVisibility(View.GONE);

		String emailName = myServices.get(position).getName();
		String dis = String.valueOf(myServices.get(position).getDistance());
		dis = dis.substring(0, dis.lastIndexOf(".") + 2);
		System.out.println(dis);

		String email = dis + " Miles";
		// Set texts
		if (email != null && !email.equals("")) {
			title.setText(emailName);
			subTitle.setText(email);

		} else {
			// title.setText(myServices.get(position).getPhonename());
			// subTitle.setText(myServices.get(position).getPhone());

		}
		title.setTextSize(16);
		subTitle.setTextSize(14);
		return convertView;
	}

}

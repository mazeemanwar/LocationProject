package com.driverconnex.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.driverconnex.app.R;

public class CustomAlertAdapter extends ArrayAdapter<String> {
	Context context;
	int resource;
	ArrayList<String> list;

	public CustomAlertAdapter(Context context, int resource,
			ArrayList<String> list) {
		super(context, resource, list);
		this.context = context;
		this.resource = resource;
		this.list = list;

	}

	ViewHolder holder;
	Drawable icon;

	class ViewHolder {
		ImageView icon;
		TextView title;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		if (convertView == null) {

			// inflate the layout

			LayoutInflater inflater = ((Activity) context).getLayoutInflater();

			convertView = inflater.inflate(resource, parent, false);

			holder = new ViewHolder();
			holder.title = (TextView) convertView
					.findViewById(R.id.alert_list_row);
			convertView.setTag(holder);
		} else {
			// view already defined, retrieve view holder
			holder = (ViewHolder) convertView.getTag();
		}

		// Drawable drawable = getResources().getDrawable(R.drawable.list_icon);
		// // this
		// is
		// an
		// image
		// from
		// the
		// drawables
		// folder

		holder.title.setText(list.get(position));
		// holder.icon.setImageDrawable(drawable);

		return convertView;
	}

}

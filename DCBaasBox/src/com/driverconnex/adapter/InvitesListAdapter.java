package com.driverconnex.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.driverconnex.app.R;
import com.driverconnex.utilities.AssetsUtilities;
import com.driverconnex.vehicles.DCVehicle;

public class InvitesListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<DCVehicle> vehicles;
	private boolean vehiclePicker;

	public InvitesListAdapter(Context context, ArrayList<DCVehicle> vehicles,
			boolean vehiclePicker) {
		super();
		this.context = context;
		this.vehicles = vehicles;
		this.vehiclePicker = vehiclePicker;
	}

	@Override
	public int getCount() {
		return vehicles.size();
	}

	@Override
	public Object getItem(int position) {
		return vehicles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ListHolder holder = null;

		final int pos = position;

		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(context.LAYOUT_INFLATER_SERVICE);
			row = (View) inflater.inflate(R.layout.list_invite_activity, null);

			holder = new ListHolder();
			// holder.model = (TextView) row.findViewById(R.id.modelText);
			// holder.regNumber = (TextView)
			// row.findViewById(R.id.regNumberText);
			// holder.derivative = (TextView)
			// row.findViewById(R.id.derivativeText);
			// holder.alerts = (TextView) row.findViewById(R.id.alertsTextView);
			// holder.tag = (TextView) row.findViewById(R.id.defaultTag);
			// holder.pic = (ImageView) row.findViewById(R.id.pic);
			// holder.btn = (ImageView) row.findViewById(R.id.infoBtn);
			//
			// holder.btn.setOnClickListener(new OnClickListener()
			// {
			// @Override
			// public void onClick(View v)
			// {
			// Intent intent = new Intent(context,
			// VehicleDetailsActivity.class);
			// Bundle extras = new Bundle();
			// extras.putSerializable("vehicle", vehicles.get(pos));
			// extras.putInt("index", pos);
			//
			// intent.putExtras(extras);
			// ((Activity) context).startActivity(intent);
			// ((Activity)
			// context).overridePendingTransition(R.anim.slide_left_sub,
			// R.anim.slide_left_main);
			// }
			// });
			//
			// // If user is picking a vehicle than they shouldn't be able to
			// enter the vehicle activity
			if (vehiclePicker) {
				holder.btn.setVisibility(View.INVISIBLE);
				holder.alerts.setVisibility(View.INVISIBLE);
			}

			holder.model.setText(vehicles.get(position).getMake() + " "
					+ vehicles.get(position).getModel());
			holder.derivative.setText(vehicles.get(position).getDerivative());
			holder.regNumber.setText(vehicles.get(position).getRegistration()
					.toString());
			holder.alerts.setText(vehicles.get(position).getAlertsCount()
					+ " Alerts");

			if (vehicles.get(position).isCurrent())
				holder.tag.setVisibility(View.VISIBLE);
			else
				holder.tag.setVisibility(View.INVISIBLE);

			if (vehicles.get(position).getPhotoSrc() != null) {
				Bitmap bmp = AssetsUtilities.readBitmapVehicle(vehicles.get(
						position).getPhotoSrc());
				holder.pic.setImageBitmap(bmp);
			}

			row.setTag(holder);
		} else {
			holder = (ListHolder) row.getTag();
		}

		return row;
	}

	private static class ListHolder {
		TextView model;
		TextView regNumber;
		TextView derivative;
		TextView alerts;
		TextView tag;
		ImageView pic;
		ImageView btn;
	}
}

package com.driverconnex.adapter;

import java.util.ArrayList;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.driverconnex.app.R;
import com.driverconnex.journeys.DCJourney;
import com.driverconnex.journeys.JourneyDetailsActivity;
import com.driverconnex.journeys.ReviewJourneysActivity;
import com.driverconnex.utilities.Utilities;
import com.driverconnex.vehicles.VehicleDetailsActivity;

/**
 * Adapter for display a list of journeys. It takes ArrayList of DCJourney
 * containing information about DCJourneys and TreeSet of Integers used for
 * displaying a separator with the date.
 * 
 * @author Yin Lee (SGI)
 * @author Adrian Klimczak
 * 
 */

public class ListJourneyAdapter extends BaseAdapter {
	private static final int TYPE_ITEM = 0;
	private static final int TYPE_SEPARATOR = 1;
	private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

	private ArrayList<DCJourney> mData = new ArrayList<DCJourney>();
	private LayoutInflater mInflater;
	private Context mContext;

	private TreeSet<Integer> mSeparatorsSet;

	public ListJourneyAdapter(Context context, ArrayList<DCJourney> data,
			TreeSet<Integer> separatorsSet) {
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mData = data;
		mSeparatorsSet = separatorsSet;
	}

	@Override
	public int getItemViewType(int position) {
		return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}

	public int getCount() {
		return mData.size();
	}

	public DCJourney getItem(int position) {
		return mData.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean isEnabled(int position) {
		return mSeparatorsSet.contains(position) ? false : true;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		int type = getItemViewType(position);

		if (convertView == null) {
			holder = new ViewHolder();

			switch (type) {
			case TYPE_ITEM:
				convertView = mInflater.inflate(R.layout.list_item_icon, null);
				break;

			case TYPE_SEPARATOR:
				convertView = mInflater.inflate(R.layout.list_separator, null);
				break;
			}

			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		switch (type) {
		case TYPE_ITEM:
			// Get views
			holder.mainTitle = (TextView) convertView
					.findViewById(R.id.listMainTitle);
			holder.subTitle = (TextView) convertView
					.findViewById(R.id.listSubTitle);
			holder.icon = (ImageView) convertView.findViewById(R.id.listIcon);
			holder.info = (ImageView) convertView.findViewById(R.id.infoBtn);

			holder.info.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					Intent intent = new Intent(mContext,
							JourneyDetailsActivity.class);
					intent.putExtra("journey", mData.get(position));
					intent.putExtra("modify", true);
					((Activity) mContext).startActivity(intent);
					((Activity) mContext).overridePendingTransition(
							R.anim.slide_left_sub, R.anim.slide_left_main);

				}
			});

			if (mData.get(position).getDescription() != null
					&& mData.get(position).getDescription().length() != 0)
				holder.mainTitle.setText(mData.get(position).getDescription());
			else
				holder.mainTitle.setText(String.valueOf(mData.get(position)
						.getDistance()) + " Miles");

			holder.subTitle.setText(Utilities.formatMinutesIntoDays(mData.get(
					position).getDuration()));
			holder.icon.setImageResource(R.drawable.line_chart);

			if (mData.get(position).isSelected())
				convertView.setActivated(true);
			else
				convertView.setActivated(false);

			break;

		case TYPE_SEPARATOR:
			holder.mainTitle = (TextView) convertView
					.findViewById(R.id.textSeparator);
			holder.mainTitle.setText(mData.get(position).getCreateDate());
			break;
		}

		return convertView;
	}

	public static class ViewHolder {
		private TextView mainTitle;
		private TextView subTitle;
		private ImageView icon;
		private ImageView info;
	}

}

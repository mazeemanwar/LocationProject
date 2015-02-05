package com.driverconnex.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.driverconnex.app.R;
import com.driverconnex.utilities.ThemeUtilities;

/**
 * It's a generic adapter for displaying a list of items. It's adaptable to many
 * situations. It can display title, subtitle and icon. If there is no icon
 * specified then it will display only text. It takes ArrayList of
 * LidAdapterItem, which is the object designed specifically for this adapter.
 * Items can be selectable, so this adapter can be used when user is selecting
 * items on the list. Each ListAdapterItem has it's own attributes that can be
 * expanded e.g each ListAdapterItem can have it's own subtitle colour.
 * 
 * This adapter has three attributes: imageResource - a drawable resource for
 * the icon to be displayed. If not specified then icon will not be displayed.
 * titleStyle - it's used to style all titles as e.g. bold, italic etc.
 * titleColor - a colour resource used to set a colour for all titles.
 * 
 * Attributes can be set at any point. They don't have to be passed to the
 * constructor.
 * 
 * @author Adrian Klimczak
 * 
 */

public class ListAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mInflater;
	private ArrayList<ListAdapterItem> data;

	private int imageResource = -1;
	private int titleStyle = -1;
	private int titleColor = -1;

	/**
	 * Constructor for items with an icon and a title colour.
	 * 
	 * @param context
	 * @param data
	 * @param imageResource
	 * @param titleColor
	 */
	public ListAdapter(Context context, ArrayList<ListAdapterItem> data,
			int imageResource, int titleColor) {
		this.data = data;
		this.imageResource = imageResource;
		this.context = context;
		this.titleColor = titleColor;
	}

	/**
	 * Constructor for items with an icon.
	 * 
	 * @param context
	 * @param data
	 * @param imageResource
	 */
	public ListAdapter(Context context, ArrayList<ListAdapterItem> data,
			int imageResource) {
		this.data = data;
		this.imageResource = imageResource;
		this.context = context;
	}

	/**
	 * Basic constructor for item with no icon.
	 * 
	 * @param context
	 * @param data
	 */
	public ListAdapter(Context context, ArrayList<ListAdapterItem> data) {
		this.data = data;
		this.context = context;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// If there is no icon
			if (imageResource == -1)
				convertView = mInflater.inflate(R.layout.list_item_divider,
						null);
			else
				convertView = mInflater.inflate(R.layout.list_item_icon, null);
		}

		// Get views from the layout
		TextView title = (TextView) convertView
				.findViewById(R.id.listMainTitle);
		TextView subTitle = (TextView) convertView
				.findViewById(R.id.listSubTitle);
		View divider = (View) convertView.findViewById(R.id.listDivider);

		// Set texts
		title.setText(data.get(position).title);

		// Check if there is a subtitle
		if (data.get(position).subtitle != null)
			subTitle.setText(data.get(position).subtitle);
		else
			subTitle.setText("");

		// Set icon if it exists
		if (imageResource != -1) {
			ImageView icon = (ImageView) convertView
					.findViewById(R.id.listIcon);
			icon.setImageDrawable(context.getResources().getDrawable(
					imageResource));
		}

		// Set colour for all titles if it was specified
		if (titleColor != -1) {
			// Tries to set colour
			try {
				title.setTextColor(titleColor);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
		}

		// Set colour for each individual subtitle if it was specified
		try {
			subTitle.setTextColor(context.getResources().getColor(
					data.get(position).subtitleColor));
		} catch (NotFoundException e) {
			e.printStackTrace();
			subTitle.setTextColor(context.getResources().getColor(
					ThemeUtilities.getMainTextColor()));
		}

		if (titleStyle != -1)
			title.setTypeface(null, titleStyle);

		// If item is selectable it checks if it's selected
		if (data.get(position).selected)
			convertView.setActivated(true);
		else
			convertView.setActivated(false);

		// The last item in the list shouldn't have divider
		if (position == data.size() - 1)
			divider.setVisibility(View.INVISIBLE);
		else
			divider.setVisibility(View.VISIBLE);

		return convertView;
	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	// ------------------------------------
	/**
	 * Gets currently used style for all titles
	 * 
	 * @return
	 */
	public int getTitleStyle() {
		return titleStyle;
	}

	/**
	 * Sets style for all items.
	 * 
	 * @param titleStyle
	 */
	public void setTitleStyle(int titleStyle) {
		this.titleStyle = titleStyle;
	}

	/**
	 * Gets currently used icon for all items.
	 * 
	 * @return
	 */
	public int getImageResource() {
		return imageResource;
	}

	/**
	 * Sets icon for all items.
	 * 
	 * @param imageResource
	 */
	public void setImageResource(int imageResource) {
		this.imageResource = imageResource;
	}

	/**
	 * Gets current colour used for all titles.
	 * 
	 * @return
	 */
	public int getTitleColor() {
		return titleColor;
	}

	/**
	 * Sets colour for all titles.
	 * 
	 * @param titleColor
	 */
	public void setTitleColor(int titleColor) {
		this.titleColor = titleColor;
	}

	/**
	 * Gets adapter data.
	 * 
	 * @return
	 */
	public ArrayList<ListAdapterItem> getData() {
		return data;
	}

	/**
	 * Sets adapter data.
	 * 
	 * @param data
	 */
	public void setData(ArrayList<ListAdapterItem> data) {
		this.data = data;
	}
}

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
import com.driverconnex.data.MenuListItem;

/**
 * Adapter used for displaying menu items on the drawer. 
 * @author Yin Lee (SGI)
 *
 */

public class NavigationDrawerAdapter extends BaseAdapter
{
	private Context context;
	private ArrayList<MenuListItem> menuList;
	
	public NavigationDrawerAdapter(Context context, ArrayList<MenuListItem> menuList) 
	{
		super();
		this.context = context;
		this.menuList = menuList;
	}

	@Override
	public int getCount() {
		return menuList.size();
	}

	@Override
	public Object getItem(int position) {
		return menuList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public boolean isEnabled(int position) 
	{
		MenuListItem item = menuList.get(position);
		return !item.isHeader();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		MenuListItem item = menuList.get(position);
		View row = null;
		TextView title = null;
		
		if (item.isHeader()) 
		{
			row = (View) inflater.inflate(R.layout.drawer_header_list_item, null);
			title = (TextView) row.findViewById(R.id.headerTitle);
		} 
		else 
		{
			row = (View) inflater.inflate(R.layout.drawer_list_item, null);
			title = (TextView) row.findViewById(R.id.title);
			ImageView icon = (ImageView) row.findViewById(R.id.icon);
			icon.setImageResource(context.getResources().getIdentifier(item.getIcon(), "drawable", context.getPackageName()));
		}
		
		title.setText(item.getName());

		return row;
	}

}

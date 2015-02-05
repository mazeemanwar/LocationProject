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

/**
 * Adapter for the user DCBadges. It takes ArrayList of Bitmaps and String[][] as a argument. ArrayList of Bitmaps is used to display icons of the badges and String[][] contains information
 * about each badge. Currently String[][0] is only used for displaying a title of the badge on the grid.  
 * @author Adrian Klimczak
 *
 */

public class BadgeAdapter extends BaseAdapter 
{
    private LayoutInflater mInflater;
    private Context context;
    
    private ArrayList<Bitmap> data; 
    private String[][] badge;
    
    public BadgeAdapter(Context context, ArrayList<Bitmap> data, String[][] badge) 
    {
    	this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        this.badge = badge;
    }

    public View getView(int position, View convertView, ViewGroup parent) 
    {
        if (convertView == null) 
            convertView = mInflater.inflate(R.layout.grid_item_badge, null);
        
        ImageView photo = (ImageView) convertView.findViewById(R.id.gridPhoto);
        TextView title = (TextView) convertView.findViewById(R.id.gridTitle);
        
        // Displays icon of the badge
        photo.setImageBitmap(data.get(position));  
        
        // Displays title of the badge
        title.setText(badge[position][0]);
        
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
}

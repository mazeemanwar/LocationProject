package com.driverconnex.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.driverconnex.app.R;

/**
 * Adapter used for displaying a grid of photos. It takes ArrayList of bitmaps used to display photos.
 * @author Adrian Klimczak
 *
 */
public class PhotoGridAdapter extends BaseAdapter 
{
    private LayoutInflater mInflater;
    private Context context;
    
    private ArrayList<Bitmap> data; 
    
    public PhotoGridAdapter(Context context, ArrayList<Bitmap> data) 
    {
    	this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
    }

    public View getView(int position, View convertView, ViewGroup parent) 
    {
        if (convertView == null) 
            convertView = mInflater.inflate(R.layout.grid_item_photo, null);
        
        ImageView photo = (ImageView) convertView.findViewById(R.id.gridPhoto);
        photo.setImageBitmap(data.get(position));  
        
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

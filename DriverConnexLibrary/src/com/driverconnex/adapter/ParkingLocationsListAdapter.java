package com.driverconnex.adapter;

import java.util.ArrayList;
import java.util.TreeSet;

import android.content.Context;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.driverconnex.app.R;
import com.driverconnex.parking.ParkingLocation;
import com.driverconnex.utilities.LocationUtilities;

/**
 * Adapter used for displaying a list of parking locations saved by the user in the local database. 
 * It's no longer used. I keep it for the reference in case it will be needed in the future.
 * @author Adrian Klimczak
 *
 */
public class ParkingLocationsListAdapter extends BaseAdapter
{
	private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

    private ArrayList<ParkingLocation> mData = new ArrayList<ParkingLocation>();
    private LayoutInflater mInflater;
    private Context mContext;

    private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();

    public ParkingLocationsListAdapter(Context context, ArrayList<ParkingLocation> data) 
    {
    	mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mData = data;
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

    public ParkingLocation getItem(int position) {
        return mData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    
    @Override
	public boolean isEnabled(int position) {
		return mSeparatorsSet.contains(position) ? false : true;
	}

    public View getView(int position, View convertView, ViewGroup parent) 
    {
        if (convertView == null) 
        {
            convertView = mInflater.inflate(R.layout.list_item, null);
            TextView mainTitle = (TextView)convertView.findViewById(R.id.listMainTitle);
            TextView subTitle = (TextView) convertView.findViewById(R.id.listSubTitle);
            
            Address address = LocationUtilities.getAddress(mContext, mData.get(position).getLatitude(),mData.get(position).getLongitude());
            
            if(address != null)
            	mainTitle.setText(address.getAddressLine(0));
            else
            	mainTitle.setText("Loading address...");
            
            subTitle.setText(mData.get(position).getDate());                       
        } 
        
        return convertView;
    }
}

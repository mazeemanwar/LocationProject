package com.driverconnex.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.driverconnex.app.R;
import com.driverconnex.vehicles.DCCover;

/**
 * Adapter for displaying insurance cover list. It takes ArrayList of DCCover containing information about DCCover and defaultPolicyIndex, 
 * which is the index of the default insurance cover.
 * @author Adrian Klimczak
 *
 */

public class ListDCCoverAdapter extends BaseAdapter 
{
    private static final int TYPE_SEPARATOR = 1;
    private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

    private ArrayList<DCCover> mData = new ArrayList<DCCover>();
    private LayoutInflater mInflater;
    private Context context;
    
    private ArrayList<CheckBox> checkBoxes = new ArrayList<CheckBox>();
    
    private int defaultPolicyIndex;
    
    public ListDCCoverAdapter(Context context, ArrayList<DCCover> data, int defaultPolicyIndex) 
    {
    	this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mData = data;
        this.defaultPolicyIndex = defaultPolicyIndex;
    }

    public View getView(int position, View convertView, ViewGroup parent) 
    {
        if (convertView == null) 
        {
            convertView = mInflater.inflate(R.layout.list_item_checkbox, null);
            
            // Get views from the layout
            TextView policyProvider = (TextView)convertView.findViewById(R.id.policyProviderTextView);
            TextView details = (TextView) convertView.findViewById(R.id.detailsTextView);
            View divider = (View) convertView.findViewById(R.id.divider);
            ImageView icon = (ImageView) convertView.findViewById(R.id.listIcon);
            final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            
            checkBoxes.add(checkBox);
 
            // Set texts
            policyProvider.setText(mData.get(position).getPolicyProvider());
            
            String str = "£" + mData.get(position).getAnualCost() + ", Expires in " + mData.get(position).getExpiryDate();
            details.setText(str);
            
            // Set icon
            icon.setImageResource(R.drawable.documents_grey_44x56);
            
            // The last item in the list shouldn't have divider
            if(position == mData.size()-1)
            	divider.setVisibility(View.INVISIBLE);     
            
            // Tick the box to indicate that this cover is a default cover
            if(defaultPolicyIndex >= 0 && defaultPolicyIndex < checkBoxes.size())
            	checkBoxes.get(defaultPolicyIndex).setChecked(true);
        } 
        
        return convertView;
    }
    
    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    public int getCount() {
        return mData.size();
    }

    public DCCover getItem(int position) {
        return mData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    
    public ArrayList<CheckBox> getCheckBoxes() {
    	return checkBoxes;
    }
}

package com.driverconnex.adapter;

import java.util.ArrayList;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.driverconnex.app.R;
import com.driverconnex.expenses.AddExpenseActivity;
import com.driverconnex.expenses.AddFuelActivity;
import com.driverconnex.expenses.AddTravelSubsistenceActivity;
import com.driverconnex.expenses.DCExpense;

/**
 * Adapter for displaying list of expenses.
 * @author Yin Lee (SGI)
 * @author Adrian Klimczak
 */

public class ExpenseListAdapter extends BaseAdapter 
{
	private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

    private ArrayList<DCExpense> mData = new ArrayList<DCExpense>();
    private LayoutInflater mInflater;
    private Context mContext;

    private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();

    public ExpenseListAdapter(Context context, ArrayList<DCExpense> data, TreeSet<Integer> separatorsSet) 
    {
    	mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    public DCExpense getItem(int position) {
        return mData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    
    @Override
	public boolean isEnabled(int position) {
		return mSeparatorsSet.contains(position) ? false : true;
	}

    public View getView(final int position, View convertView, ViewGroup parent) 
    {
        ViewHolder holder = null;
        int type = getItemViewType(position);

        if (convertView == null) 
        {
            holder = new ViewHolder();
            switch (type) 
            {
                case TYPE_ITEM:
                    convertView = mInflater.inflate(R.layout.list_item_info, null);
                    holder.mainTitle = (TextView)convertView.findViewById(R.id.listMainTitle);
                    holder.subTitle = (TextView) convertView.findViewById(R.id.listSubTitle);
                    holder.infoBtn = (ImageView) convertView.findViewById(R.id.listInfoBtn);
                    		
                    holder.mainTitle.setText(mData.get(position).getType());
                    
                    if(mData.get(position).getCurrency().contains("GBP"))
                    	holder.subTitle.setText("£ " + String.valueOf(mData.get(position).getSpend()));
                    else if(mData.get(position).getCurrency().contains("EURO"))
                    	holder.subTitle.setText("€ " + String.valueOf(mData.get(position).getSpend()));
                    
                    holder.icon = (ImageView) convertView.findViewById(R.id.listIcon);
                    holder.icon.setImageResource(R.drawable.money_grey_56x46);

                    holder.infoBtn.setOnClickListener(new OnClickListener() 
        			{
        				@Override
        				public void onClick(View v) 
        				{
        					Intent intent = new Intent();

        					Bundle extra = new Bundle();
        					extra.putParcelable("expense", mData.get(position));
        					
        					if (mData.get(position).getType().equals("Travel and Subsistence")) 
        						intent.setClass(mContext, AddTravelSubsistenceActivity.class);
        					else if (mData.get(position).getType().equals("Fuel")) 
        						intent.setClass(mContext, AddFuelActivity.class);
        					else
            					intent.setClass(mContext, AddExpenseActivity.class);
        					
        					intent.putExtras(extra);
        					mContext.startActivity(intent);
        					((Activity) mContext).overridePendingTransition(R.anim.slide_left_sub, R.anim.slide_left_main);	
        				}
        			});
                    
                    break;
                case TYPE_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.list_separator, null);
                    holder.mainTitle = (TextView)convertView.findViewById(R.id.textSeparator);
                    holder.mainTitle.setText(mData.get(position).getDateString());
                    break;
            }
            
            convertView.setTag(holder);        
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        
        if(mData.get(position).isSelected())
        	convertView.setActivated(true);
        else
        	convertView.setActivated(false);
        
        return convertView;
    }
    
	public static class ViewHolder 
	{
		private TextView mainTitle;
		private TextView subTitle;
		private ImageView icon;
		private ImageView infoBtn;
	}

}

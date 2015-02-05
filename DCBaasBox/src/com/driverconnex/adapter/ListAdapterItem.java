package com.driverconnex.adapter;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Item used for the ListAdapter. This ListAdapterItem is parcelable, so it can be passed from one activity to another.
 * It can be freely expanded with any new features that can be used in ListAdapter.
 * 
 * Attributes:
 * title - title of the item
 * subtitle - sub title of the item
 * subtitleColor - color for the subtitle
 * 
 * Other:
 * selected - It's used to indicate if item is selected
 * 
 * 
 * @author Adrian Klimczak
 *
 */
public class ListAdapterItem implements Parcelable 
{
	public String title;
	public String subtitle;
	public int subtitleColor;
	
	public boolean selected = false;
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) 
	{
		dest.writeString(title);
		dest.writeString(subtitle);
		dest.writeInt(subtitleColor);
	}
	
	public static final Parcelable.Creator<ListAdapterItem> CREATOR = new Parcelable.Creator<ListAdapterItem>() 
	{
		public ListAdapterItem createFromParcel(Parcel in) 
		{
			ListAdapterItem item = new ListAdapterItem();

			item.title = in.readString();
			item.title = in.readString();
			item.subtitleColor = in.readInt();
			
			return item;
		}

		public ListAdapterItem[] newArray(int size) {
			return new ListAdapterItem[size];
		}
	};
}

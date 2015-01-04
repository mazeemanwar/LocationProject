package com.driverconnex.vehicles;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Reproduction of DCServiceItem object in the Parse database
 * @author Adrian Klimczak
 *
 */

public class DCServiceItem implements Parcelable 
{
	private String id;
	private String name;
	private String description;
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) 
	{
		dest.writeString(id);
		dest.writeString(name);
		dest.writeString(description);
	}
	
	public static final Parcelable.Creator<DCServiceItem> CREATOR = new Parcelable.Creator<DCServiceItem>() 
	{
		public DCServiceItem createFromParcel(Parcel in) 
		{
			DCServiceItem item = new DCServiceItem();
			item.id = in.readString();
			item.name = in.readString();
			item.description = in.readString();
			return item;
		}

		public DCServiceItem[] newArray(int size) {
			return new DCServiceItem[size];
		}
	};
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}

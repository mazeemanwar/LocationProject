package com.driverconnex.parking;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parking location. This object is saved in SQL database. 
 * @author Adrian Klimczak
 * @author Muhammad Azeem Anwar
 *
 */

public class ParkingLocation implements Parcelable
{
	private long id;
	private String date;
	private double latitude;
	private double longitude;
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) 
	{
		dest.writeLong(id);
		dest.writeString(date);
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
	}
	
	public static final Parcelable.Creator<ParkingLocation> CREATOR = new Parcelable.Creator<ParkingLocation>() 
	{
		public ParkingLocation createFromParcel(Parcel in) 
		{
			ParkingLocation mJourney = new ParkingLocation();
			mJourney.id = in.readLong();
			mJourney.date = in.readString();
			mJourney.latitude = in.readDouble();
			mJourney.longitude = in.readDouble();
			return mJourney;
		}

		public ParkingLocation[] newArray(int size) {
			return new ParkingLocation[size];
		}
	};
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}	
}

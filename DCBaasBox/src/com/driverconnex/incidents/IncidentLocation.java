package com.driverconnex.incidents;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class IncidentLocation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String date;
	private double latitude;
	private double longitude;
	private String descriptioin;
	private String vehicleReg;

	public String getVehicleReg() {
		return vehicleReg;
	}

	public void setVehicleReg(String vehicleReg) {
		this.vehicleReg = vehicleReg;
	}

	public String getDescriptioin() {
		return descriptioin;
	}

	public void setDescriptioin(String descriptioin) {
		this.descriptioin = descriptioin;
	}

	public static final Parcelable.Creator<IncidentLocation> CREATOR = new Parcelable.Creator<IncidentLocation>() {
		public IncidentLocation createFromParcel(Parcel in) {
			IncidentLocation mIncident = new IncidentLocation();
			mIncident.id = in.readLong();

			mIncident.date = in.readString();
			mIncident.descriptioin = in.readString();
			mIncident.latitude = in.readDouble();
			mIncident.longitude = in.readDouble();
			mIncident.vehicleReg = in.readString();

			return mIncident;
		}

		public IncidentLocation[] newArray(int size) {
			return new IncidentLocation[size];
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

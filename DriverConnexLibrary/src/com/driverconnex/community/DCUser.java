package com.driverconnex.community;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Reproduction of user from the Parse database.
 * @author Adrian Klimczak
 *
 */

public class DCUser implements Serializable
{
	private static final long serialVersionUID = -9057010723570579927L;
	
	private String id;
	private String firstName;
	private String lastName;
	private String status;
	private String updateDate;
	private double longitude;
	private double latitude;
	
	private byte[] photoSrc;
	
	private boolean photo = false;
	private boolean isTracking = false;
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public byte[] getPhotoSrc() {
		return photoSrc;
	}

	public void setPhotoSrc(byte[] photoSrc) {
		this.photoSrc = photoSrc;
	}

	public boolean isPhoto() {
		return photo;
	}

	public void setPhoto(boolean photo) {
		this.photo = photo;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date date) 
	{
		if (date != null) 
		{
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
			this.updateDate = format.format(date);
		}
	}
	
	public void setCurrentLocation(double latitude, double longitude) 
	{
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isTracking() {
		return isTracking;
	}

	public void setTracking(boolean isTracking) {
		this.isTracking = isTracking;
	}
	
	
}

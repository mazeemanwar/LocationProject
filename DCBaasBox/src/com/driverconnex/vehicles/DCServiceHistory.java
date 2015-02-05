package com.driverconnex.vehicles;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Class representing DCServiceHistory from the Parse database.
 * @author Adrian Klimczak
 *
 */

public class DCServiceHistory implements Serializable
{
	private static final long serialVersionUID = -3606898898947902696L;
	
	private String id;
	private ArrayList<DCServiceItem> serviceItems;
	private String date;
	private String type;
	private int cost;
	private long mileage;
	private byte[] photo;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public ArrayList<DCServiceItem> getServiceItems() {
		return serviceItems;
	}
	public void setServiceItems(ArrayList<DCServiceItem> serviceItems) {
		this.serviceItems = serviceItems;
	}
	public String getDate() {
		return date;
	}
	
	public void setDate(Date date) 
	{
		if (date != null) 
		{
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			this.date = format.format(date);
		}
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}
	public long getMileage() {
		return mileage;
	}
	public void setMileage(long mileage) {
		this.mileage = mileage;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public byte[] getPhoto() {
		return photo;
	}
	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}
}

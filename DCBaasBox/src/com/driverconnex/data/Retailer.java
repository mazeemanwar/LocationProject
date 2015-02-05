package com.driverconnex.data;

import android.location.Location;

public class Retailer implements Comparable {

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	private String address;
	private String address2;
	
	private String town;
	private String postalCode;
	
	
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Float getDistance() {
		return distance;
	}

	public void setDistance(Float distance) {
		this.distance = distance;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	public Float getLat() {
		return lat;
	}

	public void setLat(Float lat) {
		this.lat = lat;
	}

	public Float getLog() {
		return log;
	}

	public void setLog(Float log) {
		this.log = log;
	}

	private Float lat;	private Float log;
	
	private Float distance;
	private Location location;

	public Retailer(String name, Float distance, Location locaiton) {
		this.name = name;
		this.distance = distance;
		this.location = locaiton;
	}

	public Retailer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		Retailer f = (Retailer) o;

		if (distance.floatValue() > f.distance.floatValue()) {
			return 1;
		} else if (distance.floatValue() < f.distance.floatValue()) {
			return -1;
		} else {
			return 0;
		}
	}

}

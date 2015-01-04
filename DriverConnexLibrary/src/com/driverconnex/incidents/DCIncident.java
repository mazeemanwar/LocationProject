package com.driverconnex.incidents;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.driverconnex.vehicles.DCVehicle;

/**
 * Reproduction of DCIncident object from Parse database
 * @author Adrian Klimczak
 *
 */

public class DCIncident implements Serializable
{
	private static final long serialVersionUID = -4980838106266466198L;
	
	private String id;
	private String date;
	private double latitude;  		   // Latitude of location
	private double longitude; 	 	   // Longitude of location
	private String description;
	private DCVehicle vehicle;         // Vehicle that engaged in incident
	private String incidentUser;
	
	private boolean selected;
	private boolean videoAttached;
	private boolean photosAttached;
	
	private int numberPhotos;
	
	private List<Map<String,String>> witnesses;
	private String vehicleReg;
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public void setDate(Date date) 
	{
		if (date != null) 
		{
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy, HH:mm");
			this.date = format.format(date);
		}
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public DCVehicle getVehicle() {
		return vehicle;
	}
	public void setVehicle(DCVehicle vehicle) {
		this.vehicle = vehicle;
	}

	public String getIncidentUser() {
		return incidentUser;
	}

	public void setIncidentUser(String incidentUser) {
		this.incidentUser = incidentUser;
	}
	
	public boolean isVideoAttached() {
		return videoAttached;
	}

	public void setVideoAttached(boolean videoAttached) {
		this.videoAttached = videoAttached;
	}

	public boolean isPhotosAttached() {
		return photosAttached;
	}

	public void setPhotosAttached(boolean photosAttached) {
		this.photosAttached = photosAttached;
	}

	public List<Map<String, String>> getWitnesses() {
		return witnesses;
	}

	public void setWitnesses(List<Map<String, String>> witnesses) {
		this.witnesses = witnesses;
	}

	public String getVehicleReg() {
		return vehicleReg;
	}

	public void setVehicleReg(String vehicleReg) {
		this.vehicleReg = vehicleReg;
	}

	public int getNumberPhotos() {
		return numberPhotos;
	}

	public void setNumberPhotos(int numberPhotos) {
		this.numberPhotos = numberPhotos;
	}
}

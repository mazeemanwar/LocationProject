package com.driverconnex.vehicles;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Reproduction of DC Cover object from Parse database.
 * 
 * @author Adrian Klimczak
 * 
 */

public class DCCover implements Serializable {
	private static final long serialVersionUID = 8544427697700105495L;

	private String id;
	private int anualCost;
	private boolean coverBreakdown;
	private String expiryDate;
	private String policyProvider;
	private byte[] photoSrc;

	// Getters and Setters
	// ========================================
	public String getExpiryDate() {
		return expiryDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getAnualCost() {
		return anualCost;
	}

	public void setAnualCost(int anualCost) {
		this.anualCost = anualCost;
	}

	public boolean isCoverBreakdown() {
		return coverBreakdown;
	}

	public void setCoverBreakdown(boolean coverBreakdown) {
		this.coverBreakdown = coverBreakdown;
	}

	// public void setExpiryDate(Date date)
	// {
	// if (date != null)
	// {
	// SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
	// this.expiryDate = format.format(date);
	// }
	// }

	public void setExpiryDate(String date) {

		this.expiryDate = date;
	}

	public String getPolicyProvider() {
		return policyProvider;
	}

	public void setPolicyProvider(String policyProvider) {
		this.policyProvider = policyProvider;
	}

	public byte[] getPhotoSrc() {
		return photoSrc;
	}

	public void setPhotoSrc(byte[] photoSrc) {
		this.photoSrc = photoSrc;
	}
}

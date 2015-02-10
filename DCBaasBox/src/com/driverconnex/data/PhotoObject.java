package com.driverconnex.data;

import java.io.Serializable;

public class PhotoObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private byte[] photoByte;
	private byte[] videoByte;
	public byte[] getVideoByte() {
		return videoByte;
	}

	public void setVideoByte(byte[] videoByte) {
		this.videoByte = videoByte;
	}

	private long id;
	private long incidentId;

	public long getIncidentId() {
		return incidentId;
	}

	public void setIncidentId(long incidentId) {
		this.incidentId = incidentId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public byte[] getPhotoByte() {
		return photoByte;
	}

	public void setPhotoByte(byte[] photoByte) {
		this.photoByte = photoByte;
	}

}

package com.driverconnex.journeys;

/**
 * Class representing a DCBehaviourPoint
 * @author Adrian Klimczak
 *
 */

public class DCBehaviourPoint 
{
	private long id;
	private long journeyId;
	private double accelerationX;
	private double accelerationY;
	private double accelerationZ;
	private String activity;
	private String date;
	private boolean deviceFlat;
	private boolean deviceLandscape;
	private boolean devicePortrait;
	private double pitch;
	private double roll;
	private double yaw;
	private float speed;
	
	
	//-----------------------------------
	public double getAccelerationX() {
		return accelerationX;
	}
	public void setAccelerationX(double accelerationX) {
		this.accelerationX = accelerationX;
	}
	public double getAccelerationY() {
		return accelerationY;
	}
	public void setAccelerationY(double accelerationY) {
		this.accelerationY = accelerationY;
	}
	public double getAccelerationZ() {
		return accelerationZ;
	}
	public void setAccelerationZ(double accelerationZ) {
		this.accelerationZ = accelerationZ;
	}
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public boolean isDeviceFlat() {
		return deviceFlat;
	}
	public void setDeviceFlat(boolean deviceFlat) {
		this.deviceFlat = deviceFlat;
	}
	public boolean isDeviceLandscape() {
		return deviceLandscape;
	}
	public void setDeviceLandscape(boolean deviceLandscape) {
		this.deviceLandscape = deviceLandscape;
	}
	public boolean isDevicePortrait() {
		return devicePortrait;
	}
	public void setDevicePortrait(boolean devicePortrait) {
		this.devicePortrait = devicePortrait;
	}
	public double getPitch() {
		return pitch;
	}
	public void setPitch(double pitch) {
		this.pitch = pitch;
	}
	public double getRoll() {
		return roll;
	}
	public void setRoll(double roll) {
		this.roll = roll;
	}
	public double getYaw() {
		return yaw;
	}
	public void setYaw(double yaw) {
		this.yaw = yaw;
	}
	public float getSpeed() {
		return speed;
	}
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getJourneyId() {
		return journeyId;
	}
	public void setJourneyId(long journeyId) {
		this.journeyId = journeyId;
	}
}

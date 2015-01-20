package com.driverconnex.journeys;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.driverconnex.app.DriverConnexApp;
import com.driverconnex.utilities.Utilities;

/**
 * Class for representing a DCJourney
 * 
 * @author Adrian Klimczak
 * 
 */

public class DCJourney implements Parcelable {
	private long id;
	private String description;
	private String startTime;
	private String endTime;
	private String startAddr;
	private String endAddr;
	private String createDate;
	private String vehicle;
	private String behaviourScore;

	private long duration;
	private double distance;
	private double expense;

	private float avgSpeed;
	private float topSpeed;
	private float emissions = 0;

	private boolean trackedAutomatically;
	private boolean validBehaviour;
	private boolean manuallyAdded;
	private boolean scoreAdded;
	private boolean claimedFor;
	private boolean isBusiness;

	private boolean selected = false;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);

		dest.writeString(description);
		dest.writeString(startTime);
		dest.writeString(endTime);
		dest.writeString(startAddr);
		dest.writeString(endAddr);
		dest.writeString(createDate);
		dest.writeString(vehicle);
		dest.writeString(behaviourScore);

		dest.writeLong(duration);

		dest.writeDouble(distance);
		dest.writeDouble(expense);

		dest.writeFloat(topSpeed);
		dest.writeFloat(avgSpeed);
		dest.writeFloat(emissions);

		dest.writeByte((byte) (trackedAutomatically ? 0x01 : 0x00));
		dest.writeByte((byte) (validBehaviour ? 0x01 : 0x00));
		dest.writeByte((byte) (manuallyAdded ? 0x01 : 0x00));
		dest.writeByte((byte) (claimedFor ? 0x01 : 0x00));
		dest.writeByte((byte) (isBusiness ? 0x01 : 0x00));
		dest.writeByte((byte) (scoreAdded ? 0x01 : 0x00));
	}

	public static final Parcelable.Creator<DCJourney> CREATOR = new Parcelable.Creator<DCJourney>() {
		public DCJourney createFromParcel(Parcel in) {
			DCJourney mJourney = new DCJourney();
			mJourney.id = in.readLong();

			mJourney.description = in.readString();
			mJourney.startTime = in.readString();
			mJourney.endTime = in.readString();
			mJourney.startAddr = in.readString();
			mJourney.endAddr = in.readString();
			mJourney.createDate = in.readString();
			mJourney.vehicle = in.readString();
			mJourney.behaviourScore = in.readString();

			mJourney.duration = in.readLong();

			mJourney.distance = in.readDouble();
			mJourney.expense = in.readDouble();

			mJourney.topSpeed = in.readFloat();
			mJourney.avgSpeed = in.readFloat();
			mJourney.emissions = in.readFloat();

			mJourney.trackedAutomatically = in.readByte() != 0x00;
			mJourney.validBehaviour = in.readByte() != 0x00;
			mJourney.manuallyAdded = in.readByte() != 0x00;
			mJourney.claimedFor = in.readByte() != 0x00;
			mJourney.isBusiness = in.readByte() != 0x00;
			mJourney.scoreAdded = in.readByte() != 0x00;

			return mJourney;
		}

		public DCJourney[] newArray(int size) {
			return new DCJourney[size];
		}
	};

	private double roundTwoDecimals(double f) {
		double res = (double) (Math.round(f * 100)) / 100;
		return res;
	}

	private float roundTwoDecimals(float f) {
		float res = (float) (Math.round(f * 100)) / 100;
		return res;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public void setStartTime(String date, String time) {
		this.startTime = date + " " + time;
		this.createDate = date;
	}

	public void setStartTime(long startTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
		Date start = new Date(startTime);
		this.startTime = sdf.format(start);
		this.createDate = sd.format(start);
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public void setEndTime(String date, String time) {
		this.endTime = date + " " + time;
	}

	public void setEndTime(long endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		Date end = new Date(endTime);
		this.endTime = sdf.format(end);
	}

	public String getStartAddr() {
		return startAddr;
	}

	public void setStartAddr(String startAddr) {
		this.startAddr = startAddr;
	}

	public String getEndAddr() {
		return endAddr;
	}

	public void setEndAddr(String endAddr) {
		this.endAddr = endAddr;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.emissions = 0;
		this.expense = roundTwoDecimals(distance
				* DriverConnexApp.getUserPref().getExpensePerMile());
		this.distance = roundTwoDecimals(distance);
	}

	public boolean isBusiness() {
		return isBusiness;
	}

	public void setBusiness(boolean business) {
		this.isBusiness = business;
	}

	public double getExpense() {
		return expense;
	}

	public void setExpense(double expense) {
		this.expense = expense;
	}

	public Object getValuesByOrder(int order) {
		switch (order) {
		case 0:
			return getStartTime();
		case 1:
			return getEndTime();
		case 2:
			return Utilities.formatMinutesIntoDays(getDuration());
		case 3:
			return getDistance() + " Miles";
		case 4:
			return "£" + getExpense();
		case 5:
			return "Getting address...";
		case 6:
			return "Getting address...";
		case 7:
			return roundTwoDecimals(avgSpeed) + " mph";
			// case 8:
			// return roundTwoDecimals(topSpeed) + " mph";
			//
		case 8:
			return 0;
		case 9:
			return roundTwoDecimals(emissions) + " grams";
		default:
			return null;
		}
	}

	// Getters and Setters
	// ==============================================
	public float getEmissions() {
		return emissions;
	}

	public float getRoundedEmissions() {
		return (float) roundTwoDecimals(emissions);
	}

	public void setEmissions(float emissions) {
		this.emissions = emissions;
	}

	public double getAvgSpeed() {
		return avgSpeed;
	}

	public void setAvgSpeed(float avgSpeed) {
		this.avgSpeed = avgSpeed;
	}

	public double getTopSpeed() {
		return topSpeed;
	}

	public void setTopSpeed(float topSpeed) {
		this.topSpeed = topSpeed;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getVehicle() {
		return vehicle;
	}

	public void setVehicle(String vehicle) {
		this.vehicle = vehicle;
	}

	public boolean isTrackedAutomatically() {
		return trackedAutomatically;
	}

	public void setTrackedAutomatically(boolean trackedAutomatically) {
		this.trackedAutomatically = trackedAutomatically;
	}

	public boolean isValidBehaviour() {
		return validBehaviour;
	}

	public void setValidBehaviour(boolean validBehaviour) {
		this.validBehaviour = validBehaviour;
	}

	public boolean isManuallyAdded() {
		return manuallyAdded;
	}

	public void setManuallyAdded(boolean manuallyAdded) {
		this.manuallyAdded = manuallyAdded;
	}

	public boolean isScoreAdded() {
		return scoreAdded;
	}

	public void setScoreAdded(boolean scoreAdded) {
		this.scoreAdded = scoreAdded;
	}

	public boolean isClaimedFor() {
		return claimedFor;
	}

	public void setClaimedFor(boolean claimedFor) {
		this.claimedFor = claimedFor;
	}

	public String getBehaviourScore() {
		return behaviourScore;
	}

	public void setBehaviourScore(String behaviourScore) {
		this.behaviourScore = behaviourScore;
	}

}

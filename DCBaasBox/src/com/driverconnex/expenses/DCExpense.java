package com.driverconnex.expenses;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * DCExpense object, that represents an expense. It can be passed to other activities since is parcelable.
 * @author Yin Lee (SGI)
 * @author Adrian Klimczak
 */

public class DCExpense implements Parcelable
{
	private long id;
	
	private String type;
	private String date;
	private String description;
	private String currency;
	private String picPath;
	private String kpmg_hours;
	private String kpmg_claimed;
	private String vehicle;
	
	private float spend;
	private float volume;
	
	private long mileage;
	
	private boolean isVat;
	private boolean isBusiness;
	private boolean selected;       // Indicates if expense is selected on the list
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) 
	{
		dest.writeLong(id);
		
		dest.writeString(type);
		dest.writeString(date);
		dest.writeString(description);
		dest.writeString(currency);
		dest.writeString(picPath);
		dest.writeString(kpmg_hours);
		dest.writeString(kpmg_claimed);
		dest.writeString(vehicle);
		
		dest.writeFloat(spend);
		dest.writeFloat(volume);
		
		dest.writeLong(mileage);
		
		dest.writeByte((byte) (isVat ? 0x01 : 0x00));
		dest.writeByte((byte) (isBusiness ? 0x01 : 0x00));
	}

	public static final Parcelable.Creator<DCExpense> CREATOR = new Parcelable.Creator<DCExpense>() 
	{
		public DCExpense createFromParcel(Parcel in) 
		{
			DCExpense expense = new DCExpense();
			expense.id = in.readLong();
			
			expense.type = in.readString();
			expense.date = in.readString();
			expense.description = in.readString();
			expense.currency = in.readString();
			expense.picPath = in.readString();
			expense.kpmg_hours = in.readString();
			expense.kpmg_claimed = in.readString();
			expense.vehicle = in.readString();
			
			expense.spend = in.readFloat();
			expense.volume = in.readFloat();
			
			expense.mileage = in.readLong();
			
			expense.isVat = in.readByte() != 0x00;
			expense.isBusiness = in.readByte() != 0x00;
			
			return expense;
		}

		public DCExpense[] newArray(int size) {
			return new DCExpense[size];
		}
	};
	
	public float getVolume() {
		return volume;
	}

	public void setVolume(float volume) {
		this.volume = volume;
	}

	public String getVehicle() {
		return vehicle;
	}

	public void setVehicle(String vehicle) {
		this.vehicle = vehicle;
	}

	public long getMileage() {
		return mileage;
	}

	public void setMileage(long mileage) {
		this.mileage = mileage;
	}

	public String getKpmg_hours() {
		return kpmg_hours;
	}

	public void setKpmg_hours(String kpmg_hours) {
		this.kpmg_hours = kpmg_hours;
	}

	public String getKpmg_claimed() {
		return kpmg_claimed;
	}

	public void setKpmg_claimed(String kpmg_claimed) {
		this.kpmg_claimed = kpmg_claimed;
	}

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDateString() {
		return date;
	}

	public Date getDate() 
	{
		if (this.date != null) 
		{
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			try {
				Date date = format.parse(this.date);
				return date;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public float getSpend() {
		return spend;
	}

	public void setSpend(float l) {
		this.spend = l;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public boolean isVat() {
		return isVat;
	}

	public void setVat(boolean isVat) {
		this.isVat = isVat;
	}

	public boolean isBusiness() {
		return isBusiness;
	}

	public void setBusiness(boolean isBusiness) {
		this.isBusiness = isBusiness;
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
}

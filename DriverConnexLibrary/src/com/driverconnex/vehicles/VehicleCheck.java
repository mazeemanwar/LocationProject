package com.driverconnex.vehicles;

import java.util.ArrayList;

/**
 * Class for VehicleCheck. Vehicle checks are in the vehicle_checks.xml file. 
 * 
 * name - name of the vehicle check e.g "Monthly"
 * Description - description of the check
 * months - number of months e.g. "1" for "Monthly" check
 * items - items of the check e.g. "Lights", each item has name and text. 
 * 
 * @author Adrian Klimczak
 *
 */

public class VehicleCheck 
{
	private String name;
	private String description;
	private int months;
	
	private ArrayList<String[]> items = new ArrayList<String[]>();
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String[]> getItems() {
		return items;
	}

	public void setItems(ArrayList<String[]> items) {
		this.items = items;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getMonths() {
		return months;
	}

	public void setMonths(int months) {
		this.months = months;
	}
	
	
}

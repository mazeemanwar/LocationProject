package com.driverconnex.data;

import java.util.ArrayList;

public class ServiceListItems {
	private String name;
	private boolean enabled;
	private ArrayList<ServiceListItem> subitems;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name.toUpperCase();
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public ArrayList<ServiceListItem> getSubitems() {
		return subitems;
	}

	public void setSubitems(ArrayList<ServiceListItem> subitems) {
		this.subitems = subitems;
	}
}
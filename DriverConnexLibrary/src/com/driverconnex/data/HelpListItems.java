package com.driverconnex.data;

import java.util.ArrayList;

public class HelpListItems {

	private String name;
	private boolean enabled;
	private ArrayList<HelpListItem> subitems;

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

	public ArrayList<HelpListItem> getSubitems() {
		return subitems;
	}

	public void setSubitems(ArrayList<HelpListItem> subitems) {
		this.subitems = subitems;
	}
}

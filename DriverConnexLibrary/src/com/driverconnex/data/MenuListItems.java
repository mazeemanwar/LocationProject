package com.driverconnex.data;

import java.util.ArrayList;

/**
 * Menu section of the module for the NavigationDrawer.
 * @author Yin Lee (SGI)
 *
 */

public class MenuListItems 
{
	private String name;
	private boolean enabled;
	private ArrayList<MenuListItem> subitems;

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

	public ArrayList<MenuListItem> getSubitems() {
		return subitems;
	}

	public void setSubitems(ArrayList<MenuListItem> subitems) {
		this.subitems = subitems;
	}
}

package com.driverconnex.data;

/**
 * Tab object for tab bar
 * 
 * Tab consists of a name and an icon. This is what is displayed in the tab bar. Priority decides about position of the tab on the tab bar
 * @author Adrian Klimczak
 *
 */
public class Tab 
{
	private String name;
	private String icon;
	private int priority;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIcon() {
		return icon.replace(".png", "");
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
}

package com.driverconnex.data;

/**
 * Individual item for the module section in NavigationDrawer
 * 
 * @author Yin Lee (SGI)
 * 
 */

public class MenuListItem {
	private String name;
	private boolean enabled;
	private String icon;
	private String url;
	private String storyboardName;
	private String storyboardId;
	private String email;
	private boolean isHeader;
	private String className;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public boolean isHeader() {
		return isHeader;
	}

	public void setHeader(boolean isHeader) {
		this.isHeader = isHeader;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getIcon() {
		return icon.replace(".png", "");
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getUrl() {
		return url;
	} 

	public void setUrl(String url) {
		this.url = url;
	}

	public String getStoryboardName() {
		return storyboardName;
	}

	public void setStoryboardName(String storyboardName) {
		this.storyboardName = storyboardName;
	}

	public String getStoryboardId() {
		return storyboardId;
	}

	public void setStoryboardId(String storyboardId) {
		this.storyboardId = storyboardId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}

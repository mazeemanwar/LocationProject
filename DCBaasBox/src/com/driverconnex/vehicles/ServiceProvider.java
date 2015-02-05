package com.driverconnex.vehicles;

import java.io.Serializable;


public class ServiceProvider implements Serializable
{
	private static final long serialVersionUID = 886148491570573881L;
	private String name;
	private String URL;
	
	public ServiceProvider(String name, String URL)
	{
		this.name = name;
		this.URL = URL;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}
}

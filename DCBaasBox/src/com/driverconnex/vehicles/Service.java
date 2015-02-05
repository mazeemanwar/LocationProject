package com.driverconnex.vehicles;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Service for the vehicle
 * @author Adrian Klimczak
 *
 */

public class Service implements Serializable
{
	private static final long serialVersionUID = -6017470385450600480L;
	
	private String name;                    // Name or type of service i.e. MOT, Tyres
	
	// List of providers of this type of service
	private ArrayList<ServiceProvider> providers = new ArrayList<ServiceProvider>();

	public Service(String name) {
		this.name = name;
	}
	
	/**
	 * Add new provider to this service
	 * @param name
	 * @param URL
	 */
	public void addNewProvider(String name, String URL) {
		providers.add(new ServiceProvider(name, URL));
	}
	
	// Getters and Setters
	//============================
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<ServiceProvider> getProviders() {
		return providers;
	}

	public void setProviders(ArrayList<ServiceProvider> providers) {
		this.providers = providers;
	}
}

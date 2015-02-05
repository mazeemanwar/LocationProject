package com.driverconnex.utilities;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baasbox.android.json.JsonObject;
import com.driverconnex.community.DCUser;
import com.driverconnex.incidents.DCIncident;
import com.driverconnex.vehicles.DCServiceHistory;
import com.driverconnex.vehicles.DCServiceItem;
import com.driverconnex.vehicles.DCVehicle;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

/**
 * Utility functions to deal with Parse
 * 
 * @author Adrian Klimczak
 * 
 */

public class ParseUtilities {
	/**
	 * Takes a vehicle in form of Parse Object and returns number of alerts it
	 * has
	 * 
	 * @param vehicle
	 * @return
	 */
	public static int getNumberOfAlerts(ParseObject vehicle) {
		int alerts = 0;

		if (vehicle.getLong("vehicleOdometer") == 0)
			alerts++;
		if (vehicle.getDate("vehicleMOTDate") == null)
			alerts++;
		if (vehicle.getDate("vehicleTaxDate") == null)
			alerts++;
		if (vehicle.getParseObject("vehicleCover") == null)
			alerts++;

		return alerts;
	}

	/*
	 * @param vehicle
	 * 
	 * @return
	 */
	public static int getNumberOfAlertsBaasBox(JsonObject vehicle) {
		int alerts = 0;

		if (vehicle.getLong("vehicleOdometer") == 0)
			alerts++;
		if (vehicle.getString("vehicleMOTDate") == null)
			alerts++;
		if (vehicle.getString("vehicleTaxDate") == null)
			alerts++;
		if (vehicle.getString("vehicleCover") == null)
			alerts++;

		return alerts;
	}

	/**
	 * Converts ParseUser into DCUser.
	 * 
	 * @param userParse
	 * @return
	 */
	public static DCUser convertUser(ParseUser userParse) {
		if (userParse != null) {
			DCUser user = new DCUser();

			user.setId(userParse.getObjectId());
			user.setFirstName(userParse.getString("userFirstName"));
			user.setLastName(userParse.getString("userSurname"));
			user.setStatus(userParse.getString("userActivity"));
			user.setTracking(userParse.getBoolean("userIsTracking"));
			user.setUpdateDate(userParse.getDate("userLastLocationDate"));
			ParseGeoPoint geoPoint = userParse
					.getParseGeoPoint("userCurrentLocation");

			if (geoPoint != null)
				user.setCurrentLocation(geoPoint.getLatitude(),
						geoPoint.getLongitude());

			return user;
		}

		return null;
	}

	/**
	 * Converts Baasbox object of the DC Vehicle into DCVehicle of the class.
	 * 
	 * @param vehicleParse
	 * @return
	 */

	public static DCVehicle convertVehicleObject(JsonObject vehicleParse) {
		if (vehicleParse != null) {
			final DCVehicle vehicle = new DCVehicle();

			// Get the info from database
			System.out.println(vehicleParse.getString("id"));
			vehicle.setId(vehicleParse.getString("id"));

			if (vehicleParse.getString("vehicleMake") != null)
				vehicle.setMake(vehicleParse.getString("vehicleMake"));

			if (vehicleParse.getString("vehicleModel") != null)
				vehicle.setModel(vehicleParse.getString("vehicleModel"));
			System.out.println(vehicleParse.getString("vehicleDerivative"));
			if (vehicleParse.getString("vehicleDerivative") != null)
				vehicle.setDerivative(vehicleParse
						.getString("vehicleDerivative"));
			//
			if (vehicleParse.getInt("vehicleOdometer") != null)
				vehicle.setCurrentMileage(Long.parseLong(vehicleParse.getInt(
						"vehicleOdometer").toString()));
			//
			// if (vehicleParse.getString("taxBand") != null)
			// vehicle.setTaxBand(vehicleParse.getString("taxBand"));
			//
			// if (vehicleParse.getNumber("engineSize") != null)
			// vehicle.setEngineSize(Float.parseFloat(vehicleParse.getNumber(
			// "engineSize").toString()));
			//
			if (vehicleParse.getString("vehicleTransmission") != null)
				vehicle.setTransmission(vehicleParse
						.getString("vehicleTransmission"));
			//
			if (vehicleParse.getString("vehicleFuel") != null)
				vehicle.setFuel(vehicleParse.getString("vehicleFuel"));

			// if (vehicleParse.getNumber("vehicleKnownMPG") != null)
			// vehicle.setKnownMPG(Integer.parseInt(vehicleParse.getNumber(
			// "vehicleKnownMPG").toString()));
			//
			if (vehicleParse.getString("vehicleQuotedMPG") != null) {
				String b = vehicleParse.getString("vehicleQuotedMPG")
						.toString();
				Float vehicleQuotedMPGValue = Float.parseFloat(b);
				//
				vehicle.setQuotedMPG(vehicleQuotedMPGValue);
			}
			if (vehicleParse.getString("vehicleMOTDate") != null) {

				Date d = Utilities.convertDate(vehicleParse
						.getString("vehicleMOTDate"));
				vehicle.setDateMOT(d);
			}
			if (vehicleParse.getString("vehicleServiceDate") != null) {

				vehicle.setService(Utilities.convertDate(vehicleParse
						.getString("vehicleServiceDate")));
			}
			if (vehicleParse.getString("vehicleTaxDate") != null) {

				vehicle.setRoadtax(Utilities.convertDate(vehicleParse
						.getString("vehicleTaxDate")));
			}
			//
			// try {
			// if (vehicleParse.containsKey("vehicleCover")) {
			// ParseObject cover = vehicleParse.getParseObject(
			// "vehicleCover").fetchIfNeeded();
			// vehicle.setInsurance(cover.getDate("coverExpiryDate"));
			// }
			//
			// } catch (ParseException e2) {
			// e2.printStackTrace();
			// }
			//
			vehicle.setRegistration(vehicleParse
					.getString("vehicleRegistration"));
			//
			if (vehicleParse.getString("vehicleEmissions") != null)
				vehicle.setCo2(Integer.parseInt(vehicleParse.getString(
						"vehicleEmissions").toString()));

			vehicle.setAlertsCount(ParseUtilities
					.getNumberOfAlertsBaasBox(vehicleParse));
			//
			// // Get default vehicle of the user
			// ParseObject currentVehicle = ParseUser.getCurrentUser()
			// .getParseObject("userDefaultVehicle");
			//
			// // Check if user has any default vehicle
			// if (currentVehicle != null) {
			// // Check if this is a default vehicle
			// if (vehicleParse.getObjectId().equals(
			// currentVehicle.getObjectId()))
			vehicle.setCurrent(true);
			// }
			//
			// // Get number of DCServiceHistory items
			// ParseRelation<ParseObject> relation = vehicleParse
			// .getRelation("vehicleServiceHistory");
			// ParseQuery<ParseObject> query = relation.getQuery();
			// int serviceHistoryItems = 0;
			//
			// try {
			// serviceHistoryItems = query.count();
			// } catch (ParseException e1) {
			// e1.printStackTrace();
			// }
			//
			// vehicle.setServiceHistory(serviceHistoryItems);
			return vehicle;
		}

		return null;
	}

	/**
	 * Converts ParseObject of the DC Vehicle into DCVehicle of the class.
	 * 
	 * @param vehicleParse
	 * @return
	 */
	public static DCVehicle convertVehicle(ParseObject vehicleParse) {
		if (vehicleParse != null) {
			final DCVehicle vehicle = new DCVehicle();

			// Get the info from database
			vehicle.setId(vehicleParse.getObjectId());

			if (vehicleParse.getString("vehicleMake") != null)
				vehicle.setMake(vehicleParse.getString("vehicleMake"));

			if (vehicleParse.getString("vehicleModel") != null)
				vehicle.setModel(vehicleParse.getString("vehicleModel"));

			if (vehicleParse.getString("vehicleDerivative") != null)
				vehicle.setDerivative(vehicleParse
						.getString("vehicleDerivative"));

			if (vehicleParse.getNumber("vehicleOdometer") != null)
				vehicle.setCurrentMileage(Long.parseLong(vehicleParse
						.getNumber("vehicleOdometer").toString()));

			if (vehicleParse.getString("taxBand") != null)
				vehicle.setTaxBand(vehicleParse.getString("taxBand"));

			if (vehicleParse.getNumber("engineSize") != null)
				vehicle.setEngineSize(Float.parseFloat(vehicleParse.getNumber(
						"engineSize").toString()));

			if (vehicleParse.getString("vehicleTransmission") != null)
				vehicle.setTransmission(vehicleParse
						.getString("vehicleTransmission"));

			if (vehicleParse.getString("vehicleFuel") != null)
				vehicle.setFuel(vehicleParse.getString("vehicleFuel"));

			if (vehicleParse.getNumber("vehicleKnownMPG") != null)
				vehicle.setKnownMPG(Integer.parseInt(vehicleParse.getNumber(
						"vehicleKnownMPG").toString()));

			if (vehicleParse.getNumber("vehicleQuotedMPG") != null) {
				String b = vehicleParse.getNumber("vehicleQuotedMPG")
						.toString();
				Float vehicleQuotedMPGValue = Float.parseFloat(b);

				vehicle.setQuotedMPG(vehicleQuotedMPGValue);
			}
			vehicle.setDateMOT(vehicleParse.getDate("vehicleMOTDate"));
			vehicle.setService(vehicleParse.getDate("vehicleServiceDate"));
			vehicle.setRoadtax(vehicleParse.getDate("vehicleTaxDate"));

			try {
				if (vehicleParse.containsKey("vehicleCover")) {
					ParseObject cover = vehicleParse.getParseObject(
							"vehicleCover").fetchIfNeeded();
					vehicle.setInsurance(cover.getDate("coverExpiryDate"));
				}

			} catch (ParseException e2) {
				e2.printStackTrace();
			}

			vehicle.setRegistration(vehicleParse
					.getString("vehicleRegistration"));

			if (vehicleParse.getNumber("vehicleEmissions") != null)
				vehicle.setCo2(Integer.parseInt(vehicleParse.getNumber(
						"vehicleEmissions").toString()));

			vehicle.setAlertsCount(ParseUtilities
					.getNumberOfAlerts(vehicleParse));

			// Get default vehicle of the user
			ParseObject currentVehicle = ParseUser.getCurrentUser()
					.getParseObject("userDefaultVehicle");

			// Check if user has any default vehicle
			if (currentVehicle != null) {
				// Check if this is a default vehicle
				if (vehicleParse.getObjectId().equals(
						currentVehicle.getObjectId()))
					vehicle.setCurrent(true);
			}

			// Get number of DCServiceHistory items
			ParseRelation<ParseObject> relation = vehicleParse
					.getRelation("vehicleServiceHistory");
			ParseQuery<ParseObject> query = relation.getQuery();
			int serviceHistoryItems = 0;

			try {
				serviceHistoryItems = query.count();
			} catch (ParseException e1) {
				e1.printStackTrace();
			}

			vehicle.setServiceHistory(serviceHistoryItems);
			return vehicle;
		}

		return null;
	}

	/**
	 * Converts ParseObject of the DC Incident into DCIncident of the class.
	 * 
	 * @param vehicleParse
	 * @return
	 */
	public static DCIncident convertIncident(ParseObject parseIncident) {
		if (parseIncident != null) {
			DCIncident incident = new DCIncident();
			incident.setId(parseIncident.getObjectId());
			incident.setDate(parseIncident.getDate("incidentDate"));

			ParseGeoPoint geoPoint = parseIncident
					.getParseGeoPoint("incidentLocation");

			if (geoPoint != null) {
				incident.setLatitude(geoPoint.getLatitude());
				incident.setLongitude(geoPoint.getLongitude());
			}

			incident.setDescription(parseIncident
					.getString("incidentDescription"));

			// Check if video is attached
			ParseFile video = parseIncident.getParseFile("incidentVideo");

			if (video != null)
				incident.setVideoAttached(true);
			else
				incident.setVideoAttached(false);

			List<Map<String, String>> witnesses = parseIncident
					.getList("incidentWitnesses");
			incident.setWitnesses(witnesses);

			return incident;
		}

		return null;
	}

	/**
	 * Converts DCServiceHistory from the Parse to DCServiceHistory of the class
	 * 
	 * @param parseService
	 * @return
	 */
	public static DCServiceHistory convertServiceHistory(
			ParseObject parseService) {
		if (parseService != null) {
			DCServiceHistory serviceHistory = new DCServiceHistory();

			serviceHistory.setId(parseService.getObjectId());
			serviceHistory.setType(parseService.getString("serviceType"));
			serviceHistory.setDate(parseService.getDate("serviceDate"));
			serviceHistory.setCost((Integer) parseService
					.getNumber("serviceCost"));
			serviceHistory.setMileage(parseService.getLong("serviceMileage"));

			return serviceHistory;
		}

		return null;
	}

	/**
	 * Converts DCServiceItem from the Parse to DCServiceItem of the class.
	 * 
	 * @param parseItem
	 * @return
	 */
	public static DCServiceItem convertServiceItem(ParseObject parseItem) {
		if (parseItem != null) {
			DCServiceItem item = new DCServiceItem();

			item.setId(parseItem.getObjectId());
			item.setName(parseItem.getString("itemName"));
			item.setDescription(parseItem.getString("itemDescription"));

			return item;
		}

		return null;
	}
}

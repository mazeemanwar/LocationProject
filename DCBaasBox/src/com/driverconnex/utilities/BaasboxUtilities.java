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
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class BaasboxUtilities {

	/**
	 * Takes a vehicle in form of Parse Object and returns number of alerts it
	 * has
	 * 
	 * @param vehicle
	 * @return
	 */
	public static int getNumberOfAlerts(JsonObject vehicle) {
		int alerts = 0;

		if (vehicle.getLong("vehicleOdometer") == 0)
			alerts++;
		if ((vehicle.getString("vehicleMOTDate") == null)
				|| (vehicle.getString("vehicleMOTDate").equals("")))
			alerts++;
		if ((vehicle.getString("vehicleTaxDate") == null)
				|| (vehicle.getString("vehicleTaxDate").equals("")))
			alerts++;
		if ((vehicle.getString("vehicleCover") == null)
				|| (vehicle.getString("vehicleTaxDate").equals("")))
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
	 * Converts ParseObject of the DC Vehicle into DCVehicle of the class.
	 * 
	 * @param vehicleParse
	 * @return
	 */
	public static DCVehicle convertVehicle(JsonObject vehicleParse) {
		if (vehicleParse != null) {
			final DCVehicle vehicle = new DCVehicle();

			// Get the info from database
			vehicle.setId(vehicleParse.getString("id"));

			String idd = vehicleParse.getString("id");
			if (vehicleParse.getString("vehicleMake") != null)
				vehicle.setMake(vehicleParse.getString("vehicleMake"));

			if (vehicleParse.getString("vehicleModel") != null)
				vehicle.setModel(vehicleParse.getString("vehicleModel"));

			if (vehicleParse.getString("vehicleDerivative") != null)
				vehicle.setDerivative(vehicleParse
						.getString("vehicleDerivative"));

			if (vehicleParse.getInt("vehicleOdometer") != null)
				vehicle.setCurrentMileage(Long.parseLong(vehicleParse.getInt(
						"vehicleOdometer").toString()));

			if (vehicleParse.getString("taxBand") != null)
				vehicle.setTaxBand(vehicleParse.getString("taxBand"));

			if (vehicleParse.getInt("engineSize") != null)
				vehicle.setEngineSize(Float.parseFloat(vehicleParse.getInt(
						"engineSize").toString()));

			if (vehicleParse.getString("vehicleTransmission") != null)
				vehicle.setTransmission(vehicleParse
						.getString("vehicleTransmission"));

			if (vehicleParse.getString("vehicleFuel") != null)
				vehicle.setFuel(vehicleParse.getString("vehicleFuel"));

			if (vehicleParse.getInt("vehicleKnownMPG") != null)
				vehicle.setKnownMPG(Integer.parseInt(vehicleParse.getInt(
						"vehicleKnownMPG").toString()));

			if (vehicleParse.getString("vehicleQuotedMPG") != null) {
				// String b =
				// vehicleParse.getInt("vehicleQuotedMPG").toString();
				// Float vehicleQuotedMPGValue = Float.parseFloat(b);

				vehicle.setComMPG(vehicleParse.getString("vehicleQuotedMPG"));
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
			if (vehicleParse.getString("vehicleInsuranceDate") != null) {

				Date d = Utilities.convertDate(vehicleParse
						.getString("vehicleInsuranceDate"));
				vehicle.setInsurance(d);
			}

			// vehicle.setDateMOT(vehicleParse.getString("vehicleMotDate"));
			// vehicle.setDateMOT(vehicleParse.getDate("vehicleMotDate"));
			// vehicle.setService(vehicleParse.getDate("vehicleServiceDate"));
			// vehicle.setRoadtax(vehicleParse.getDate("vehicleTaxDate"));

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

			vehicle.setRegistration(vehicleParse
					.getString("vehicleRegistration"));

			if (vehicleParse.getString("vehicleEmissions") != null)
				vehicle.setCo2Value(vehicleParse.getString("vehicleEmissions")
						.toString());

			vehicle.setAlertsCount(BaasboxUtilities
					.getNumberOfAlerts(vehicleParse));
			System.out.println(vehicle);

			// Get default vehicle of the user
			// ParseObject currentVehicle = ParseUser.getCurrentUser()
			// .getParseObject("userDefaultVehicle");

			// Check if user has any default vehicle
			// if (currentVehicle != null) {
			// Check if this is a default vehicle
			// if (vehicleParse.getObjectId().equals(
			// currentVehicle.getObjectId()))
			// vehicle.setCurrent(true);
			// }

			// Get number of DCServiceHistory items
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

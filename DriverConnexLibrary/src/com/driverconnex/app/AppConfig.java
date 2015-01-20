package com.driverconnex.app;

/**
 * Configuration class for the app. To add a new dashboard you have to update
 * setDashboard() function, put a new item in enum Dashboard and in HomeActivity
 * you need to handle inflating the Fragment for the dashboard.
 * 
 * @author Adrian Klimczak
 * @author Muhammad Azeem Anwar
 * 
 */

public class AppConfig {
	public final static int PARKING_NOTIFICATION_ID = 1;
	public final static String VEHICLE_LOOKUP_SERVICE = "https://iris.cdlis.co.uk/iris/elvis?vehicle_type=PC&userid=DRIVER_CONNEX&test_flag=N&client_type=external&search_type=vrm&function_name=xml_driver_connex_fnc&search_string=AJ54ZBE&password=GMDQJMVT3VzQZGFEcWk14x7egTfeHrRFuhzIsLYZoMF9NJx5tz";
	private static String ISORGANIZATIONREQUIRED = "";
	private static String ISVERIFICATIONREQUIRED = "";
	private static String ISONLINEMODULEREQUIRED = "";
	private static Boolean ISFINANCEDISABLE;
	private static Boolean ISSHARINGDISABLED;

	private static String HOMEPAGENAME;
	private static String HOMEPAGEURL;
	private static String HOMEPAGEICON;
	private static Boolean ISHOMEPAGEENABLE;
	// By default it's from DriverConnex database
	// this key id DRIVERCONNEXWHILE APP key you can change it with your app key
	private static String APPID = "4dW6hKU33I7RfOj6bLFwFvGHkcZWooobuFfp04uT";
	private static String CLIENTKEY = "C6IqumUa8gijUPQTfOUerNkNNsOcj82UEwyss6Q6";

	// private static String APPID = "S1vCvpPYI2XSIfRNuPt2pXWQJtfobDfbsLQaZdlF";
	// private static String CLIENTKEY =
	// "Uwi57GA0A2nIGKdoC8Z5Zdlz6pGRN8dRGCa4ctdt";

	// For LogicalConnect key tesitng key. you have to change it with your own
	// application key

	// private static String APPID = "G5SpKnesV58gPGttGZTz9bgDANblff7qMcW5zdG1";
	// private static String CLIENTKEY =
	// "VEiOsDR16XX4eau0fZzHWo87BadFnRuGSi6pnqcB";

	// Available dashboards
	public enum Dashboard {
		NONE, KPMG, DRIVER_CONNEX, LOGICAL
	};

	// Currently used dashboard
	public static Dashboard dashboard;

	/**
	 * Sets dashboard.
	 * 
	 * @param dashboard
	 */
	public static void setDashboard(String dashboard) {
		System.out.println("APPCONFIG" + dashboard);
		if (dashboard.equals("KPMGConnect"))
			AppConfig.dashboard = AppConfig.Dashboard.KPMG;
		else if (dashboard.equals("LogicalConnect"))
			AppConfig.dashboard = AppConfig.Dashboard.LOGICAL;
		else if (dashboard.equals("DriverConnex"))
			AppConfig.dashboard = AppConfig.Dashboard.DRIVER_CONNEX;
		else
			AppConfig.dashboard = AppConfig.Dashboard.NONE;
	}

	// Getters and Setters
	// ==========================================
	public static String getAppID() {
		return APPID;
	}

	public static void setAppID(String appID) {
		APPID = appID;
	}

	public static String getClientKey() {
		return CLIENTKEY;
	}

	public static void setClientKey(String clientKey) {
		CLIENTKEY = clientKey;
	}

	public static String getIsOrganizationRequried() {
		return ISORGANIZATIONREQUIRED;
	}

	public static void setIsOrganizationRequried(String isOrganizationRequired) {
		ISORGANIZATIONREQUIRED = isOrganizationRequired;
	}

	public static String getIsVerificationRequired() {
		return ISVERIFICATIONREQUIRED;
	}

	public static void setIsVerificationRequired(String isVerificationRequired) {
		System.out.println("isverfication required " + isVerificationRequired);
		ISVERIFICATIONREQUIRED = isVerificationRequired;
	}

	public static String getIsOnlineModuleRequired() {
		return ISONLINEMODULEREQUIRED;
	}

	public static void setIsOnlineModuleRequired(String isOnlineModuleReq) {
		ISONLINEMODULEREQUIRED = isOnlineModuleReq;
	}

	public static Boolean isfinanceDisable() {
		return ISFINANCEDISABLE;
	}

	public static void setisfinanceDisable(Boolean isDisable) {
		ISFINANCEDISABLE = isDisable;
	}

	public static Boolean isSharingDisable() {
		return ISSHARINGDISABLED;
	}

	public static void setIsSharingDisable(Boolean isDisable) {
		ISSHARINGDISABLED = isDisable;
	}

	// set custom home page values here

	public static String getHOMEPAGENAME() {
		return HOMEPAGENAME;
	}

	public static void setHOMEPAGENAME(String hOMEPAGENAME) {
		HOMEPAGENAME = hOMEPAGENAME;
	}

	public static String getHOMEPAGEURL() {
		return HOMEPAGEURL;
	}

	public static void setHOMEPAGEURL(String hOMEPAGEURL) {
		HOMEPAGEURL = hOMEPAGEURL;
	}

	public static String getHOMEPAGEICON() {
		return HOMEPAGEICON;
	}

	public static void setHOMEPAGEICON(String hOMEPAGEICON) {
		HOMEPAGEICON = hOMEPAGEICON;
	}

	public static Boolean getISHOMEPAGEENABLE() {
		return ISHOMEPAGEENABLE;
	}

	public static void setISHOMEPAGEENABLE(Boolean iSHOMEPAGEENABLE) {
		ISHOMEPAGEENABLE = iSHOMEPAGEENABLE;
	}

}

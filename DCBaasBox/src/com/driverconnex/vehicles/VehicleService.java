package com.driverconnex.vehicles;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Xml;

/**
 * This class is for getting details of the vehicle from the registration number by using iris services.
 * @author Yin Lee(SGI)
 *
 */

public class VehicleService 
{
	private final static String URL = "https://iris.cdlis.co.uk/iris/elvis?vehicle_type=PC&userid=DRIVER_CONNEX&test_flag=N&client_type=external&search_type=vrm&function_name=xml_driver_connex_fnc&search_string=VEHICLEREG&password=GMDQJMVT3VzQZGFEcWk14x7egTfeHrRFuhzIsLYZoMF9NJx5tz";
	private Context context;

	public VehicleService(Context context) {
		super();
		this.context = context;
	}

	public DCVehicle lookupVehicleByRegistration(String reg) throws XmlPullParserException, IOException 
	{
		reg = reg.replaceAll(" ", "").toUpperCase();
		DCVehicle vehicle = new DCVehicle();

		String url = URL.replace("VEHICLEREG", reg);
		InputStream is = null;
		
		try {
			is = retrieveStream(url);

			parse(is, vehicle);
			vehicle.setRegistration(reg);
			
			// Makes sure that the InputStream is closed after the app is
			// finished using it.
		} finally {
			if (is != null) {
				is.close();
			}
		}

		return vehicle;
	}

	/*
	private String convertStreamToString(InputStream inputStream) {
		String result = null;
		try {
			// json is UTF-8 by default
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			result = sb.toString();
		} 
		catch (Exception e) 
		{
		}
		
		return result;
	}*/

	private InputStream retrieveStream(String urlString) throws IOException 
	{
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(10000 /* milliseconds */);
		conn.setConnectTimeout(15000 /* milliseconds */);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		// Starts the query
		conn.connect();
		return conn.getInputStream();
	}

	private void parse(InputStream in, DCVehicle vehicle)
			throws XmlPullParserException, IOException 
			{
		try 
		{
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			readFeed(parser, vehicle, "DATASET");
		} 
		finally 
		{
			in.close();
		}
	}

	private void readFeed(XmlPullParser parser, DCVehicle vehicle, String tag)
			throws XmlPullParserException, IOException 
	{

		parser.require(XmlPullParser.START_TAG, null, tag);
		
		while (parser.next() != XmlPullParser.END_TAG) 
		{
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			
			String name = parser.getName();
			
			// Starts by looking for the entry tag
			if (name.equals("VEHICLE")) 
				readVehicleEntry(parser, vehicle, "VEHICLE");
			else if (name.equals("DVLA")) 
				readFeed(parser, vehicle, "DVLA");
			else if (name.equals("MVRIS")) 
				readMVRISEntry(parser, vehicle, "MVRIS");
			else if(name.equals("VED"))
				readVedEntry(parser,vehicle, "VED");
			else 
			{
				skip(parser);
			}
		}
		
		//readInfoDump(parser,vehicle);
	}

	// Parses the contents of an entry. If it encounters a title, summary, or
	// link tag, hands them off
	// to their respective "read" methods for processing. Otherwise, skips the
	// tag.
	private void readVehicleEntry(XmlPullParser parser, DCVehicle vehicle, String subtag)
			throws XmlPullParserException, IOException 
	{
		parser.require(XmlPullParser.START_TAG, null, subtag);
		
		while (parser.next() != XmlPullParser.END_TAG) 
		{
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			
			String name = parser.getName();
			String value = "";
			
			if (name.equals("MAKE")) 
			{
				parser.require(XmlPullParser.START_TAG, null, "MAKE");
				value = readText(parser);
				vehicle.setMake(value);
				parser.require(XmlPullParser.END_TAG, null, "MAKE");
			} 
			else if (name.equals("MODEL")) 
			{
				parser.require(XmlPullParser.START_TAG, null, "MODEL");
				value = readText(parser);
				//vehicle.setModel(value);
				parser.require(XmlPullParser.END_TAG, null, "MODEL");
			} 
			else if(name.equals("CT_MARKER"))
			{
				parser.require(XmlPullParser.START_TAG, null, "CT_MARKER");
				value = readText(parser);
				parser.require(XmlPullParser.END_TAG, null, "CT_MARKER");
			}
			else if (name.equals("REG_DATE")) 
			{
				parser.require(XmlPullParser.START_TAG, null, "REG_DATE");
				value = readText(parser);
				vehicle.setRegdate(value);
				parser.require(XmlPullParser.END_TAG, null, "REG_DATE");
			} 
			else if(name.equals("VIN_ENDING"))
			{
				parser.require(XmlPullParser.START_TAG, null, "VIN_ENDING");
				value = readText(parser);
				parser.require(XmlPullParser.END_TAG, null, "VIN_ENDING");
			}
			else if (name.equals("COLOUR")) 
			{
				parser.require(XmlPullParser.START_TAG, null, "COLOUR");
				value = readText(parser);
				vehicle.setColour(value);
				parser.require(XmlPullParser.END_TAG, null, "COLOUR");
			} 
			else if (name.equals("BODY")) 
			{
				parser.require(XmlPullParser.START_TAG, null, "BODY");
				value = readText(parser);
				vehicle.setBody(value);
				parser.require(XmlPullParser.END_TAG, null, "BODY");
			} 
			else if(name.equals("BODY_CLASS"))
			{
				parser.require(XmlPullParser.START_TAG, null, "BODY_CLASS");
				value = readText(parser);
				parser.require(XmlPullParser.END_TAG, null, "BODY_CLASS");
			}
			else if(name.equals("SEATING_CAPACITY"))
			{
				parser.require(XmlPullParser.START_TAG, null, "SEATING_CAPACITY");
				value = readText(parser);
				parser.require(XmlPullParser.END_TAG, null, "SEATING_CAPACITY");
			}
			else if(name.equals("WHEELPLAN"))
			{
				parser.require(XmlPullParser.START_TAG, null, "WHEELPLAN");
				value = readText(parser);
				parser.require(XmlPullParser.END_TAG, null, "WHEELPLAN");
			}
			else if (name.equals("CO2")) 
			{
				parser.require(XmlPullParser.START_TAG, null, "CO2");
				value = readText(parser);
				vehicle.setCo2(Integer.valueOf(value));
				parser.require(XmlPullParser.END_TAG, null, "CO2");
			} 
			else if(name.equals("CC"))
			{
				parser.require(XmlPullParser.START_TAG, null, "CC");
				value = readText(parser);
				parser.require(XmlPullParser.END_TAG, null, "CC");
			}
			else 
			{
				skip(parser);
			}
			
			vehicle.getInfodump().put(name, value);
		}
	}
	
	private void readVedEntry(XmlPullParser parser, DCVehicle vehicle, String subtag)
			throws XmlPullParserException, IOException 
	{
		parser.require(XmlPullParser.START_TAG, null, subtag);
		
		while (parser.next() != XmlPullParser.END_TAG) 
		{
			if (parser.getEventType() != XmlPullParser.START_TAG) 
				continue;
			
			String name = parser.getName();
			String value = "";
			
			if(name.equals("BAND"))
			{
				parser.require(XmlPullParser.START_TAG, null, "BAND");
				value = readText(parser);
				vehicle.setTaxBand(value);
				parser.require(XmlPullParser.END_TAG, null, "BAND");
			}
			else if(name.equals("RATE_6_MONTH"))
			{
				parser.require(XmlPullParser.START_TAG, null, "RATE_6_MONTH");
				value = readText(parser);
				parser.require(XmlPullParser.END_TAG, null, "RATE_6_MONTH");
			}
			else if(name.equals("RATE_12_MONTH"))
			{
				parser.require(XmlPullParser.START_TAG, null, "RATE_12_MONTH");
				value = readText(parser);
				vehicle.setAnnualTax(Float.parseFloat(value));
				parser.require(XmlPullParser.END_TAG, null, "RATE_12_MONTH");
			}
			else 
				skip(parser);
			
			vehicle.getInfodump().put(name, value);
		}
	}
	
	private void readMVRISEntry(XmlPullParser parser, DCVehicle vehicle, String subtag)
			throws XmlPullParserException, IOException 
	{
		parser.require(XmlPullParser.START_TAG, null, subtag);
		
		while (parser.next() != XmlPullParser.END_TAG) 
		{
			if (parser.getEventType() != XmlPullParser.START_TAG) 
				continue;
			
			String name = parser.getName();
			String value = "";
			
			if(name.equals("ENGINE_SIZE"))
			{
				parser.require(XmlPullParser.START_TAG, null, "ENGINE_SIZE");
				value = readText(parser);
				vehicle.setEngineSize(Float.valueOf(value));
				parser.require(XmlPullParser.END_TAG, null, "ENGINE_SIZE");
			}
			else if(name.equals("CC"))
			{
				parser.require(XmlPullParser.START_TAG, null, "CC");
				value = readText(parser);
				parser.require(XmlPullParser.END_TAG, null, "CC");
			}
			else if(name.equals("BHP_COUNT"))
			{
				parser.require(XmlPullParser.START_TAG, null, "BHP_COUNT");
				value = readText(parser);
				parser.require(XmlPullParser.END_TAG, null, "BHP_COUNT");
			}
			else if (name.equals("MODEL_VARIANT_NAME")) 
			{
				parser.require(XmlPullParser.START_TAG, null,"MODEL_VARIANT_NAME");
				value = readText(parser);
				vehicle.setDerivative(value);
				parser.require(XmlPullParser.END_TAG, null,"MODEL_VARIANT_NAME");
			} 
			else if(name.equals("DOOR_COUNT"))
			{
				parser.require(XmlPullParser.START_TAG, null, "DOOR_COUNT");
				value = readText(parser);
				parser.require(XmlPullParser.END_TAG, null, "DOOR_COUNT");
			}
			else if(name.equals("BODY_DESC"))
			{
				parser.require(XmlPullParser.START_TAG, null, "BODY_DESC");
				value = readText(parser);
				parser.require(XmlPullParser.END_TAG, null, "BODY_DESC");
			}
			else if(name.equals("CAB_TYPE"))
			{
				parser.require(XmlPullParser.START_TAG, null, "CAB_TYPE");
				value = readText(parser);
				parser.require(XmlPullParser.END_TAG, null, "CAB_TYPE");	
			}
			else if (name.equals("GEARBOX_TYPE")) 
			{
				parser.require(XmlPullParser.START_TAG, null, "GEARBOX_TYPE");
				value = readText(parser);
				vehicle.setTransmission(value);
				parser.require(XmlPullParser.END_TAG, null, "GEARBOX_TYPE");
			}
			else if(name.equals("NUMBER_OF_AXLES"))
			{
				parser.require(XmlPullParser.START_TAG, null, "NUMBER_OF_AXLES");
				value = readText(parser);
				parser.require(XmlPullParser.END_TAG, null, "NUMBER_OF_AXLES");
			}
			else if(name.equals("EXTRA_URBAN_MPG"))
			{
				parser.require(XmlPullParser.START_TAG, null, "EXTRA_URBAN_MPG");
				value = readText(parser);
				parser.require(XmlPullParser.END_TAG, null, "EXTRA_URBAN_MPG");
			}
			else if(name.equals("COMBINED_MPG"))
			{
				parser.require(XmlPullParser.START_TAG, null, "COMBINED_MPG");
				value = readText(parser);			
				vehicle.setQuotedMPG((int)Float.parseFloat(value));
				parser.require(XmlPullParser.END_TAG, null, "COMBINED_MPG");
			}
			else if (name.equals("MODEL")) 
			{
				// Skip <MODEL> tag to get into <DESC> tag
				parser.nextTag();
				parser.require(XmlPullParser.START_TAG, null, "DESC");
				value = readText(parser);
				vehicle.setModel(value);
				parser.require(XmlPullParser.END_TAG, null, "DESC");
				// Skip </MODEL> tag
				parser.nextTag();
			} 
			else if (name.equals("FUEL")) 
			{
				parser.nextTag();
				parser.require(XmlPullParser.START_TAG, null, "DESC");
				value = readText(parser);
				vehicle.setFuel(value);
				parser.require(XmlPullParser.END_TAG, null, "DESC");
			} 
			else 
				skip(parser);
			
			vehicle.getInfodump().put(name, value);
		}
	}

	// For the tags title and summary, extracts their text values.
	private String readText(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	//private void readInfoDump(XmlPullParser parser, Vehicle vehicle) throws XmlPullParserException, IOException
	//{
		/*int eventType = parser.getEventType();
		eventType = parser.START_DOCUMENT;
		
		vehicle.getInfodump().clear();
		
		while (eventType != XmlPullParser.END_DOCUMENT) 
		{
			// Check if we found a tag
			if (eventType == XmlPullParser.START_TAG) 
			{
				// Get name of the tag
				String nodeName = parser.getName();
				
				// Read main entry
				//if (nodeName.contentEquals("main")) 
					//readMainEntry(parser, nodeName);
				//if(nodeName.contentEquals("appearance"))
					//readAppearanceEntry(parser, nodeName);
				
				//vehicle.getInfodump().put(nodeName, parser.getAttributeValue(0));
			} 
			
			eventType = parser.next();
		}*/
	//}
	
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException 
	{
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}
}

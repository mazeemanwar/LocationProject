package com.driverconnex.parking;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.driverconnex.data.DatabaseHelper;

/**
 * SQL database class for parking locations.
 * @author Adrian Klimczak
 *
 */

public class ParkingLocationDataSource 
{
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;
	
	// Columns from the table
	private String[] allColumns = { DatabaseHelper.PARKING_ID,
			DatabaseHelper.PARKIN_GDATE,
			DatabaseHelper.PARKING_LATITUDE,
			DatabaseHelper.PARKING_LONGITUDE };
	
	public ParkingLocationDataSource(Context context) {
		dbHelper = new DatabaseHelper(context);
	}
	
	/**
	 * Opens database
	 * @throws SQLException
	 */
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	/**
	 * Closes database
	 */
	public void close() {
		dbHelper.close();
	}
	
	/**
	 * Creates parking location in the database.
	 * @param location
	 */
	public void createParkingLocation(ParkingLocation location) 
	{
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.PARKIN_GDATE, location.getDate());
		values.put(DatabaseHelper.PARKING_LATITUDE, location.getLatitude());
		values.put(DatabaseHelper.PARKING_LONGITUDE, location.getLongitude());
		database.insert(DatabaseHelper.PARKING_TABLE, null, values);
	}
	
	/**
	 * Deletes all parking locations from the database.
	 */
	public void deleteAllParkingLocations()
	{
		database.delete(DatabaseHelper.PARKING_TABLE, null, null);
	}
	
	/**
	 * Deletes given parking location from the database.
	 * @param location
	 */
	public void deleteParkingLocation(ParkingLocation location) 
	{
		long id = location.getId();
		database.delete(DatabaseHelper.PARKING_TABLE, DatabaseHelper.PARKING_ID
				+ " = " + id, null);
	}
	
	/**
	 * Gets the latest saved parking location. 
	 * @return
	 */
	public ParkingLocation getLatestParkingLocation()
	{
		Cursor cursor = database.query(DatabaseHelper.PARKING_TABLE, allColumns,
				null, null, null, null, DatabaseHelper.PARKIN_GDATE);

		if(cursor.moveToLast())
		{
			ParkingLocation location = cursorToParkingLocation(cursor);
			return location;
		}
		
		return null;
	}
	
	/**
	 * Gets all parking locations.
	 * @return
	 */
	public ArrayList<ParkingLocation> getAllParkingLocations() 
	{
		ArrayList<ParkingLocation> parkingLocations = new ArrayList<ParkingLocation>();

		Cursor cursor = database.query(DatabaseHelper.PARKING_TABLE, allColumns,
				null, null, null, null, DatabaseHelper.PARKIN_GDATE);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) 
		{
			ParkingLocation location = cursorToParkingLocation(cursor);
			parkingLocations.add(location);
			cursor.moveToNext();
		}
		
		// make sure to close the cursor
		cursor.close();
		
		return parkingLocations;
	}
	
	private ParkingLocation cursorToParkingLocation(Cursor cursor) 
	{
		ParkingLocation location = new ParkingLocation();
		location.setId(cursor.getLong(0));
		location.setDate(cursor.getString(1));
		location.setLatitude(cursor.getDouble(2));
		location.setLongitude(cursor.getDouble(3));

		return location;
	}
}

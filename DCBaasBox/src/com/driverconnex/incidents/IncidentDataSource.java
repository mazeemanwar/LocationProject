package com.driverconnex.incidents;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.driverconnex.data.DatabaseHelper;

public class IncidentDataSource {
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;

	// Columns from the table
	private String[] allColumns = { DatabaseHelper.INCIDENT_ID,
			DatabaseHelper.INCIDENT_GDATE, DatabaseHelper.INCIDENT_VEHICLE_REG,
			DatabaseHelper.INCIDENT_DESC, DatabaseHelper.INCIDENT_LATITUDE,
			DatabaseHelper.INCIDENT_LONGITUDE, };

	public IncidentDataSource(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	/**
	 * Opens database
	 * 
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
	 * Creates incident location in the database.
	 * 
	 * @param location
	 */
	public void createIncidentReport(IncidentLocation location) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.INCIDENT_GDATE, location.getDate());
		// values.put(DatabaseHelper.INCIDENT_GTIME, location.getDate());
		values.put(DatabaseHelper.INCIDENT_VEHICLE_REG,
				location.getVehicleReg());
		values.put(DatabaseHelper.INCIDENT_DESC, location.getDescriptioin());
		values.put(DatabaseHelper.INCIDENT_LATITUDE, location.getLatitude());
		values.put(DatabaseHelper.INCIDENT_LONGITUDE, location.getLongitude());

		database.insert(DatabaseHelper.INCIDENT_TABLE, null, values);
	}

	/**
	 * Deletes all incident locations from the database.
	 */
	public void deleteAllIncidentReports() {
		database.delete(DatabaseHelper.INCIDENT_TABLE, null, null);
	}

	/**
	 * Deletes given incident location from the database.
	 * 
	 * @param location
	 */
	public void deleteIncidentReport(IncidentLocation location) {
		long id = location.getId();
		database.delete(DatabaseHelper.INCIDENT_TABLE,
				DatabaseHelper.INCIDENT_ID + " = " + id, null);
	}

	/**
	 * Gets the latest saved incident location.
	 * 
	 * @return
	 */
	public IncidentLocation getLatestIncidentLocation() {
		Cursor cursor = database.query(DatabaseHelper.INCIDENT_TABLE,
				allColumns, null, null, null, null,
				DatabaseHelper.INCIDENT_GDATE);

		if (cursor.moveToLast()) {
			IncidentLocation location = cursorToIncidentLocation(cursor);
			return location;
		}

		return null;
	}

	/**
	 * Gets all incident locations.
	 * 
	 * @return
	 */
	public ArrayList<IncidentLocation> getAllIncidentReports() {
		ArrayList<IncidentLocation> incidentLocations = new ArrayList<IncidentLocation>();

		Cursor cursor = database.query(DatabaseHelper.INCIDENT_TABLE,
				allColumns, null, null, null, null,
				DatabaseHelper.INCIDENT_GDATE);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			IncidentLocation location = cursorToIncidentLocation(cursor);
			incidentLocations.add(location);
			cursor.moveToNext();
		}

		// make sure to close the cursor
		cursor.close();

		return incidentLocations;
	}

	private IncidentLocation cursorToIncidentLocation(Cursor cursor) {
		IncidentLocation location = new IncidentLocation();
		location.setId(cursor.getLong(0));
		location.setDate(cursor.getString(1));
		
		location.setVehicleReg(cursor.getString(2));
		location.setDescriptioin(cursor.getString(3));
		

		location.setLatitude(cursor.getDouble(4));
		location.setLongitude(cursor.getDouble(5));

		return location;
	}
}
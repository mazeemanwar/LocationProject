package com.driverconnex.incidents;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.driverconnex.data.DatabaseHelper;
import com.driverconnex.data.PhotoObject;
import com.driverconnex.data.Witness;

public class IncidentDataSource {
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;

	// Columns from the table
	private String[] allColumns = { DatabaseHelper.INCIDENT_ID,
			DatabaseHelper.INCIDENT_GDATE, DatabaseHelper.INCIDENT_VEHICLE_REG,
			DatabaseHelper.INCIDENT_DESC, DatabaseHelper.INCIDENT_LATITUDE,
			DatabaseHelper.INCIDENT_LONGITUDE, DatabaseHelper.INCIDENT_VIDEO, };

	private String[] photoColumns = { DatabaseHelper.PHOTO_ID,
			DatabaseHelper.INCIDENT_PHOTO_INCIDENT_ID,
			DatabaseHelper.PHOTO_BYTE, };

	private String[] witnessColumns = { DatabaseHelper.WITNESS_ID,
			DatabaseHelper.INCIDENT_WITNESS_INCIDENT_ID,
			DatabaseHelper.WITNESS_NAME, DatabaseHelper.WITNESS_PHONE,
			DatabaseHelper.WITNESS_EMAIL, DatabaseHelper.WITNESS_STATEMENT,

	};

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
	public void createIncidentReport(IncidentLocation location,
			ArrayList<PhotoObject> photoList, ArrayList<Witness> witnessList) {
		int n = photoList.size();

		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.INCIDENT_GDATE, location.getDate());
		// values.put(DatabaseHelper.INCIDENT_GTIME, location.getDate());
		values.put(DatabaseHelper.INCIDENT_VEHICLE_REG,
				location.getVehicleReg());
		values.put(DatabaseHelper.INCIDENT_DESC, location.getDescriptioin());
		values.put(DatabaseHelper.INCIDENT_LATITUDE, location.getLatitude());
		values.put(DatabaseHelper.INCIDENT_LONGITUDE, location.getLongitude());
		values.put(DatabaseHelper.INCIDENT_VIDEO, location.getVideoByteData());

		long photoID = database.insert(DatabaseHelper.INCIDENT_TABLE, null,
				values);
		if (photoList != null) {
			for (int i = 0; i < photoList.size(); i++) {
				values = new ContentValues();
				values.put(DatabaseHelper.INCIDENT_PHOTO_INCIDENT_ID, photoID);
				values.put(DatabaseHelper.PHOTO_BYTE, photoList.get(i)
						.getPhotoByte());

				database.insert(DatabaseHelper.PHOTO_TABLE, null, values);
			}
		}

		if (witnessList != null) {
			for (int i = 0; i < witnessList.size(); i++) {
				values = new ContentValues();
				values.put(DatabaseHelper.INCIDENT_WITNESS_INCIDENT_ID, photoID);
				values.put(DatabaseHelper.WITNESS_NAME, witnessList.get(i)
						.getName());
				values.put(DatabaseHelper.WITNESS_PHONE, witnessList.get(i)
						.getPhoneNo());
				values.put(DatabaseHelper.WITNESS_EMAIL, witnessList.get(i)
						.getEmail());
				values.put(DatabaseHelper.WITNESS_STATEMENT, witnessList.get(i)
						.getStatement());

				database.insert(DatabaseHelper.WITNESS_TABLE, null, values);
			}
		}
		System.out.println("ya Allah");

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
		location.setVideoByteData(cursor.getBlob(6));
		return location;
	}

	/**
	 * Gets photo associated with incident of the given id.
	 * 
	 * @param journeyID
	 * @return
	 */
	public ArrayList<PhotoObject> getIncidentPhoto(long incidentID) {
		ArrayList<PhotoObject> photos = new ArrayList<PhotoObject>();

		Cursor cursor = database.query(DatabaseHelper.PHOTO_TABLE,
				photoColumns, DatabaseHelper.INCIDENT_PHOTO_INCIDENT_ID + "=?",
				new String[] { "" + incidentID }, null, null,
				DatabaseHelper.PHOTO_ID);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			PhotoObject photo = cursorToIncidentPhoto(cursor);
			photos.add(photo);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return photos;
	}

	/**
	 * Gets all journey points from SQL database.
	 * 
	 * @return
	 */
	public ArrayList<PhotoObject> getAllIncidentPhoto() {
		ArrayList<PhotoObject> photos = new ArrayList<PhotoObject>();

		Cursor cursor = database.query(DatabaseHelper.PHOTO_TABLE,
				photoColumns, null, null, null, null, DatabaseHelper.PHOTO_ID);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			PhotoObject photo = cursorToIncidentPhoto(cursor);
			photos.add(photo);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return photos;
	}

	private PhotoObject cursorToIncidentPhoto(Cursor cursor) {
		PhotoObject photo = new PhotoObject();

		photo.setId(cursor.getLong(0));
		photo.setIncidentId(cursor.getLong(1));
		photo.setPhotoByte(cursor.getBlob(2));

		return photo;
	}

	/**
	 * Gets WITNESS associated with incident of the given id.
	 * 
	 * @param IncidentID
	 * @return
	 */
	public ArrayList<Witness> getIncidentWitnesess(long incidentID) {
		ArrayList<Witness> witnesess = new ArrayList<Witness>();

		Cursor cursor = database.query(DatabaseHelper.WITNESS_TABLE,
				witnessColumns, DatabaseHelper.INCIDENT_WITNESS_INCIDENT_ID
						+ "=?", new String[] { "" + incidentID }, null, null,
				DatabaseHelper.WITNESS_ID);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Witness witness = cursorToIncidentWitness(cursor);
			witnesess.add(witness);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return witnesess;
	}

	private Witness cursorToIncidentWitness(Cursor cursor) {
		Witness witness = new Witness();

		witness.setId(cursor.getLong(0));
		witness.setIncidentId(cursor.getLong(1));
		witness.setName(cursor.getString(2));
		witness.setPhoneNo(cursor.getString(3));
		witness.setEmail(cursor.getString(4));
		witness.setStatement(cursor.getString(5));

		return witness;
	}
}

package com.driverconnex.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class helps to create a SQL database for the app. This is the place where you add any new tables and columns. 
 * 
 * @author Yin Lee (SGI)
 * @author Adrian Klimczak
 *
 */

public class DatabaseHelper extends SQLiteOpenHelper 
{
	private static int DB_VERSION = 1;
	private static String DATABASE_NAME = "driverConnex.db";

	private final Context context;
	
	// Expenses Module
	//---------------------------------------------------
	public static final String EXPENSE_TABLE = "expenses";
	public static final String EXPENSE_ID = "expense_id";
	public static final String EXPENSE_DESCRIPTION = "expense_description";
	public static final String EXPENSE_TYPE = "expense_type";
	public static final String EXPENSE_SPEND = "expense_spend";
	public static final String EXPENSE_DATE = "expense_date";
	public static final String EXPENSE_CURRENCY = "expense_currency";
	public static final String EXPENSE_IS_VAT = "expense_is_vat";
	public static final String EXPENSEIS_BUSINESS = "expense_is_business";
	public static final String EXPENSE_PICPATH = "expense_pic_path";
	public static final String EXPENSE_VEHICLE = "expense_vehicle";
	public static final String FUEL_VOLUME = "fuel_volume";
	public static final String FUEL_MILEAGE = "fuel_mileage";
	public static final String KPMG_HOURS = "kpmg_hours";
	public static final String KPMG_CLAIMED = "kpmg_claimed";

	// Journey Module
	//---------------------------------------------------
	// DCJourney Table
	public static final String JOURNEY_TABLE = "journey";
	public static final String JOURNEY_ID = "journey_id";
	public static final String JOURNEY_DESC = "journey_desc";
	public static final String JOURNEY_CREATE_DATE = "journey_create_date";
	public static final String JOURNEY_START_TIME = "journey_start_time";
	public static final String JOURNEY_END_TIME = "journey_end_time";
	public static final String JOURNEY_DURATION = "journey_duration";
	public static final String JOURNEY_DISTANCE = "journey_distance";
	public static final String JOURNEY_BUSINESS = "journey_purpose";
	public static final String JOURNEY_EXPENSE = "journey_expense";
	public static final String JOURNEY_START_ADDR = "journey_start_address";
	public static final String JOURNEY_END_ADDR = "journey_end_address";
	public static final String JOURNEY_AVG_SPEED = "journey_avg_speed";
	public static final String JOURNEY_MAX_SPEED = "journey_max_speed";
	public static final String JOURNEY_EMISSION = "journey_emission";
	public static final String JOURNEY_SCORE = "journey_score";
	public static final String JOURNEY_SCORE_ADDED = "journey_score_added";
	public static final String JOURNEY_VALID_BEHAVIOUR = "journey_valid_behaviour";
			
	// DCJourneyPoint table
	public static final String JOURNEY_POINT_TABLE = "journey_point";
	public static final String JOURNEY_POINT_ID = "journey_point_id";
	public static final String JOURNEY_POINT_JOURNEY_ID = "journey_point_journey_id";
	public static final String JOURNEY_POINT_LATITUDE = "journey_point_latitude";
	public static final String JOURNEY_POINT_LONGITUDE = "journey_point_longitude";
	
	// DCBehaviourPoint table
	public static final String BEHAVIOUR_TABLE = "behaviour";
	public static final String BEHAVIOUR_ID = "behaviour_id";
	public static final String BEHAVIOUR_JOURNEY_ID = "behaviour_journey_id";
	public static final String BEHAVIOUR_ACCELERATION_X = "behaviour_acceleration_x";
	public static final String BEHAVIOUR_ACCELERATION_Y = "behaviour_acceleration_y";
	public static final String BEHAVIOUR_ACCELERATION_Z = "behaviour_acceleration_z";
	public static final String BEHAVIOUR_ACTIVITY = "behaviour_activity";
	public static final String BEHAVIOUR_SPEED = "behaviour_speed";
	public static final String BEHAVIOUR_FLAT = "behaviour_flat";
	public static final String BEHAVIOUR_LANDSCAPE = "behaviour_landscape";
	public static final String BEHAVIOUR_PORTRAIT = "behaviour_portrait";
	//---------------------------------------------------
	
	// Parking Module
	//---------------------------------------------------
	public static final String PARKING_TABLE = "parking_locations";
	public static final String PARKING_ID = "parking_location_id";
	public static final String PARKIN_GDATE = "parking_date";
	public static final String PARKING_LATITUDE = "parking_latitude";
	public static final String PARKING_LONGITUDE = "parking_longitude";
	
	public DatabaseHelper(Context context) 
	{
		super(context, DATABASE_NAME, null, DB_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) 
	{	
		// Create Expenses table
		db.execSQL("CREATE TABLE " + EXPENSE_TABLE + " (" + EXPENSE_ID
				+ " INTEGER PRIMARY KEY, " 
				+ EXPENSE_TYPE + " TEXT, "
				+ EXPENSE_DESCRIPTION + " TEXT, "
				+ EXPENSE_SPEND + " DOUBLE, " 
				+ EXPENSE_DATE + " DATETIME, "
				+ EXPENSE_CURRENCY + " TEXT, " 
				+ EXPENSE_IS_VAT + " BOOLEAN, "
				+ EXPENSEIS_BUSINESS + " BOOLEAN, " 
				+ EXPENSE_PICPATH + " TEXT, "
				+ FUEL_VOLUME + " DOUBLE, " 
				+ FUEL_MILEAGE + " INTEGER, "
				+ EXPENSE_VEHICLE + " TEXT, " 
				+ KPMG_HOURS + " TEXT, " 
				+ KPMG_CLAIMED + " TEXT)");

		// Create DCJourney table
		db.execSQL("CREATE TABLE " + JOURNEY_TABLE + " (" 
				+ JOURNEY_ID + " INTEGER PRIMARY KEY, " 
				+ JOURNEY_DESC + " TEXT, "
				+ JOURNEY_START_TIME + " DATETIME, " 
				+ JOURNEY_END_TIME + " DATETIME, " 
				+ JOURNEY_DURATION + " INTEGER, "
				+ JOURNEY_DISTANCE + " DOUBLE, " 
				+ JOURNEY_BUSINESS + " BOOLEAN, "
				+ JOURNEY_EXPENSE + " DOUBLE, " 
				+ JOURNEY_START_ADDR + " TEXT, "
				+ JOURNEY_END_ADDR + " TEXT, " 
				+ JOURNEY_AVG_SPEED + " DOUBLE, "
				+ JOURNEY_MAX_SPEED + " DOUBLE, " 
				+ JOURNEY_CREATE_DATE	+ " DATETIME, " 
				+ JOURNEY_SCORE + " TEXT, "
				+ JOURNEY_SCORE_ADDED + " BOOLEAN, "
				+ JOURNEY_VALID_BEHAVIOUR + " BOOLEAN, "
				+ JOURNEY_EMISSION + " DOUBLE)");
		
		// Create DCJourneyPoint table
		db.execSQL("CREATE TABLE " + JOURNEY_POINT_TABLE + " (" 
				+ JOURNEY_POINT_ID	+ " INTEGER PRIMARY KEY, "
				+ JOURNEY_POINT_JOURNEY_ID + " INTEGER, "
				+ JOURNEY_POINT_LATITUDE + " DOUBLE, "
				+ JOURNEY_POINT_LONGITUDE + " DOUBLE, "
				+ "FOREIGN KEY ("+JOURNEY_POINT_JOURNEY_ID+") REFERENCES " 
				+ JOURNEY_TABLE + " (" + JOURNEY_ID + "));");
		
		// Create DCBehaviour table
		db.execSQL("CREATE TABLE " + BEHAVIOUR_TABLE + " (" 
				+ BEHAVIOUR_ID	+ " INTEGER PRIMARY KEY, "
				+ BEHAVIOUR_JOURNEY_ID + " INTEGER, "
				+ BEHAVIOUR_ACCELERATION_X + " DOUBLE, "
				+ BEHAVIOUR_ACCELERATION_Y + " DOUBLE, "
				+ BEHAVIOUR_ACCELERATION_Z + " DOUBLE, "
				+ BEHAVIOUR_ACTIVITY + " TEXT, "
				+ BEHAVIOUR_SPEED + " DOUBLE, "
				+ BEHAVIOUR_FLAT + " BOOLEAN, "
				+ BEHAVIOUR_LANDSCAPE + " BOOLEAN, "
				+ BEHAVIOUR_PORTRAIT + " BOOLEAN, "
				+ "FOREIGN KEY ("+BEHAVIOUR_JOURNEY_ID+") REFERENCES " 
				+ JOURNEY_TABLE + " (" + JOURNEY_ID + "));");
		
		// Create Parking Locations table
		db.execSQL("CREATE TABLE " + PARKING_TABLE + " (" + PARKING_ID
				+ " INTEGER PRIMARY KEY, "
				+ PARKIN_GDATE + " DATETIME, "
				+ PARKING_LATITUDE + " DOUBLE, "
				+ PARKING_LONGITUDE + " DOUBLE)");
	}

	/**
	 * Updates tables in database
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		db.execSQL("drop table if exists " + EXPENSE_TABLE);
		db.execSQL("drop table if exists " + JOURNEY_TABLE);
		db.execSQL("drop table if exists " + JOURNEY_POINT_TABLE);
		db.execSQL("drop table if exists " + PARKING_TABLE);
		db.execSQL("drop table if exists " + BEHAVIOUR_TABLE);
		onCreate(db);
	}

	public static void setDatabaseName(String username) 
	{
		DATABASE_NAME = "driverConnex_" + username + ".db";
	}
}

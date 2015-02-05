package com.driverconnex.expenses;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.driverconnex.data.DatabaseHelper;
import com.driverconnex.utilities.Utilities;

/**
 * SQL database class for the expenses.
 * @author Yin Lee (SGI)
 * @author Adrian Klimczak
 */

public class ExpensesDataSource 
{
	// Database fields
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;
	private String[] allColumns = { 
			DatabaseHelper.EXPENSE_ID,
			DatabaseHelper.EXPENSE_TYPE, 
			DatabaseHelper.EXPENSE_DESCRIPTION,
			DatabaseHelper.EXPENSE_SPEND,
			DatabaseHelper.EXPENSE_DATE, 
			DatabaseHelper.EXPENSE_CURRENCY,
			DatabaseHelper.EXPENSE_IS_VAT, 
			DatabaseHelper.EXPENSEIS_BUSINESS,
			DatabaseHelper.EXPENSE_PICPATH, 
			DatabaseHelper.FUEL_VOLUME,
			DatabaseHelper.FUEL_MILEAGE, 
			DatabaseHelper.EXPENSE_VEHICLE,
			DatabaseHelper.KPMG_HOURS,
			DatabaseHelper.KPMG_CLAIMED };

	public ExpensesDataSource(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	/**
	 * Creates expense other than fuel
	 * @param type
	 * @param description
	 * @param spend
	 * @param date
	 * @param currency
	 * @param isVat
	 * @param isBusiness
	 * @param picPath
	 */
	public void createOtherExpense(String type, String description, float spend, String date,
			String currency, boolean isVat, boolean isBusiness, String picPath) 
	{
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.EXPENSE_TYPE, type);
		values.put(DatabaseHelper.EXPENSE_DESCRIPTION, description);
		values.put(DatabaseHelper.EXPENSE_SPEND, spend);
		values.put(DatabaseHelper.EXPENSE_DATE, date);
		values.put(DatabaseHelper.EXPENSE_CURRENCY, currency);
		values.put(DatabaseHelper.EXPENSE_IS_VAT, isVat);
		values.put(DatabaseHelper.EXPENSEIS_BUSINESS, isBusiness);
		values.put(DatabaseHelper.EXPENSE_PICPATH, picPath);
		
		database.insert(DatabaseHelper.EXPENSE_TABLE, null, values);
	}

	/**
	 * Simplified version of creating other expense than fuel
	 * @param expense
	 */
	public void createOtherExpense(DCExpense expense) 
	{
		String expenseDate = Utilities.convertDateFormat(expense.getDateString(), "dd-MM-yyyy", "yyyy-MM-dd");
		
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.EXPENSE_TYPE, expense.getType());
		values.put(DatabaseHelper.EXPENSE_DESCRIPTION, expense.getDescription());
		values.put(DatabaseHelper.EXPENSE_SPEND, expense.getSpend());
		values.put(DatabaseHelper.EXPENSE_DATE, expenseDate);
		values.put(DatabaseHelper.EXPENSE_CURRENCY, expense.getCurrency());
		values.put(DatabaseHelper.EXPENSE_IS_VAT, expense.isVat());
		values.put(DatabaseHelper.EXPENSEIS_BUSINESS, expense.isBusiness());
		values.put(DatabaseHelper.EXPENSE_PICPATH, expense.getPicPath());
		database.insert(DatabaseHelper.EXPENSE_TABLE, null, values);
	}
	
	/**
	 * Creates fuel expense
	 * @param expense
	 */
	public void createFuel(DCExpense expense) 
	{	
		String expenseDate = Utilities.convertDateFormat(expense.getDateString(), "dd-MM-yyyy", "yyyy-MM-dd");
		
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.EXPENSE_TYPE, "Fuel");
		values.put(DatabaseHelper.EXPENSE_DESCRIPTION, expense.getDescription());
		values.put(DatabaseHelper.EXPENSE_SPEND, expense.getSpend());
		values.put(DatabaseHelper.EXPENSE_DATE, expenseDate);
		values.put(DatabaseHelper.EXPENSE_CURRENCY, expense.getCurrency());
		values.put(DatabaseHelper.EXPENSE_IS_VAT, expense.isVat());
		values.put(DatabaseHelper.EXPENSEIS_BUSINESS, expense.isBusiness());
		values.put(DatabaseHelper.EXPENSE_PICPATH, expense.getPicPath());
		values.put(DatabaseHelper.FUEL_VOLUME, expense.getVolume());
		values.put(DatabaseHelper.FUEL_MILEAGE, expense.getMileage());
		values.put(DatabaseHelper.EXPENSE_VEHICLE, expense.getVehicle());
		
		database.insert(DatabaseHelper.EXPENSE_TABLE, null, values);
	}

	/**
	 * Creates KPMG expense.
	 * @param spend
	 * @param description
	 * @param date
	 * @param currency
	 * @param picPath
	 * @param hours
	 * @param claimed
	 */
	public void createKPMG(float spend, String description, String date, String currency,
			String picPath, String hours, String claimed) 
	{
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.EXPENSE_TYPE, "Travel and Subsistence");
		values.put(DatabaseHelper.EXPENSE_DESCRIPTION, description);
		values.put(DatabaseHelper.EXPENSE_SPEND, spend);
		values.put(DatabaseHelper.EXPENSE_DATE, date);
		values.put(DatabaseHelper.EXPENSE_CURRENCY, currency);
		values.put(DatabaseHelper.EXPENSE_PICPATH, picPath);
		values.put(DatabaseHelper.KPMG_HOURS, hours);
		values.put(DatabaseHelper.KPMG_CLAIMED, claimed);
		database.insert(DatabaseHelper.EXPENSE_TABLE, null, values);
	}

	/**
	 * Updates existing expense.
	 * @param id
	 * @param expense
	 */
	public void updateExpense(long id, DCExpense expense) 
	{
		ContentValues values = new ContentValues();
		
		if (expense.getType() != null) 
			values.put(DatabaseHelper.EXPENSE_TYPE, expense.getType());
		
		values.put(DatabaseHelper.EXPENSE_DESCRIPTION, expense.getDescription());
		values.put(DatabaseHelper.EXPENSE_SPEND, expense.getSpend());
		values.put(DatabaseHelper.EXPENSE_DATE, expense.getDateString());
		values.put(DatabaseHelper.EXPENSE_CURRENCY, expense.getCurrency());
		values.put(DatabaseHelper.EXPENSE_IS_VAT, expense.isVat());
		values.put(DatabaseHelper.EXPENSEIS_BUSINESS, expense.isBusiness());
		values.put(DatabaseHelper.EXPENSE_PICPATH, expense.getPicPath());
		values.put(DatabaseHelper.FUEL_VOLUME, expense.getVolume());
		values.put(DatabaseHelper.FUEL_MILEAGE, expense.getMileage());
		values.put(DatabaseHelper.KPMG_HOURS, expense.getKpmg_hours());
		values.put(DatabaseHelper.KPMG_CLAIMED, expense.getKpmg_claimed());
		values.put(DatabaseHelper.EXPENSE_VEHICLE, expense.getVehicle());
		
		database.update(DatabaseHelper.EXPENSE_TABLE, values, DatabaseHelper.EXPENSE_ID + "=" + id, null);
	}
	
	/**
	 * Simplified version of updating existing expense.
	 * @param expense
	 */
	public void updateExpense(DCExpense expense) 
	{
		updateExpense(expense.getId(), expense);
	}
	
	/**
	 * Deletes expense
	 * @param expense
	 */
	public void deleteExpense(DCExpense expense) 
	{
		long id = expense.getId();
		database.delete(DatabaseHelper.EXPENSE_TABLE, DatabaseHelper.EXPENSE_ID
				+ " = " + id, null);
	}
	
	/**
	 * Deletes expense
	 * @param id
	 */
	public void deleteExpense(long id) 
	{
		database.delete(DatabaseHelper.EXPENSE_TABLE, DatabaseHelper.EXPENSE_ID
				+ " = " + id, null);
	}
	
	/**
	 * Gets all expenses from database
	 * @return
	 */
	public ArrayList<DCExpense> getAllExpenses() 
	{
		ArrayList<DCExpense> expenses = new ArrayList<DCExpense>();

		Cursor cursor = database.query(DatabaseHelper.EXPENSE_TABLE, allColumns,
				null, null, null, null, DatabaseHelper.EXPENSE_DATE +" DESC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) 
		{
			DCExpense expense = cursorToExpense(cursor);
			expenses.add(expense);
			cursor.moveToNext();
		}
		
		// make sure to close the cursor
		cursor.close();
		return expenses;
	}

	/**
	 * Gets all expenses that are of specified type from database.
	 * @param type
	 * @return
	 */
	public ArrayList<DCExpense> getAllExpenses(String type) 
	{
		ArrayList<DCExpense> expenses = new ArrayList<DCExpense>();

		Cursor cursor = database.query(DatabaseHelper.EXPENSE_TABLE, allColumns,
				DatabaseHelper.EXPENSE_TYPE + "=?", new String[] { type }, null, null, DatabaseHelper.EXPENSE_DATE +" DESC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) 
		{
			DCExpense expense = cursorToExpense(cursor);
			expenses.add(expense);
			cursor.moveToNext();
		}
		
		// make sure to close the cursor
		cursor.close();
		return expenses;
	}
	
	/**
	 * Cursors to expense.
	 * @param cursor
	 * @return
	 */
	private DCExpense cursorToExpense(Cursor cursor) 
	{
		// Create expense
		DCExpense expense = new DCExpense();
		expense.setId(cursor.getLong(0));
		expense.setType(cursor.getString(1));
		expense.setDescription(cursor.getString(2));
		expense.setSpend(cursor.getFloat(3));
		
		String expenseDate = Utilities.convertDateFormat(cursor.getString(4),"yyyy-MM-dd", "dd-MM-yyyy");
		
		expense.setDate(expenseDate);
		expense.setCurrency(cursor.getString(5));
		if (cursor.getInt(6) == 1) 
			expense.setVat(true);
		else
			expense.setVat(false);

		if (cursor.getInt(7) == 1) 
			expense.setBusiness(true);
		else 
			expense.setBusiness(false);
		
		expense.setPicPath(cursor.getString(8));
		expense.setVolume(cursor.getFloat(9));
		expense.setMileage(cursor.getLong(10));
		expense.setVehicle(cursor.getString(11));
		expense.setKpmg_hours(cursor.getString(12));
		expense.setKpmg_claimed(cursor.getString(13));
		
		return expense;
	}
}

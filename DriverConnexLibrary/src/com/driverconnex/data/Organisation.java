package com.driverconnex.data;

import java.util.ArrayList;

public class Organisation {
	String currency;
	int PPM;
	private ArrayList<ArrayList<String>> breakDown;
	private ArrayList<ArrayList<String>> accidentNumber;
	private ArrayList<String> expenseType;


	public ArrayList<String> getExpenseType() {
		return expenseType;
	}

	public void setExpenseType(ArrayList<String> expenseType) {
		this.expenseType = expenseType;
	}

	public ArrayList<ArrayList<String>> getAccidentNumber() {
		return accidentNumber;
	}

	public void setAccidentNumber(ArrayList<ArrayList<String>> accidentNumber) {
		this.accidentNumber = accidentNumber;
	}

	public ArrayList<ArrayList<String>> getBreakDown() {
		return breakDown;
	}

	public void setBreakDown(ArrayList<ArrayList<String>> breakDown) {
		this.breakDown = breakDown;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public int getPPM() {
		return PPM;
	}

	public void setPPM(int PPM) {
		this.PPM = PPM;
	}

}

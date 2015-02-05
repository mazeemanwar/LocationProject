package com.driverconnex.expenses;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.driverconnex.adapter.ExpenseListAdapter;
import com.driverconnex.app.R;
import com.driverconnex.utilities.ThemeUtilities;
import com.driverconnex.utilities.Utilities;
import com.parse.ParseUser;

/**
 * Activity for displaying a list of all expenses.
 * 
 * @author Yin Lee (SGI)
 * @author Adrian Klimczak
 * 
 */

public class ReviewExpensesActivity extends Activity {
	private ArrayList<DCExpense> data = new ArrayList<DCExpense>(); // Stores
																	// all
																	// expenses
	private ArrayList<DCExpense> adapterData = new ArrayList<DCExpense>(); // Stores
																			// only
																			// expenses
																			// used
																			// for
																			// the
																			// adapter
	private TreeSet<Integer> separatorsSet = new TreeSet<Integer>();

	private ListView list;
	private LinearLayout otherExpensesBtn;
	private LinearLayout fuelExpensesBtn;
	private ImageView otherExpensesIcon;
	private ImageView fuelExpensesIcon;
	private TextView otherExpensesText;
	private TextView fuelExpensesText;

	private ExpenseListAdapter adapter;

	private boolean displayFuel = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expense_review);

		list = (ListView) findViewById(R.id.list);
		otherExpensesBtn = (LinearLayout) findViewById(R.id.otherExpensesBtn);
		fuelExpensesBtn = (LinearLayout) findViewById(R.id.fuelExpensesBtn);
		otherExpensesIcon = (ImageView) findViewById(R.id.otherExpensesIcon);
		fuelExpensesIcon = (ImageView) findViewById(R.id.fuelExpensesIcon);
		otherExpensesText = (TextView) findViewById(R.id.otherExpensesText);
		fuelExpensesText = (TextView) findViewById(R.id.fuelExpensesText);

		otherExpensesBtn.setOnClickListener(onClickListener);
		fuelExpensesBtn.setOnClickListener(onClickListener);

		list.setOnItemClickListener(onItemClickListener);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!data.isEmpty())
			data.clear();

		if (!adapterData.isEmpty())
			adapterData.clear();

		if (!separatorsSet.isEmpty())
			separatorsSet.clear();

		ExpensesDataSource dataSource = new ExpensesDataSource(
				ReviewExpensesActivity.this);
		dataSource.open();
		data = dataSource.getAllExpenses();

		dataSource.close();
		String d = "";

		for (int i = 0; i < data.size(); i++) {
			if (!displayFuel) {
				if (!data.get(i).getType().contains("Fuel")) {
					// Put all items to the same date groups
					if (!data.get(i).getDateString().equals(d)) {
						adapterData.add(data.get(i));
						separatorsSet.add(adapterData.size() - 1);
						d = data.get(i).getDateString();
					}

					adapterData.add(data.get(i));
				}
			} else {
				if (data.get(i).getType().contains("Fuel")) {
					// Put all items to the same date groups
					if (!data.get(i).getDateString().equals(d)) {
						adapterData.add(data.get(i));
						separatorsSet.add(adapterData.size() - 1);
						d = data.get(i).getDateString();
					}

					adapterData.add(data.get(i));
				}
			}
		}

		adapter = new ExpenseListAdapter(this, adapterData, separatorsSet);
		list.setAdapter(adapter);
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (!view.isActivated())
				adapterData.get(position).setSelected(true);
			else
				adapterData.get(position).setSelected(false);

			adapter.notifyDataSetChanged();
		}
	};

	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v == otherExpensesBtn) {
				otherExpensesIcon.setImageDrawable((getResources()
						.getDrawable(R.drawable.money_white_56x46)));
				fuelExpensesIcon.setImageDrawable(getResources().getDrawable(
						R.drawable.car_grey_56x50));

				otherExpensesText.setTextColor(getResources().getColor(
						R.color.white));
				fuelExpensesText.setTextColor(getResources().getColor(
						ThemeUtilities.getMainTextColor()));

				if (!adapterData.isEmpty())
					adapterData.clear();

				if (!separatorsSet.isEmpty())
					separatorsSet.clear();

				String d = "";

				// Prepare data for the adapter
				for (int i = 0; i < data.size(); i++) {
					// Display only other expenses
					if (!data.get(i).getType().equals("Fuel")) {
						// Put all items to the same date groups
						if (!data.get(i).getDateString().equals(d)) {
							adapterData.add(data.get(i));
							separatorsSet.add(adapterData.size() - 1);
							d = data.get(i).getDateString();
						}

						adapterData.add(data.get(i));
					}
				}

				adapter = new ExpenseListAdapter(ReviewExpensesActivity.this,
						adapterData, separatorsSet);
				list.setAdapter(adapter);

				displayFuel = false;
			} else if (v == fuelExpensesBtn) {
				otherExpensesIcon.setImageDrawable(getResources().getDrawable(
						R.drawable.money_grey_56x46));
				fuelExpensesIcon.setImageDrawable(getResources().getDrawable(
						R.drawable.car_white_56x50));

				otherExpensesText.setTextColor(getResources().getColor(
						ThemeUtilities.getMainTextColor()));
				fuelExpensesText.setTextColor(getResources().getColor(
						R.color.white));

				if (!adapterData.isEmpty())
					adapterData.clear();

				if (!separatorsSet.isEmpty())
					separatorsSet.clear();

				String d = "";

				// Prepare data for the adapter
				for (int i = 0; i < data.size(); i++) {
					// Display only fuel expenses
					if (data.get(i).getType().equals("Fuel")) {
						// Put all items to the same date groups
						if (!data.get(i).getDateString().equals(d)) {
							adapterData.add(data.get(i));
							separatorsSet.add(adapterData.size() - 1);
							d = data.get(i).getDateString();
						}

						adapterData.add(data.get(i));
					}
				}

				displayFuel = true;

				adapter = new ExpenseListAdapter(ReviewExpensesActivity.this,
						adapterData, separatorsSet);
				list.setAdapter(adapter);
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_export, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			overridePendingTransition(R.anim.null_anim, R.anim.slide_out);
			return true;
		} else if (item.getItemId() == R.id.action_export) {
			exportToCSVFile();
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Exports selected expenses to CSV file and it sends it via email.
	 */
	private void exportToCSVFile() {
		String expenseString = "Type,Date,Fuel Volume,Fuel Mileage,Amount Spent,Currency,VAT,T&S Hours,T&S Claimed\n";

		StringBuilder builder = new StringBuilder();
		builder.append(expenseString);

		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).isSelected()) {
				String objectString = "" + data.get(i).getType() + ","
						+ data.get(i).getDateString() + ","
						+ data.get(i).getVolume() + ","
						+ data.get(i).getMileage() + ","
						+ data.get(i).getSpend() + ","
						+ data.get(i).getCurrency() + "," + data.get(i).isVat()
						+ "," + data.get(i).getKpmg_hours() + ","
						+ data.get(i).getKpmg_claimed() + "\n";

				builder.append(objectString);
			}
		}
		// ----------------------------------------------

		// Create file
		// ----------------------------------
		File myFile = new File(getFilesDir() + "/" + "expenses.csv");
		myFile.setReadable(true, false);

		if (myFile != null) {
			try {
				FileOutputStream fOut = openFileOutput("expenses.csv",
						Context.MODE_WORLD_READABLE);
				OutputStreamWriter osw = new OutputStreamWriter(fOut);
				osw.write(builder.toString());
				osw.flush();
				osw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// ----------------------------------
		String recipient = ParseUser.getCurrentUser().getEmail();
		String subject = "Your DriverConnex Expenses";

		ArrayList<String> paths = new ArrayList<String>();
		paths.add(myFile.getAbsolutePath());

		// Sends email
		Utilities.sendEmail(ReviewExpensesActivity.this, recipient, "",
				subject, "", paths);
	}
}

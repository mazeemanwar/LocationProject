package com.driverconnex.journeys;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.TreeSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.driverconnex.adapter.ListJourneyAdapter;
import com.driverconnex.app.R;
import com.driverconnex.utilities.Utilities;
import com.parse.ParseUser;

/**
 * Activity for displaying a list of saved journeys.
 * 
 * @author Yin Lee (SGI)
 * @author Adrian Klimczak
 */
public class ReviewJourneysActivity extends Activity {
	private ArrayList<DCJourney> data = new ArrayList<DCJourney>();
	private TreeSet<Integer> separatorsSet = new TreeSet<Integer>();

	private ListView list;
	private ListJourneyAdapter adapter;
	private RelativeLayout bottomActionBar;
	private ImageButton exportBtn;
	private ImageButton deleteBtn;

	private boolean isEditMode = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_journey_review);

		list = (ListView) findViewById(R.id.list);
		bottomActionBar = (RelativeLayout) findViewById(R.id.bottomActionBar);
		exportBtn = (ImageButton) findViewById(R.id.exportBtn);
		deleteBtn = (ImageButton) findViewById(R.id.deleteBtn);

		exportBtn.setOnClickListener(onClickListener);
		deleteBtn.setOnClickListener(onClickListener);

		View divider = new View(this);
		divider.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
		list.addFooterView(divider, null, false);

		adapter = new ListJourneyAdapter(this, data, separatorsSet);
		list.setAdapter(adapter);
		list.setOnItemClickListener(onitemClickListener);
	}

	@Override
	protected void onResume() {
		super.onResume();

		getJourneys();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_edit, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			overridePendingTransition(R.anim.null_anim, R.anim.slide_out);
			return true;
		} else if (item.getItemId() == R.id.action_edit) {
			Animation anim = null;

			// If bottom action bar is not shown, then show it, otherwise hide
			// it
			if (!bottomActionBar.isShown()) {
				bottomActionBar.setVisibility(View.VISIBLE);
				anim = AnimationUtils.loadAnimation(this,
						R.anim.bottom_actionbar_show);
				isEditMode = true;
			} else {
				bottomActionBar.setVisibility(View.INVISIBLE);
				anim = AnimationUtils.loadAnimation(this,
						R.anim.bottom_actionbar_hide);
				isEditMode = false;
			}

			bottomActionBar.startAnimation(anim);
		} else if (item.getItemId() == R.id.action_export) {
			exportToCSVFile();
		}

		return super.onOptionsItemSelected(item);
	}

	private OnItemClickListener onitemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (isEditMode) {
				if (!view.isActivated())
					data.get(position).setSelected(true);
				else
					data.get(position).setSelected(false);

				adapter.notifyDataSetChanged();
			} else {
				Intent intent = new Intent(ReviewJourneysActivity.this,
						JourneyDetailsActivity.class);
				intent.putExtra("journey", data.get(position));
				intent.putExtra("modify", true);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_left_sub,
						R.anim.slide_left_main);
			}
		}
	};

	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v == exportBtn) {
				exportToCSVFile();
			} else if (v == deleteBtn) {
				new AlertDialog.Builder(ReviewJourneysActivity.this)
						.setTitle("Delete Journeys")
						.setMessage(
								getResources().getString(
										R.string.journey_delete_warning))
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// Stores journey IDs that we need in
										// order to delete journey points and
										// behaviour points
										ArrayList<Long> journeyIDs = new ArrayList<Long>();

										JourneyDataSource dataSource = new JourneyDataSource(
												ReviewJourneysActivity.this);
										dataSource.open();

										// Deletes selected journeys
										for (int i = 0; i < data.size(); i++) {
											if (data.get(i).isSelected()) {
												journeyIDs.add(data.get(i)
														.getId());
												dataSource.deleteJourney(data
														.get(i).getId());
											}
										}

										dataSource.close();

										// Journeys are deleted, now delete in
										// background all journey points and
										// behaviour points associated with
										// deleted journeys
										new deleteJourneysTask(
												ReviewJourneysActivity.this)
												.execute(journeyIDs);

										// Refreshes the list of journeys
										getJourneys();
									}
								})
						.setNegativeButton(android.R.string.cancel,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).show();
			}
		}
	};

	private void getJourneys() {
		if (!data.isEmpty())
			data.clear();

		if (!separatorsSet.isEmpty())
			separatorsSet.clear();

		JourneyDataSource dataSource = new JourneyDataSource(
				ReviewJourneysActivity.this);
		dataSource.open();
		ArrayList<DCJourney> journeys = dataSource.getAllJourneys();
		dataSource.close();

		String d = "";

		for (int i = 0; i < journeys.size(); i++) {
			// Journey is created when user starts tracking, but it should be
			// hidden until it's confirmed
			// to be saved. Such journey doesn't have createDate. So skip this
			// one.
			if (journeys.get(i).getCreateDate() != null) {
				if (!journeys.get(i).getCreateDate().equals(d)) {
					data.add(journeys.get(i));
					separatorsSet.add(data.size() - 1);
					d = journeys.get(i).getCreateDate();
				}

				data.add(journeys.get(i));
			}
		}

		adapter.notifyDataSetChanged();
	}

	private class deleteJourneysTask extends
			AsyncTask<ArrayList<Long>, Void, ArrayList<Long>> {
		private Context context;

		public deleteJourneysTask(Context context) {
			this.context = context;
		}

		@Override
		protected ArrayList<Long> doInBackground(ArrayList<Long>... params) {
			JourneyDataSource dataSource = new JourneyDataSource(context);
			dataSource.open();

			ArrayList<Long> journeyIDs = params[0];

			// Deletes all behaviour points and journey points associated with
			// the selected journeys
			for (int i = 0; i < journeyIDs.size(); i++) {
				dataSource.deleteBehaviourPoints(journeyIDs.get(i));
				dataSource.deleteJourneyPoints(journeyIDs.get(i));
			}

			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<Long> journeyIDs) {
			super.onPostExecute(journeyIDs);
		}
	}

	private void exportToCSVFile() {
		// ONLY FOR TESTING
		// Used for testing DCBehaviourPoint to see recordings
		// ----------------------------------------------
		/*
		 * JourneyDataSource dataSource = new
		 * JourneyDataSource(ReviewJourneysActivity.this); dataSource.open();
		 * 
		 * ArrayList<File> behaviourFiles = new ArrayList<File>();
		 * 
		 * // Loop trough all data to export for(int i=0; i<dataToExport.size();
		 * i++) { if(dataToExport.get(i).isSelected()) {
		 * ArrayList<DCBehaviourPoint> points =
		 * dataSource.getBehaviourPoints(dataToExport.get(i).getId());
		 * 
		 * String behaviourString =
		 * "AccelX,AccelY,AccelZ,Activity,Speed,Flat,Landscape,Portrait\n";
		 * StringBuilder behaviourBuilder = new StringBuilder();
		 * behaviourBuilder.append(behaviourString);
		 * 
		 * for(int j=0; j<points.size(); j++) { String objectString = ""+
		 * points.get(j).getAccelerationX()+"," +
		 * points.get(j).getAccelerationY()+"," +
		 * points.get(j).getAccelerationZ()+"," +
		 * points.get(j).getActivity()+"," + points.get(j).getSpeed()+"," +
		 * points.get(j).isDeviceFlat()+"," +
		 * points.get(j).isDeviceLandscape()+"," +
		 * points.get(j).isDevicePortrait()+"\n";
		 * 
		 * behaviourBuilder.append(objectString); }
		 * 
		 * File file = new File(getFilesDir() + "/" + "behaviour"+i+".csv");
		 * file.setReadable(true,false); behaviourFiles.add(file);
		 * 
		 * if( file != null ) { try { FileOutputStream fOut =
		 * openFileOutput("behaviour"+i+".csv", Context.MODE_WORLD_READABLE);
		 * OutputStreamWriter osw = new OutputStreamWriter(fOut);
		 * osw.write(behaviourBuilder.toString()); osw.flush(); osw.close(); }
		 * catch (IOException e) { e.printStackTrace(); } } } }
		 */
		// ----------------------------------------------

		// String expenseString =
		// "Description,Start Time,End Time,Duration,Distance,Business,Expense,"
		// + "Start Address,End Address,Avg. Speed,Max Speed,Emissions\n";
		String expenseString = "Description,Start Time,End Time,Duration,Distance,Business,Expense,"
				+ "Start Address,End Address,Avg. Speed,Emissions\n";

		StringBuilder builder = new StringBuilder();
		builder.append(expenseString);

		// Loop trough all data to export
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).isSelected()) {
				data.get(i).setSelected(false);

				String startAddress = "";
				String endAddress = "";

				// Remove new lines from Start Address
				// --------------------
				TextUtils.StringSplitter splitter = new TextUtils.SimpleStringSplitter(
						'\n');
				splitter.setString(data.get(i).getStartAddr());

				for (String s : splitter) {
					startAddress += s + " ";

					// if(splitter.iterator().hasNext())
					// startAddress += ", ";
				}

				// Remove new lines from End Address
				// --------------------
				splitter.setString(data.get(i).getEndAddr());

				for (String s : splitter) {
					System.out.println("description " + s + "end addreass "
							+ endAddress);
					endAddress += s + " ";
				}
				System.out.println("description "
						+ data.get(i).getDescription());
				String objectString = "" + data.get(i).getDescription() + ","
						+ data.get(i).getStartTime() + ","
						+ data.get(i).getEndTime() + ","
						+ data.get(i).getDuration() + ","
						+ data.get(i).getDistance() + ","
						+ data.get(i).isBusiness() + ","
						+ data.get(i).getExpense() + "," + startAddress + ","
						+ endAddress + "," + "" + data.get(i).getAvgSpeed()
						+ "," +
						// ""+data.get(i).getTopSpeed()+"," +
						"" + data.get(i).getEmissions() + "\n";

				builder.append(objectString);
			}
		}
		// ----------------------------------------------

		// Create file
		// ----------------------------------
		File myFile = new File(getFilesDir() + "/" + "journeys.csv");
		myFile.setReadable(true, false);

		if (myFile != null) {
			try {
				FileOutputStream fOut = openFileOutput("journeys.csv",
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

		// Send email
		String recipient = ParseUser.getCurrentUser().getEmail();
		String subject = "Your DriverConnex Journeys";

		ArrayList<String> paths = new ArrayList<String>();
		paths.add(myFile.getAbsolutePath());

		// ONLY FOR TESTING
		// Log.d("TEST", "total size"+behaviourFiles.size());
		// for(int i=0; i<behaviourFiles.size(); i++)
		// paths.add(behaviourFiles.get(i).getAbsolutePath());

		Utilities.sendEmail(ReviewJourneysActivity.this, recipient, "",
				subject, "", paths);
	}
}

package com.driverconnex.incidents;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.driverconnex.adapter.PhotoGridAdapter;
import com.driverconnex.app.R;
import com.driverconnex.basicmodules.PhotoActivity;
import com.driverconnex.data.PhotoObject;
import com.driverconnex.utilities.AssetsUtilities;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Activity displaying a grid of taken photos of the incident and it's used to
 * take a photo of the incident. Also it's used to just preview grid of taken
 * photos that were saved in Parse database.
 * 
 */

public class IncidentPhotosActivity extends Activity {
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

	private RelativeLayout bottomActionbar;
	private RelativeLayout loading;
	private ImageButton cameraBtn;
	private TextView photoInfoText;

	private boolean review = false;

	private GridView photoGrid;
	private PhotoGridAdapter adapter;

	private ArrayList<Uri> photoUri;
	private ArrayList<Bitmap> photoBitmaps;
	private String incidentId;
	private long mIncidentId;
	private ArrayList<PhotoObject> data = new ArrayList<PhotoObject>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_incident_photo_grid);

		photoInfoText = (TextView) findViewById(R.id.photoTextView);
		bottomActionbar = (RelativeLayout) findViewById(R.id.bottomActionBar);
		loading = (RelativeLayout) findViewById(R.id.loadSpinner);
		cameraBtn = (ImageButton) findViewById(R.id.cameraBtn);
		photoGrid = (GridView) findViewById(R.id.grid);

		photoGrid.setOnItemClickListener(onItemClickListener);
		cameraBtn.setOnClickListener(onClickListener);

		if (getIntent().getExtras() != null) {
			Bundle extras = getIntent().getExtras();

			mIncidentId = extras.getLong("incidentId");
			photoUri = extras.getParcelableArrayList("photos");
			review = extras.getBoolean("review");

			if (photoUri != null) {
				photoInfoText.setVisibility(View.INVISIBLE);
				new LoadPhotosTask().execute();
			}
		}

		if (photoUri == null)
			photoUri = new ArrayList<Uri>();

		if (photoBitmaps == null)
			photoBitmaps = new ArrayList<Bitmap>();

		adapter = new PhotoGridAdapter(IncidentPhotosActivity.this,
				photoBitmaps);
		photoGrid.setAdapter(adapter);

		getActionBar().setTitle(R.string.title_activity_back);

		if (review)
			// getPhotosByParse();
			displayPhoto();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!review) {
			bottomActionbar.setVisibility(View.VISIBLE);
			Animation anim = AnimationUtils.loadAnimation(this,
					R.anim.bottom_actionbar_show);
			bottomActionbar.startAnimation(anim);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode != RESULT_OK)
				return;

			if (data != null) {
				Uri imgUri = data.getData();

				int screenHeight = getWindowManager().getDefaultDisplay()
						.getHeight();
				Bitmap bmp = AssetsUtilities.bmpResize(AssetsUtilities
						.readBitmapFromUriInPortraitMode(this, imgUri, true),
						screenHeight);

				photoUri.add(imgUri);
				photoBitmaps.add(bmp);
			}

			adapter.notifyDataSetChanged();

			if (photoInfoText.getVisibility() == View.VISIBLE)
				photoInfoText.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			Intent returnIntent = new Intent();
			returnIntent.putExtra("photos", photoUri);
			setResult(RESULT_OK, returnIntent);

			finish();
			overridePendingTransition(R.anim.slide_right_main,
					R.anim.slide_right_sub);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v == cameraBtn) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent,
						CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
			}
		}
	};

	/**
	 * On click listener for the items in the list
	 */
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent = new Intent(IncidentPhotosActivity.this,
					PhotoActivity.class);

			intent.putExtra("photo", photoUri.get(position));

			if (review)
				intent.putExtra("imageDownloaded", true);

			startActivity(intent);
			overridePendingTransition(R.anim.slide_left_sub,
					R.anim.slide_left_main);
		}
	};

	/**
	 * Loads photos.
	 * 
	 */
	private class LoadPhotosTask extends AsyncTask<String, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			loading.setVisibility(View.VISIBLE);
		}

		@Override
		protected Boolean doInBackground(String... keywords) {
			for (int i = 0; i < photoUri.size(); i++) {
				// Extract BMP from the URI
				Bitmap bmp = AssetsUtilities.readBitmapFromUriInPortraitMode(
						IncidentPhotosActivity.this, photoUri.get(i), true);
				photoBitmaps.add(bmp);
			}

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			loading.setVisibility(View.GONE);
		}
	}

	/**
	 * Queries for the photos from the Parse database.
	 */
	private void getPhotosByParse() {
		loading.setVisibility(View.VISIBLE);
		photoInfoText.setVisibility(View.INVISIBLE);

		ParseQuery<ParseObject> query = ParseQuery.getQuery("DCIncident");
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
		query.getInBackground(incidentId, new GetCallback<ParseObject>() {
			@Override
			public void done(ParseObject object, ParseException e) {
				if (e == null) {
					ParseQuery<ParseObject> query = object.getRelation(
							"incidentPhotos").getQuery();
					query.findInBackground(new FindCallback<ParseObject>() {
						@Override
						public void done(List<ParseObject> objects,
								ParseException e) {
							if (e == null) {
								ArrayList<byte[]> bytes = new ArrayList<byte[]>();

								// Loop trough all incident photos
								for (int i = 0; i < objects.size(); i++) {
									// Get photo file from the parse
									ParseFile photo = (ParseFile) objects
											.get(i).get("photoFile");

									try {
										if (photo != null)
											bytes.add(photo.getData());
									} catch (ParseException e1) {
										e1.printStackTrace();
									}
								}

								// Get files out of bytes
								ArrayList<File> files = AssetsUtilities
										.saveIncidentPhoto(
												IncidentPhotosActivity.this,
												bytes);

								// Loop trough all files
								for (int i = 0; i < files.size(); i++) {
									// Get uri from the file
									Uri uri = Uri.fromFile(files.get(i));
									// Get bitmap from the uri
									Bitmap bmp = AssetsUtilities
											.readBitmapFromUri(
													IncidentPhotosActivity.this,
													uri);

									photoBitmaps.add(bmp);
									photoUri.add(uri);
								}

								if (!files.isEmpty()) {
									adapter.notifyDataSetChanged();

									if (photoInfoText.getVisibility() == View.VISIBLE)
										photoInfoText
												.setVisibility(View.INVISIBLE);
								}
							} else {
								photoInfoText.setVisibility(View.VISIBLE);
							}

							loading.setVisibility(View.INVISIBLE);
						}
					});
				}
			}
		});
	}

	private void displayPhoto() {
		IncidentDataSource mDataSource = new IncidentDataSource(
				IncidentPhotosActivity.this);
		mDataSource.open();
		long id = mIncidentId;
		data = mDataSource.getIncidentPhoto(mIncidentId);

		mDataSource.close();
		ArrayList<byte[]> bytes = new ArrayList<byte[]>();
		for (int i = 0; i < data.size(); i++) {

			byte[] photoByte = data.get(i).getPhotoByte();

			bytes.add(photoByte);
		}
		System.out.println(data);

		// Get files out of bytes
		ArrayList<File> files = AssetsUtilities.saveIncidentPhoto(
				IncidentPhotosActivity.this, bytes);

		// Loop trough all files
		for (int i = 0; i < files.size(); i++) {
			// Get uri from the file
			Uri uri = Uri.fromFile(files.get(i));
			// Get bitmap from the uri
			Bitmap bmp = AssetsUtilities.readBitmapFromUri(
					IncidentPhotosActivity.this, uri);

			photoBitmaps.add(bmp);
			photoUri.add(uri);
		}

		if (!files.isEmpty()) {
			adapter.notifyDataSetChanged();

			if (photoInfoText.getVisibility() == View.VISIBLE)
				photoInfoText.setVisibility(View.INVISIBLE);
		}

	}
}

package com.driverconnex.vehicles;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.driverconnex.app.R;
import com.driverconnex.utilities.AssetsUtilities;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Activity for taking a photo for the vehicle.
 * @author Adrian Klimczak
 *
 */

public class AddVehiclePhotoActivity extends Activity 
{
	private final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private final int GET_IMAGE_ACTIVITY_REQUEST_CODE = 200;
	
	private ImageView photoImg;
	private ImageButton cameraBtn, albumsBtn;
	private RelativeLayout bottomActionbar;
	private RelativeLayout loading;

	private DCVehicle vehicle;
	private boolean isEdit = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_photo);

		loading = (RelativeLayout) findViewById(R.id.loadSpinner);
		photoImg = (ImageView) findViewById(R.id.photo);
		bottomActionbar = (RelativeLayout) findViewById(R.id.bottomActionBar);
		cameraBtn = (ImageButton) findViewById(R.id.cameraBtn);
		albumsBtn = (ImageButton) findViewById(R.id.albumsBtn);

		PackageManager pm = this.getPackageManager();
		
		// Check if device has got a camera
		if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) 
			cameraBtn.setOnClickListener(onClickListener);
		else
			cameraBtn.setVisibility(View.INVISIBLE);
		
		albumsBtn.setOnClickListener(onClickListener);

		if (getIntent().getExtras() != null) 
		{
			vehicle = (DCVehicle) getIntent().getExtras().getSerializable("vehicle");
			isEdit = getIntent().getExtras().getBoolean("is_edit", false);

			AssetsUtilities.cleanTempMediaFile();

			if (isEdit && vehicle.isPhoto())
				new retrieveTask().execute();
		}
	}

	@Override
	protected void onResume() 
	{
		super.onResume();

		bottomActionbar.setVisibility(View.VISIBLE);
		Animation anim = AnimationUtils.loadAnimation(this,	R.anim.bottom_actionbar_show);
		bottomActionbar.startAnimation(anim);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_save, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if (item.getItemId() == android.R.id.home) 
		{
			finish();
			overridePendingTransition(R.anim.slide_right_main,	R.anim.slide_right_sub);
			return true;
		}
		else if (item.getItemId() == R.id.action_save) 
		{
			if(photoImg.getDrawable() != null)
			{
				Bitmap tempBmp = ((BitmapDrawable)photoImg.getDrawable()).getBitmap();
				
				if (isEdit) 
				{
					if (tempBmp != null) 
						new UpdateTask().execute(tempBmp);
				} 
				else 
				{
					if (tempBmp != null)
						new SaveTask().execute(tempBmp);
				}
			}
			else
			{
				// User didn't choose any photo so...
				vehicle.setPhoto(false);
				finish();
				overridePendingTransition(R.anim.slide_left_sub, R.anim.slide_left_main);
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private OnClickListener onClickListener = new OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{
			if (v == cameraBtn) 
			{
				// create Intent to take a picture and return control to the
				// calling application
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

				// start the image capture Intent
				startActivityForResult(intent,	CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
			}
			else if (v == albumsBtn) 
			{
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent, GET_IMAGE_ACTIVITY_REQUEST_CODE);
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE	|| requestCode == GET_IMAGE_ACTIVITY_REQUEST_CODE) 
		{	
			if (resultCode != RESULT_OK)
				return;

			if (data != null) 
			{	
				Bitmap bmp = null;
				int screenHeight = getWindowManager().getDefaultDisplay().getHeight();	
				bmp = AssetsUtilities.bmpResize(AssetsUtilities.readBitmapFromUriInPortraitMode(AddVehiclePhotoActivity.this, data.getData(), true), screenHeight);
				
				if(bmp != null)
					photoImg.setImageBitmap(bmp);
			}
		} 
		else
			Log.e("Capture Photo", "Failure!");
	}

	private class retrieveTask extends AsyncTask<String, Void, Bitmap> 
	{
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
			loading.setVisibility(View.VISIBLE);
		}

		@Override
		protected Bitmap doInBackground(String... params) 
		{
			ParseQuery<ParseObject> query = ParseQuery.getQuery("DCVehicle");
			
			Bitmap tempBmp = null;
			try {
				ParseObject mVehicle = query.get(vehicle.getId());
				ParseFile photo = (ParseFile) mVehicle.get("vehiclePhoto");
				
				byte[] bytes = null;
				bytes = photo.getData();
				
				if (bytes != null) 
				{				
					int screenHeight = getWindowManager().getDefaultDisplay().getHeight();					
					tempBmp = AssetsUtilities.bmpResize(AssetsUtilities.readBitmap(bytes), screenHeight);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return tempBmp;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) 
		{
			super.onPostExecute(bitmap);
			
			if(bitmap != null)
				photoImg.setImageBitmap(bitmap);
			
			loading.setVisibility(View.INVISIBLE);
		}
	}
	
	private class UpdateTask extends AsyncTask<Bitmap, Void, String> 
	{
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
			loading.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(Bitmap... params) 
		{
			ByteArrayOutputStream temp = new ByteArrayOutputStream();
			params[0].compress(Bitmap.CompressFormat.JPEG, 50, temp);
			byte[] data = temp.toByteArray();
			
			// Update vehicle with new photo source
			vehicle.setPhotoSrc(data);
			// Create ParseFile for the photo
			ParseFile file = new ParseFile("vehicle.png", data);

			try 
			{
				file.save();
				temp.close();
				ParseQuery<ParseObject> query = ParseQuery.getQuery("DCVehicle");
				ParseObject mVehicle = query.get(vehicle.getId());
				mVehicle.put("vehiclePhoto", file);
				mVehicle.save();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) 
		{
			super.onPostExecute(result);
			loading.setVisibility(View.INVISIBLE);
			
			Intent intent = new Intent();
			intent.putExtra("vehicle", vehicle);
			setResult(RESULT_OK, intent);
			
			finish();
			overridePendingTransition(R.anim.slide_right_main,	R.anim.slide_right_sub);
		}

	}

	private class SaveTask extends AsyncTask<Bitmap, Void, String> 
	{
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
			loading.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(Bitmap... params) 
		{
			if (params[0] != null) 
			{
				File file = AssetsUtilities.getOutputMediaFile(AssetsUtilities.MEDIA_TYPE_IMAGE);

				// Encode the file as a JPEG image.
				FileOutputStream outStream;
				try {
					outStream = new FileOutputStream(file);
					params[0].compress(Bitmap.CompressFormat.JPEG, 70, outStream);
					/* 100 to keep full quality of the image */
					outStream.flush();
					outStream.close();
					vehicle.setPhoto(true);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) 
		{
			super.onPostExecute(result);
			
			loading.setVisibility(View.GONE);
			
			Intent intent = new Intent();
			intent.putExtra("vehicle", vehicle);
			setResult(RESULT_OK, intent);
			
			finish();
			overridePendingTransition(R.anim.slide_right_main,	R.anim.slide_right_sub);
		}
	}
}

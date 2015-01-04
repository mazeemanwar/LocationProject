package com.driverconnex.adapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.driverconnex.app.R;
import com.driverconnex.community.DCUser;
import com.driverconnex.utilities.AssetsUtilities;

/**
 * Adapter for displaying list of friends. Adapter takes ArrayList of DCUser which contains user information and ArrayList of byte[] which is used to extract photo of the user.
 * @author Adrian Klimczak
 *
 */

public class FriendsListAdapter extends BaseAdapter 
{
	private Context context;
	private ArrayList<DCUser> users;
	private ArrayList<byte[]> photoSrc;
	private Bitmap tempBmp;
	private Options tempBmpOptions;

	public FriendsListAdapter(Context context, ArrayList<DCUser> users,ArrayList<byte[]> photo) 
	{
		super();
		this.context = context;
		this.users = users;
		this.photoSrc = photo;

		if (photo.size() != 0) 
		{
			// Loop trough all photos
			for (int i = 0; i < photo.size(); i++) 
			{
				// Check if photo really exists
				if (photo.get(i) != null) 
				{
					// Extracts photo of the user from byte[]
					try {
						InputStream is = new ByteArrayInputStream(photoSrc.get(i));
						tempBmpOptions = new BitmapFactory.Options();
						tempBmpOptions.inJustDecodeBounds = true;
						tempBmpOptions.inPreferredConfig = Config.ARGB_8888;
						tempBmp = BitmapFactory.decodeStream(is, null,tempBmpOptions);
						tempBmpOptions.inSampleSize = AssetsUtilities.calculateInSampleSize(tempBmpOptions, 80, 80);
						tempBmpOptions.inJustDecodeBounds = false;
						tempBmpOptions.inBitmap = tempBmp;
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					break;
				}
			}

		}
	}

	@Override
	public int getCount() {
		return users.size();
	}

	@Override
	public Object getItem(int position) {
		return users.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View row = convertView;
		ListHolder holder = null;

		final int pos = position;
		
		if (row == null) 
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
			row = (View) inflater.inflate(R.layout.list_friend_item, null);

			holder = new ListHolder();
			holder.title = (TextView) row.findViewById(R.id.mainTitle);
			holder.subtitle = (TextView) row.findViewById(R.id.subTitle);
			holder.pic = (ImageView) row.findViewById(R.id.listPicture);
			
			row.setTag(holder);	
		} 
		else 
		{
			holder = (ListHolder) row.getTag();
		}

		holder.title.setText(users.get(position).getFirstName() + " " + users.get(position).getLastName());
		
		if(users.get(position).getStatus() != null)
			holder.subtitle.setText(users.get(position).getStatus());
		else
			holder.subtitle.setText("Not tracking a journey");
		
		if (users.get(position).isPhoto()) 
		{
			try {
				InputStream is = new ByteArrayInputStream(photoSrc.get(position));
				tempBmp = BitmapFactory.decodeStream(is, null, tempBmpOptions);

				holder.pic.setImageBitmap(tempBmp);

				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		} 

		return row;
	}

	private static class ListHolder 
	{
		TextView title;
		TextView subtitle;
		ImageView pic;
	}

	public void recycleBitmap() {
		AssetsUtilities.bmpRecycle(this.tempBmp);
	}
}

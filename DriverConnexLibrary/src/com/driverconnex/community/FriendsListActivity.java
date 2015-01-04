package com.driverconnex.community;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.driverconnex.adapter.FriendsListAdapter;
import com.driverconnex.app.R;
import com.driverconnex.utilities.ParseUtilities;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

/**
 * Activity for displaying list of friends.
 * 
 * NOTE: 
 * Entire community section is not working since Greg made some changes in the Parse database.
 * 
 * @author Created by Adrian Klimczak
 * 
 */

public class FriendsListActivity extends Activity 
{
	private ListView list;
	private RelativeLayout loading;
	
	private ArrayList<DCUser> users = new ArrayList<DCUser>();
	private FriendsListAdapter adapter;
	private ArrayList<byte[]> tempPhoto;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		list = (ListView) findViewById(R.id.list);
		loading = (RelativeLayout) findViewById(R.id.loadSpinner);
		
		list.setOnItemClickListener(onItemClickListener);

		tempPhoto = new ArrayList<byte[]>();
		
		// Enable loading bar
		loading.setVisibility(View.VISIBLE);
		getFriendsByParse();
	}

	/**
	 * Handles what happens when item from the list is clicked/touched
	 */
	private OnItemClickListener onItemClickListener = new OnItemClickListener() 
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,	long index) 
		{
			Bundle extras = new Bundle();
			
			extras.putSerializable("user", users.get(position));
			
			Intent intent = new Intent(FriendsListActivity.this, UserProfileActivity.class);			
			intent.putExtras(extras);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_left_sub, R.anim.slide_left_main);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.vehicle, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if (item.getItemId() == android.R.id.home) 
		{
			
			finish();
			overridePendingTransition(R.anim.null_anim, R.anim.slide_out);
			return true;
		}
		else if (item.getItemId() == R.id.action_plus) 
		{
			Intent intent = new Intent(FriendsListActivity.this, InviteFriendsActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_left_sub, R.anim.slide_left_main);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Gets list of friends from the parse database
	 */
	private void getFriendsByParse() 
	{
		final ParseUser user = ParseUser.getCurrentUser();
		
		ParseRelation<ParseUser> userFriends = user.getRelation("userFriends"); 	 
		ParseQuery<ParseUser> query = userFriends.getQuery();
		
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
		query.findInBackground(new FindCallback<ParseUser>() 
		{
			public void done(final List<ParseUser> userList, ParseException e) 
			{
				if (e == null) 
				{	
					// Set adapter
					adapter = new FriendsListAdapter(FriendsListActivity.this, users, tempPhoto);
					list.setAdapter(adapter);
					
					// Loop through all users that are friends of the user
					for (int i = 0; i < userList.size(); i++)
					{	
						// Convert user, so that we can easly use it
						final DCUser user = ParseUtilities.convertUser(userList.get(i));
									
						// Get photo from the parse
						ParseFile photo = (ParseFile) userList.get(i).get("userProfilePhoto");
								
						if (photo != null)
						{
							photo.getDataInBackground(new GetDataCallback() 
							{
								@Override
								public void done(byte[] data, ParseException e) 
								{
									if(e == null)
									{
										if (data == null) 
										{
											user.setPhoto(false);
											tempPhoto.add(null);
										} 
										else 
										{
											user.setPhoto(true);
											tempPhoto.add(data);
										}
										
										adapter.notifyDataSetChanged();
									}
								}
							});
						}
												
						users.add(user);
						adapter.notifyDataSetChanged();
					}
				}
				else 
				{
					Log.e("Get User", e.getMessage());
				}	
				
				// Disable loading bar
				loading.setVisibility(View.GONE);
			}
		});
	}
}

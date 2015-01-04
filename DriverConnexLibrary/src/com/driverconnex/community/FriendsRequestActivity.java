package com.driverconnex.community;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.driverconnex.adapter.FriendsRequestListAdapter;
import com.driverconnex.app.R;
import com.driverconnex.utilities.ParseUtilities;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Activity for displaying list of friends requests
 * 
 * NOTE: 
 * Entire community section is not working since Greg made some changes in the Parse database.
 * @author Adrian Klimczak
 */

public class FriendsRequestActivity extends Activity 
{
	private ListView list;
	private RelativeLayout loading;
	
	// Used for the adapter to display list of requests 
	private ArrayList<DCUser> users = new ArrayList<DCUser>();
	private FriendsRequestListAdapter adapter;
	
	// Used to store temporal photos of the users
	private ArrayList<byte[]> tempPhoto = new ArrayList<byte[]>();

	// Used for user friend relation
	private ArrayList<ParseUser> requestSenders = new ArrayList<ParseUser>();
	// Used for parse cloud code
	private ArrayList<ParseObject> requests = new ArrayList<ParseObject>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		list = (ListView) findViewById(R.id.list);
		loading = (RelativeLayout) findViewById(R.id.loadSpinner);
		
		list.setOnItemClickListener(onItemClickListener);

		// Enable loading bar
		loading.setVisibility(View.VISIBLE);
		getFriendsRequestsByParse();
	}

	/**
	 * Handles what happens when item from the list is clicked/touched
	 */
	private OnItemClickListener onItemClickListener = new OnItemClickListener() 
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, final int position,	long index) 
		{
			new AlertDialog.Builder(FriendsRequestActivity.this)
			.setTitle("Add Friend")
			.setPositiveButton(R.string.action_confirm,	new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog,	int which) 
				{
					addFriend(position);
				}
			})
			.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() 
			{	
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
				}
			}).show();
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if (item.getItemId() == android.R.id.home) 
		{
			
			finish();
			overridePendingTransition(R.anim.null_anim, R.anim.slide_out);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Gets list of vehicles from the parse database
	 */
	private void getFriendsRequestsByParse() 
	{
		ParseQuery<ParseObject> query = ParseQuery.getQuery("DCFriendRequest");
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
		query.findInBackground(new FindCallback<ParseObject>() 
		{
			@Override
			public void done(final List<ParseObject> friendRequestList, ParseException e) 
			{
				if (e == null) 
				{	
					if(friendRequestList.isEmpty())
					{
						loading.setVisibility(View.GONE);
						return;
					}
					
					requests = (ArrayList<ParseObject>) friendRequestList;
					
					// Loop through all friend requests
					for (int i = 0; i < friendRequestList.size(); i++)
					{	
						// Get request sender
						ParseUser requestSender = friendRequestList.get(i).getParseUser("requestSender");
						
						final int position = i;
						
						requestSender.fetchInBackground(new GetCallback<ParseUser>()
						{
							@Override
							public void done(ParseUser ParseUser, ParseException e) 
							{
								if(e == null)
								{
									requestSenders.add(ParseUser);
									
									// Convert user, so that we can easly use it
									DCUser user = ParseUtilities.convertUser(ParseUser);
									
									// Get photo from the parse
									ParseFile photo = (ParseFile) ParseUser.get("userProfilePhoto");
									byte[] data = null;
											
									try 
									{
										if (photo != null) 
											data = photo.getData();
									} 
									catch (ParseException e1) {
										e1.printStackTrace();
									}
											
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
									
									users.add(user);
									
									if(position == friendRequestList.size()-1)
									{
										// Set adapter
										adapter = new FriendsRequestListAdapter(FriendsRequestActivity.this, users, tempPhoto);
										list.setAdapter(adapter);
										// Disable loading bar
										loading.setVisibility(View.GONE);
									}
								}
							}				
						});
					}
				}
				else 
				{
					Log.e("Get User", e.getMessage());
				}	
			}
		});
	}
	
	private void addFriend(final int position)
	{
		loading.setVisibility(View.VISIBLE);
		
		ParseUser currentUser = ParseUser.getCurrentUser();
		
		if(currentUser.getACL() != null)
			currentUser.getACL().setReadAccess(requestSenders.get(position).getObjectId(), true);
		
		ParseRelation<ParseUser> relation = currentUser.getRelation("userFriends");
		relation.add(requestSenders.get(position));
		
		currentUser.saveInBackground(new SaveCallback()
		{
			@Override
			public void done(ParseException arg0) 
			{	
				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("requestID", requests.get(position).getObjectId());
				
				ParseCloud.callFunctionInBackground("acceptFriendRequest", params,
						new FunctionCallback <String>() 
				{
					@Override
					public void done(String result, com.parse.ParseException e) 
					{
						if (e == null) 
						{
							loading.setVisibility(View.INVISIBLE);
							
							users.remove(position);
							requestSenders.remove(position);
							requests.remove(position);
							
							adapter.notifyDataSetChanged();
					    }
					}
				});
			}	
		});
	}
}

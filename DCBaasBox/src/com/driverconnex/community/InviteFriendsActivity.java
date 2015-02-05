package com.driverconnex.community;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.driverconnex.adapter.ListAdapter;
import com.driverconnex.adapter.ListAdapterItem;
import com.driverconnex.app.R;
import com.driverconnex.utilities.Utilities;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseUser;

/**
 * Activity for inviting friends.
 * 
 * NOTE: 
 * Entire community section is not working since Greg made some changes in the Parse database.
 * 
 * @author Adrian Klimczak
 *
 */

public class InviteFriendsActivity extends Activity 
{
	private ListView list;
	//private InviteFriendsListAdapter adapter;
	private ListAdapter adapter;
	private RelativeLayout loading;
	private ArrayList<Contact> contacts = new ArrayList<Contact>();
	private ArrayList<ListAdapterItem> adapterData = new ArrayList<ListAdapterItem>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		
		list = (ListView) findViewById(R.id.list);
		loading = (RelativeLayout) findViewById(R.id.loadSpinner);
		
		list.setOnItemClickListener(onItemClickListener);
		
		new ServiceTask().execute();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_invite, menu);
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
		else if(item.getItemId() == R.id.action_invite)
		{
			// Allow other user to see current user
			final ParseUser user = ParseUser.getCurrentUser();
			
			// Send request to all selected users
			for(int i=0; i<contacts.size(); i++)
			{
				//if(contacts.get(i).isSelected())
				if(adapterData.get(i).selected)
				{
					final int index = i;
					
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("userID", contacts.get(i).getId());
					
					if(user.getACL() != null)
						user.getACL().setReadAccess(contacts.get(i).getId(), true);
					
					// Get members
					ParseCloud.callFunctionInBackground("sendFriendRequest", params,
							new FunctionCallback <String>() 
					{
						@Override
						public void done(String result, com.parse.ParseException e) 
						{
							if (e == null) 
							{
								// Check if this is the last invite sent
								if(index == contacts.size()-1)
									user.saveInBackground();
						    }
						}
					});
				}
			}
			
			finish();
			overridePendingTransition(R.anim.null_anim, R.anim.slide_out);
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	
	/**
	 * On click listener for the items in the list
	 */
	private OnItemClickListener onItemClickListener = new OnItemClickListener() 
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,	long id) 
		{
			if(!adapterData.get(position).selected)
				adapterData.get(position).selected = true;
			else		
				adapterData.get(position).selected = false;
			
			adapter.notifyDataSetChanged();
		}
	};
	
	private class ServiceTask extends AsyncTask<String, Void, Boolean> 
	{
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
			loading.setVisibility(View.VISIBLE);
		}

		@Override
		protected Boolean doInBackground(String... keywords) 
		{
			// Get emails
			ArrayList<String> emails =  Utilities.getEmailsFromAddressBook(InviteFriendsActivity.this);
			
			HashMap<String, Object> params = new HashMap<String, Object>();
			
			params.put("emailArray", emails);
			
			// Get members
			ParseCloud.callFunctionInBackground("getMembersFromAddressBook", params,
					new FunctionCallback<ArrayList<ArrayList<String>>>() 
			{
				@Override
				public void done(ArrayList<ArrayList<String>> result, com.parse.ParseException e) 
				{
					if (e == null) 
					{
						for(int i=0; i<result.size(); i++)
						{
							Contact contact = new Contact();
							contact.setEmail(result.get(i).get(0));
							contact.setFirstName(result.get(i).get(1));
							//contact.setLastName(result.get(i).get(2));
							contact.setId(result.get(i).get(3));
							
							ListAdapterItem item = new ListAdapterItem();
							
					        // Set texts         
					        String username = "";
					        
					        if(contact.getFirstName() != null)
					        	username += contact.getFirstName();
					        if(contact.getLastName() != null)
					        	username += " " + contact.getLastName();
							
							item.title = username;
							item.subtitle = contact.getEmail();
							item.subtitleColor = getResources().getColor(R.color.list_heading);
							
							adapterData.add(item);
							contacts.add(contact);
						}
						
						//adapter = new InviteFriendsListAdapter(InviteFriendsActivity.this, contacts);
						adapter = new ListAdapter(InviteFriendsActivity.this, adapterData, R.drawable.account_grey_28x28);
						list.setAdapter(adapter);
				    }
					
					loading.setVisibility(View.GONE);
				}
			});
			
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) 
		{
			super.onPostExecute(result);
		}	
	}
}

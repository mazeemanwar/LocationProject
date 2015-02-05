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
 * Activity for inviting drivers to use the app. It displays list of contacts to invite. 
 * 
 * NOTE: 
 * Entire community section is not working since Greg made some changes in the Parse database.
 * 
 * @author Adrian Klimczak
 * 
 */

public class ShareAppActivity extends Activity 
{
	private ListView list;
	private ListAdapter adapter;
	//private InviteFriendsListAdapter adapter;
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
			ArrayList<ArrayList<String>> convertedContacts = new ArrayList<ArrayList<String>>();
			
			ParseUser user = ParseUser.getCurrentUser();
			String currentUserFirstName = user.getString("userFirstName");
			
			// Prepare contacts for the Parse cloud function
			for(int i=0; i<contacts.size(); i++)
			{
				//if(contacts.get(i).isSelected())
				if(adapterData.get(i).selected)
				{
					ArrayList<String> contact = new ArrayList<String>();
					
					String name = "";
					
					if(contacts.get(i).getFirstName() != null)
						name += contacts.get(i).getFirstName();
					if(contacts.get(i).getLastName() != null)
						name += " " + contacts.get(i).getLastName();
					
					contact.add(name);
					
					if(contacts.get(i).getFirstName() != null)
						contact.add(contacts.get(i).getFirstName());
					else
						contact.add("");
					
					if(contacts.get(i).getLastName() != null)
						contact.add(contacts.get(i).getLastName());	
					else
						contact.add("");

					contact.add(contacts.get(i).getEmail());
					contact.add(currentUserFirstName);
					convertedContacts.add(contact);
				}
			}
			
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("userArrays", convertedContacts);
			
			loading.setVisibility(View.VISIBLE);
			
			// Send invites
			ParseCloud.callFunctionInBackground("batchUserInvite", params,
					new FunctionCallback<String>() 
			{
				@Override
				public void done(String result, com.parse.ParseException e) 
				{
					loading.setVisibility(View.GONE);	
					finish();
					overridePendingTransition(R.anim.null_anim, R.anim.slide_out);
				}
			});
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
			//if(!contacts.get(position).isSelected())
				//contacts.get(position).setSelected(true);
			//else		
				//contacts.get(position).setSelected(false);
			if(adapterData.get(position).selected)
				adapterData.get(position).selected = true;
			else
				adapterData.get(position).selected = false;
			
			adapter.notifyDataSetChanged();
		}
	};
	
	/**
	 * Gets contacts from the address book
	 *
	 */
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
			ArrayList<ArrayList<String>> addressBook = Utilities
			.getContactsFromAddressBook(ShareAppActivity.this);
			
			for(int i=0; i<addressBook.size(); i++)
			{
				Contact contact = new Contact();
				
				String arr[] = addressBook.get(i).get(0).split(" ");
				contact.setFirstName(arr[0]);
				
				if(arr.length == 2)
					contact.setLastName(arr[1]);
					
				contact.setEmail(addressBook.get(i).get(1));
				
				contacts.add(contact);
			}
			
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) 
		{
			super.onPostExecute(result);
			
			adapter = new ListAdapter(ShareAppActivity.this, adapterData, R.drawable.account_grey_28x28);
			//adapter = new InviteFriendsListAdapter(ShareAppActivity.this, contacts);
			list.setAdapter(adapter);
			loading.setVisibility(View.GONE);			
		}	
	}
}

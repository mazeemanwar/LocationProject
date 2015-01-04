package com.driverconnex.basicmodules;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.driverconnex.adapter.MessageListAdapter;
import com.driverconnex.app.R;
import com.driverconnex.data.Message;
import com.driverconnex.utilities.ThemeUtilities;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * 
 * @author Yin Lee (SGI)
 *  @author Muhammad Azeem Anwar
 *         NOTE: I haven't seen this activity in the action. I assume that it
 *         was created for the KPMGConnect app. Adrian Klimczak.
 */

public class MessageActivity extends Activity {
	private ListView msgList;
	private ArrayList<Message> messages;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);

		msgList = (ListView) findViewById(R.id.messageList);
		msgList.setOnItemClickListener(itemClickListener);
		View divider = new View(this);
		divider.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));

		divider.setBackgroundColor(ThemeUtilities.getMainInterfaceColor(this));
		msgList.addFooterView(divider, null, false);
		// msgList.addHeaderView(divider, null, false);

		getMessages();
	}

	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			Message msg = messages.get(position);
			Bundle bundle = new Bundle();
			bundle.putString("main_title", msg.getTitle());
			bundle.putString("sub_title", msg.getSubject());
			bundle.putString("date", msg.getCreateDate().toString());
			bundle.putString("body", msg.getBody());
			Intent intent = new Intent(MessageActivity.this,
					MessageViewActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in, R.anim.null_anim);
		}

	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			overridePendingTransition(R.anim.null_anim, R.anim.slide_out);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void getMessages() {
		messages = new ArrayList<Message>();

		// SharedPreferences prefs =
		// getActivity().getSharedPreferences(DriverConnexApp.PREFS,
		// getActivity().MODE_PRIVATE);

		ParseUser user = ParseUser.getCurrentUser();

		ParseQuery<ParseObject> query = ParseQuery.getQuery("DCMessage");
		// query.whereEqualTo("messageGlobal", true);
		// ArrayList<Object> users = new ArrayList<Object>();
		// users.add(JSONObject.NULL);
		// users.add(user);
		//
		// query.whereContainsAll("messageRecipient", users);
		query.whereEqualTo("messageClient", user.getParseObject("userClient"));
		query.whereDoesNotExist("messageDeleted");
		// query.fromLocalDatastore();
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> msglist, ParseException e) {
				if (e == null) {
					Log.i("Message", "Get messages successfully.");
					for (int i = 0; i < msglist.size(); i++) {
						Message message = new Message();
						message.setId(msglist.get(i).getObjectId());
						message.setBody(msglist.get(i).getString("messageBody"));
						// for testing purpose
						DateFormat dateFormat = new SimpleDateFormat(
								"yyyy/MM/dd");
						// get current date time with Date()
						Date date = new Date();
						// System.out.println(dateFormat.format(date)); don't
						// print it, but save it!
						String yourDate = dateFormat.format(msglist.get(i)
								.getCreatedAt());

						System.out.println("SSSS" + yourDate);
						message.setCreateDate(String.valueOf(msglist.get(i)
								.getCreatedAt()));
						message.setGlobal(msglist.get(i).getBoolean(
								"messageGlobal"));
						message.setTitle(msglist.get(i).getString(
								"messageTitle"));
						message.setSubject(msglist.get(i).getString(
								"messageSubject"));
						messages.add(message);

						msgList.setAdapter(new MessageListAdapter(
								MessageActivity.this, messages));
					}
				} else {
					Log.i("Message", "Failure with error!");
				}
			}
		});
	}

}

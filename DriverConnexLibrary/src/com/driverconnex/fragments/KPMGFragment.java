package com.driverconnex.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.driverconnex.adapter.MessageListAdapter;
import com.driverconnex.app.HomeActivity;
import com.driverconnex.app.R;
import com.driverconnex.basicmodules.MessageViewActivity;
import com.driverconnex.data.Message;
import com.driverconnex.singletons.DCMessageSingleton;
import com.driverconnex.utilities.ThemeUtilities;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Dashboard for KPMG app.
 * 
 * @author Yin Lee (SGI)
 * @author Adrian Klimczak
 * @author Muhammad Azeem Anwar
 */

public class KPMGFragment extends Fragment {
	private ListView msgList;
	private ArrayList<Message> messages;
	private ArrayList<ParseObject> parseObjList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_kpmg, container, false);

		msgList = (ListView) view.findViewById(R.id.homeList);
		View divider = new View(getActivity());
		divider.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));

		divider.setBackgroundColor(ThemeUtilities
				.getMainInterfaceColor(getActivity()));
		msgList.addFooterView(divider, null, false);
		msgList.setOnItemClickListener(itemClickListener);
		LinearLayout tabBar = (LinearLayout) view
				.findViewById(R.id.tabBar_logical);
		HomeActivity.createTabBar(getActivity(), tabBar, "modules_config.xml");

		return view;
	}

	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Message msg = messages.get(position);
			Bundle bundle = new Bundle();
			bundle.putString("main_title", msg.getTitle());
			bundle.putString("title", msg.getSubject());
			bundle.putString("date", msg.getCreateDate().toString());
			bundle.putString("body", msg.getBody());
			bundle.putString("date", msg.getCreateDate());
			bundle.putString("id", msg.getMessageObjectId());
			msg.setRead(true);
			Intent intent = new Intent(getActivity(), MessageViewActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.slide_in,
					R.anim.null_anim);
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		// getMessages();
		getMessageFromLocal();
	}

	private void getMessages() {

		// if (messages == null) {
		messages = new ArrayList<Message>();

		// }
		ParseUser user = ParseUser.getCurrentUser();

		ParseQuery<ParseObject> query = ParseQuery.getQuery("DCMessage");

		query.whereEqualTo("messageRecipient", user);
		query.whereDoesNotExist("messageDeleted");
		List<ParseObject> objects;
		try {
			objects = query.find();
			ParseObject.unpinAllInBackground("localMessage", objects);
			ParseObject.pinAllInBackground("localMessage", objects);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Online ParseQuery results

		ParseQuery<ParseObject> query2 = ParseQuery.getQuery("DCMessage");
		query2.orderByAscending("messageSendDate").fromLocalDatastore()
				.findInBackground(new FindCallback<ParseObject>() {

					@Override
					public void done(List<ParseObject> msglist, ParseException e) {
						if (e == null) {
							Log.i("Message", "Get messages successfully.");
							for (int i = 0; i < msglist.size(); i++) {
								Message message = new Message();
								message.setId(msglist.get(i).getString(
										"objectId"));
								message.setBody(msglist.get(i).getString(
										"messageBody"));
								message.setCreateDate(String.valueOf(msglist
										.get(i).getCreatedAt()));
								String date = String.valueOf(msglist.get(i)
										.getCreatedAt());
								System.out.println(date);
								message.setGlobal(msglist.get(i).getBoolean(
										"messageGlobal"));
								message.setTitle(msglist.get(i).getString(
										"messageTitle"));
								message.setSubject(msglist.get(i).getString(
										"messageSubject"));
								message.setMessageObjectId(msglist.get(i)
										.getObjectId().toString());
								message.setRead(msglist.get(i).getBoolean(
										"messageRead"));
								// ParseObject messageObj = new
								// ParseObject("DCMessage");
								// messageObj = msglist.get(i);
								// messageObj.pinInBackground();
								messages.add(message);

							}
							msgList.setAdapter(new MessageListAdapter(
									getActivity(), messages));

						} else {
							Log.d("score", "Error: " + e.getMessage());
						}
					}
				});

		// query.findInBackground(new FindCallback<ParseObject>() {
		// public void done(List<ParseObject> msglist, ParseException e) {
		// if (e == null) {
		//
		// Log.i("Message", "Get messages successfully.");
		// for (int i = 0; i < msglist.size(); i++) {
		// ParseObject messageObj = new ParseObject("DCMessage");
		// messageObj = msglist.get(i);
		// messageObj.pinInBackground();
		//
		// }
		//
		// } else {
		// Log.d("score", "Error: " + e.getMessage());
		// }
		// }
		// });

	}

	private void getMessageFromLocal() {

		messages = new ArrayList<Message>();
		messages = DCMessageSingleton.getDCModuleSingleton(getActivity())
				.getSerMessage();
		msgList.setAdapter(new MessageListAdapter(getActivity(), messages));

	}

}

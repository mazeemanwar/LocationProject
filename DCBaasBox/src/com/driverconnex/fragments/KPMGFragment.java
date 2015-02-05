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
		// getMessages();
		getMessagesByBaasBox();
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
			bundle.putString("title", msg.getTitle());
			String t = msg.getTitle();
			System.out.println(t);
			bundle.putString("sub_title", msg.getSubject());
			bundle.putString("date", msg.getCreateDate().toString());
			bundle.putString("body", msg.getBody());
			// bundle.putString("date", msg.getCreateDate());
			// bundle.putString("id", msg.getMessageObjectId());
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
	}

	private void getMessagesByBaasBox() {

		messages = new ArrayList<Message>();
		messages = DCMessageSingleton.getDCModuleSingleton(getActivity())
				.getSerMessage();
		msgList.setAdapter(new MessageListAdapter(getActivity(), messages));

//		BaasDocument.fetchAll("BAAMessage",
//				new BaasHandler<List<BaasDocument>>() {
//
//					@Override
//					public void handle(BaasResult<List<BaasDocument>> msglist) {
//						// TODO Auto-generated method stub
//						messages = new ArrayList<Message>();
//
//						for (int i = 0; i < msglist.value().size(); i++) {
//							Message message = new Message();
//							String myMsg = msglist.value().get(i)
//									.getString("title");
//							System.out.println(myMsg);
//							message.setTitle(msglist.value().get(i)
//									.getString("title"));
//							message.setBody(msglist.value().get(i)
//									.getString("body"));
//
//							// //
//							// message.setBody(msglist.get(i).getString("messageBody"));
//							String createdDate = msglist.value().get(i)
//									.getCreationDate();
//							message.setCreateDate(String.valueOf(createdDate));

							// message.setGlobal(msglist.get(i)
							// .getBoolean("messageGlobal"));
							// message.setTitle(msglist.get(i).getString("title"));
							// message.setSubject(msglist.get(i).getString(
							// "messageSubject"));
							// message.setMessageObjectId(msglist.get(i).getObjectId()
							// .toString());
							// if (msglist.value().get(i).getBoolean("read") ==
							// null) {
							// message.setRead(false);
							//
							// } else {
							// message.setRead(msglist.value().get(i)
							// .getBoolean("read"));
							// }
		// messages.add(message);
		//
		// }
		//
		// }
		// });
		// BaasDocument.fetch("BAAMessage",
		// "090dd688-2e9a-4dee-9afa-aad72a1efa93",
		// new BaasHandler<BaasDocument>() {
		//
		// @Override
		// public void handle(BaasResult<BaasDocument> arg0) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
		//
		// final BaasQuery GETMESSAGES = BaasQuery.builder()
		// .collection("BAAMessage")
		//
		// .build();
		// // then
		// GETMESSAGES.query(new BaasHandler<List<JsonObject>>() {
		//
		// @Override
		// public void handle(BaasResult<List<JsonObject>> msglist) {
		//
		// // TODO Auto-generated method stub
		// // JsonObject tObject = res.value().get(1);
		// // String make = res.value().get(1).getString("vehicleMake");
		// // System.out.println(make);
		// messages = new ArrayList<Message>();
		// for (int i = 0; i < msglist.value().size(); i++) {
		// Message message = new Message();
		// message.setTitle(msglist.value().get(i).getString("title"));
		// message.setBody(msglist.value().get(i).getString("body"));
		//
		// // //
		// // message.setBody(msglist.get(i).getString("messageBody"));
		// // message.setCreateDate(String.valueOf(msglist.value().get(i)
		// // .getCreatedAt()));
		//
		// // message.setGlobal(msglist.get(i)
		// // .getBoolean("messageGlobal"));
		// // message.setTitle(msglist.get(i).getString("title"));
		// // message.setSubject(msglist.get(i).getString(
		// // "messageSubject"));
		// // message.setMessageObjectId(msglist.get(i).getObjectId()
		// // .toString());
		// if (msglist.value().get(i).getBoolean("read") == null) {
		// message.setRead(false);
		//
		// } else {
		// message.setRead(msglist.value().get(i)
		// .getBoolean("read"));
		// }
		// messages.add(message);
		//
		// msgList.setAdapter(new MessageListAdapter(getActivity(),
		// messages));
		// }
		//
		// System.out.println(msglist);
		// }
		// });

	}

	private void getMessages() {
		messages = new ArrayList<Message>();

		ParseUser user = ParseUser.getCurrentUser();

		ParseQuery<ParseObject> query = ParseQuery.getQuery("DCMessage");

		query.whereEqualTo("messageRecipient", user);
		query.whereDoesNotExist("messageDeleted");
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> msglist, ParseException e) {
				if (e == null) {
					Log.i("Message", "Get messages successfully.");
					for (int i = 0; i < msglist.size(); i++) {
						Message message = new Message();
						message.setId(msglist.get(i).getString("objectId"));
						message.setBody(msglist.get(i).getString("messageBody"));
						message.setCreateDate(String.valueOf(msglist.get(i)
								.getCreatedAt()));

						message.setGlobal(msglist.get(i).getBoolean(
								"messageGlobal"));
						message.setTitle(msglist.get(i).getString(
								"messageTitle"));
						message.setSubject(msglist.get(i).getString(
								"messageSubject"));
						message.setMessageObjectId(msglist.get(i).getObjectId()
								.toString());
						message.setRead(msglist.get(i)
								.getBoolean("messageRead"));
						messages.add(message);

						msgList.setAdapter(new MessageListAdapter(
								getActivity(), messages));
					}
				} else
					Log.i("Message", "Failure with error!");
			}
		});
	}

}

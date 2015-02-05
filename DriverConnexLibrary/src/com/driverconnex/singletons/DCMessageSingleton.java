package com.driverconnex.singletons;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.driverconnex.adapter.MessageListAdapter;
import com.driverconnex.data.Message;
import com.driverconnex.utilities.Utilities;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class DCMessageSingleton {

	// Static member holds only one instance of the

	// SingletonExample class

	private static DCMessageSingleton singletonInstance;
	private static ArrayList<String> moduleFromServer = new ArrayList<String>();
	private static ArrayList<Message> messageUpdatedList = new ArrayList<Message>();
	private static ArrayList<Message> messageList = new ArrayList<Message>();

	// SingletonExample prevents any other class from instantiating

	private DCMessageSingleton() {

	}

	// Providing Global point of access

	public static DCMessageSingleton getDCModuleSingleton(final Context context) {

		if (null == singletonInstance) {

			singletonInstance = new DCMessageSingleton();
			System.out.println("with out creating object");
			int size = messageList.size();
			System.out.println(size);
			// if (moduleMenuList.size() < 1) {
			getMessage();

			// }
		}

		return singletonInstance;

	}

	public ArrayList<Message> getSerMessage() {
		getMessage();

		System.out.println("with out creating object");
		return messageUpdatedList;
	}

	private static void getMessage() {

		ParseQuery<ParseObject> query = ParseQuery.getQuery("DCMessage");
		// query.orderByAscending("messageSendDate");

		// query.fromLocalDatastore();
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> msglist, ParseException e) {
				if (e == null) {
					messageList = new ArrayList<Message>();

					for (int i = 0; i < msglist.size(); i++) {
						Message message = new Message();
						message.setId(msglist.get(i).getString("objectId"));
						message.setBody(msglist.get(i).getString("messageBody"));
//						message.setCreateDate(String.valueOf(msglist.get(i)
//								.getCreatedAt()));
						String createdDate = String.valueOf(msglist.get(i)
								.getCreatedAt());
						System.out.println(createdDate);
						createdDate = createdDate.substring(0,
								createdDate.lastIndexOf("+") - 1);

						String messageDate = Utilities
								.convertDateFormatUni(createdDate,
										"yyyy-MM-dd'T'HH:mm:ss",

										"MMM dd, yyyy, hh:mm a");

						message.setCreateDate(messageDate);

						
						
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
						// ParseObject messageObj = new
						// ParseObject("DCMessage");
						// messageObj = msglist.get(i);
						// messageObj.pinInBackground();
						messageList.add(message);

					}
					if (messageList.size() > messageUpdatedList.size()) {

						messageUpdatedList = new ArrayList<Message>();
						for (int i = 0; i < messageList.size(); i++) {
							messageUpdatedList.add(messageList.get(i));
						}
					}

				} else {
					Log.d("score", "Error: " + e.getMessage());
				}
			}
		});

	}
}

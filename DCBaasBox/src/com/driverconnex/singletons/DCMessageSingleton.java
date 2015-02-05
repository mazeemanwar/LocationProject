package com.driverconnex.singletons;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasQuery;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.json.JsonObject;
import com.driverconnex.data.Message;
import com.driverconnex.utilities.Utilities;

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

		String user = BaasUser.current().getName();
		final BaasQuery PREPARED_QUERY = BaasQuery.builder()
				.collection("BAAMessage").where("recipient = ?")
				.whereParams(user).build();
		// then
		PREPARED_QUERY.query(new BaasHandler<List<JsonObject>>() {

			@Override
			public void handle(BaasResult<List<JsonObject>> msglist) {
				// TODO Auto-generated method stub
				if (msglist.isSuccess()) {

					messageList = new ArrayList<Message>();

					for (int i = 0; i < msglist.value().size(); i++) {
						Message message = new Message();
						String myMsg = msglist.value().get(i)
								.getString("title");
						System.out.println(myMsg);

						message.setTitle(msglist.value().get(i)
								.getString("title"));
						message.setBody(msglist.value().get(i)
								.getString("body"));
						String createdDate = msglist.value().get(i)
								.getString("_creation_date");

						createdDate = createdDate.substring(0,
								createdDate.lastIndexOf("+") - 1);

						String messageDate = Utilities.convertDateFormatUni(
								createdDate, "yyyy-MM-dd'T'HH:mm:ss",

								"MMM dd, yyyy, hh:mm a");

						message.setCreateDate(messageDate);
						messageList.add(message);

					}
					if (messageList.size() > messageUpdatedList.size()) {

						messageUpdatedList = new ArrayList<Message>();
						for (int i = 0; i < messageList.size(); i++) {
							messageUpdatedList.add(messageList.get(i));
						}
					}
				} else {
					System.out.println();
				}

			}
		});

	}
}

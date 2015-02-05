package com.driverconnex.basicmodules;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import com.driverconnex.app.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * 
 * @author Yin Lee (SGI)
 * @author Muhammad Azeem Anwar
 */

public class MessageViewActivity extends Activity {

	private TextView mainTitle, subTitle, content, date;
	String msgObjectId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_view);

		mainTitle = (TextView) findViewById(R.id.titleView);
		date = (TextView) findViewById(R.id.dateView);
		content = (TextView) findViewById(R.id.msgContent);
		// date = (TextView) findViewById(R.id.msgDate);
		content.setMovementMethod(new ScrollingMovementMethod());
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		Bundle bundle = getIntent().getExtras();

		String title = bundle.getString("title");
		String datedd = bundle.getString("date");
		String body = bundle.getString("body");
		System.out.println(title + "" + body + "" + date);
		mainTitle.setText(bundle.getString("title"));
		content.setText(body);
		date.setText(bundle.getString("date"));
		msgObjectId = bundle.getString("id");
		updateMessageStatus();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == android.R.id.home) {
			finish();
			overridePendingTransition(R.anim.null_anim, R.anim.slide_out);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateMessageStatus() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("DCMessage");

		// Retrieve the object by id
		query.getInBackground(msgObjectId, new GetCallback<ParseObject>() {
			public void done(ParseObject messageObj, ParseException e) {
				if (e == null) {
					// Now let's update it with some new data. In this case,
					// only cheatMode and score
					// will get sent to the Parse Cloud. playerName hasn't
					// changed.
					messageObj.put("messageRead", true);
					messageObj.saveInBackground();
				}
			}
		});
	}

}

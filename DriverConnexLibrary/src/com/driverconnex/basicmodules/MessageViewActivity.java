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
 * @author Muhammad Azeem Anwar NOTE: I haven't seen this activity in the
 *         action. I assume that it was created for the KPMGConnect app. Adrian
 *         Klimczak.
 */

public class MessageViewActivity extends Activity {

	private TextView mainTitle, subTitle, content, date;
	String msgObjectId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_view);

		mainTitle = (TextView) findViewById(R.id.msgMainTitle);
		subTitle = (TextView) findViewById(R.id.msgSubTitle);
		content = (TextView) findViewById(R.id.msgContent);
		date = (TextView) findViewById(R.id.msgDate);
		content.setMovementMethod(new ScrollingMovementMethod());
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		Bundle bundle = getIntent().getExtras();

		mainTitle.setText(bundle.getString("main_title"));
		subTitle.setText(bundle.getString("sub_title"));
		content.setText(bundle.getString("body"));
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
			public void done(ParseObject gameScore, ParseException e) {
				if (e == null) {
					// Now let's update it with some new data. In this case,
					// only cheatMode and score
					// will get sent to the Parse Cloud. playerName hasn't
					// changed.
					gameScore.put("messageRead", true);
					gameScore.saveInBackground();
				}
			}
		});
	}

}

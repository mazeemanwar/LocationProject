package com.driverconnex.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.driverconnex.app.R;
import com.driverconnex.data.Message;

/**
 * 
 * @author Yin Lee (SGI)
 * 
 *         Probably it's used for KPMG dashboard displaying messages, but I have
 *         never seen it in the action. comment by Adrian Klimczak
 */

public class MessageListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Message> messages;

	public MessageListAdapter(Context context, ArrayList<Message> messages) {
		super();
		this.context = context;
		this.messages = messages;
	}

	@Override
	public int getCount() {
		return messages.size();
	}

	@Override
	public Object getItem(int position) {
		return messages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(context.LAYOUT_INFLATER_SERVICE);
			row = (View) inflater.inflate(R.layout.list_item_message, null);
		}

		TextView title = (TextView) row.findViewById(R.id.listMainTitle);
		TextView subtitle = (TextView) row.findViewById(R.id.listSubTitle);

		title.setText(messages.get(position).getCreateDate());
		subtitle.setText(messages.get(position).getSubject());
		//
		String s = messages.get(position).getSubject();
		if (messages.get(position).isRead()) {

			title.setTypeface(null, Typeface.NORMAL);
			subtitle.setTypeface(null, Typeface.NORMAL);

		} else {

			title.setTypeface(null, Typeface.BOLD);
			subtitle.setTypeface(null, Typeface.BOLD);

		}

		// View topDivider = (View) row.findViewById(R.id.listTopDivider);
		// if (position == 0) {
		// topDivider.setVisibility(View.VISIBLE);
		// } else {
		// topDivider.setVisibility(View.INVISIBLE);
		// }

		return row;
	}

}

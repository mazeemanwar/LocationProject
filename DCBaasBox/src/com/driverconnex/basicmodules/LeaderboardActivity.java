package com.driverconnex.basicmodules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.driverconnex.adapter.BadgeAdapter;
import com.driverconnex.app.R;
import com.driverconnex.utilities.AssetsUtilities;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetDataCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Activity for displaying a leadboard. It displays a global and friends ranking. It also displays user photo and all badges that they have.
 * 
 * NOTE:
 * Since Greg did some changes with the App ranking no longer works. The Cloud code returns this message:
 * 
 * "This user is not allowed to perform the find operation on _User. You can change this setting in the Data Browser
 * 
 * @author Adrian Klimczak
 * 
 */

public class LeaderboardActivity extends Activity 
{	
	private TextView globalRanking;
	private TextView friendsRanking;
	private ImageView userPhoto;
	
	private GridView grid;
	private BadgeAdapter adapter;
	private String[][] badge;
	private ArrayList<Bitmap> icons = new ArrayList<Bitmap>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leaderboard);

		globalRanking = (TextView) findViewById(R.id.globalRankingText);
		friendsRanking = (TextView) findViewById(R.id.friendsRankingText);
		grid = (GridView) findViewById(R.id.grid);
		userPhoto = (ImageView) findViewById(R.id.photoImage);
		
		grid.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) 
			{
				Intent intent = new Intent(LeaderboardActivity.this, BadgeActivity.class);
				intent.putExtra("badge", badge[position]);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in, R.anim.null_anim);	
			}
		});
		
		// Get user photo
		//---------------------------
		ParseFile photo = (ParseFile) ParseUser.getCurrentUser().get("userProfilePhoto");
		
		if(photo != null)
		{
			photo.getDataInBackground(new GetDataCallback()
			{
				@Override
				public void done(byte[] data, ParseException e) 
				{
					if(e == null)
					{
						Bitmap bmp = AssetsUtilities.readBitmap(data);
						
						if(bmp != null)
							userPhoto.setImageBitmap(bmp);
					}
					else
						Log.e("Get user photo", e.getMessage());
				}
			});
		}
		
		globalRanking.setVisibility(View.INVISIBLE);
		friendsRanking.setVisibility(View.INVISIBLE);
		
		// Get user ranking
		getRankingByParse();
		
		// Query for badges
		getBadgesByParse();
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
		
		return super.onOptionsItemSelected(item);
	}
	
	private void getRankingByParse()
	{
		HashMap<String, Object> params = new HashMap<String, Object>();
		ParseCloud.callFunctionInBackground("calculateGlobalRanking", params, new FunctionCallback<Number>()
		{
			@Override
			public void done(Number ranking, ParseException e) 
			{
				if(e == null)
				{
					String rankingStr = convertRankingToString((int)Float.parseFloat(ranking.toString()));
					globalRanking.setText(rankingStr + " Globally");
					globalRanking.setVisibility(View.VISIBLE);
				}
				else
					Log.e("Get global ranking", e.getMessage());
			}
		});
		
		ParseCloud.callFunctionInBackground("calculateFriendsRanking", params, new FunctionCallback<Number>()
		{
			@Override
			public void done(Number ranking, ParseException e) 
			{
				if(e == null)
				{
					String rankingStr = convertRankingToString((int)Float.parseFloat(ranking.toString()));
					friendsRanking.setText(rankingStr + " in Friends");
					friendsRanking.setVisibility(View.VISIBLE);
				}
				else
					Log.e("Get friends ranking", e.getMessage());
			}
		});
	}
	
	private void getBadgesByParse()
	{
		ParseQuery<ParseObject> query = ParseUser.getCurrentUser().getRelation("userBadges").getQuery();
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
		query.findInBackground(new FindCallback<ParseObject>()
		{
			@Override
			public void done(List<ParseObject> objects, ParseException e) 
			{
				if(e == null)
				{
					badge = new String[objects.size()][2];
					
					for(int i=0; i<objects.size(); i++)
					{
						badge[i][0] = objects.get(i).getString("badgeTitle");
						badge[i][1] = objects.get(i).getString("badgeDescription");
						
						// Get photo from the parse
						ParseFile photo = (ParseFile) objects.get(i).get("badgeImage");
						byte[] data = null;
						
						try 
						{
							if (photo != null) 
								data = photo.getData();
						} 
						catch (ParseException e1) {
							e1.printStackTrace();
						}

						Bitmap bmp = AssetsUtilities.readBitmap(data);
						
						if(bmp != null)
							icons.add(bmp);
					}
					
					adapter = new BadgeAdapter(LeaderboardActivity.this,icons, badge);
					grid.setAdapter(adapter);
				}
				else
					Log.e("get DCBadge", e.getMessage());
			}
		});
	}
	
	/**
	 * Converts position in the ranking in form of integer into string form with prefix
	 * @param position
	 * @return
	 */
	private String convertRankingToString(int position)
	{
		String positionStr = "" + position;
		
		if(positionStr.charAt(positionStr.length()-1) == '1')
			positionStr += "st";
		else if(positionStr.charAt(positionStr.length()-1) == '2')
			positionStr += "nd";
		else if(positionStr.charAt(positionStr.length()-1) == '3')
			positionStr += "rd";
		else
			positionStr += "th";
		
		return positionStr;
	}
}

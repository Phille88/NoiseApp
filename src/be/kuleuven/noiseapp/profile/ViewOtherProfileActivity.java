package be.kuleuven.noiseapp.profile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import be.kuleuven.noiseapp.R;
import be.kuleuven.noiseapp.tools.BitmapScaler;
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.MemoryFileNames;
import be.kuleuven.noiseapp.tools.UserDetails;

public class ViewOtherProfileActivity extends Activity {
	
	SharedPreferences sp;
	UserDetails userDetails;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_other_profile);
		setupActionBar();
		
		initializeUserDetails();
	}

	private void initializeUserDetails() {
		userDetails = (UserDetails) getIntent().getSerializableExtra(MemoryFileNames.OTHER_PROFILE_DETAILS);
		
		TextView txt_userName = (TextView) findViewById(R.id.txt_other_username);
		txt_userName.setText(userDetails.getObfuscatedFullName());
		
		TextView txt_totalPoints = (TextView) findViewById(R.id.txt_other_points_earned_username);
		txt_totalPoints.setText(Long.toString(userDetails.getTotalPoints()));
		
		ImageView img_profilePicture = (ImageView) findViewById(R.id.img_other_profile_picture);
		Bitmap bm = null;
		try {
			FileInputStream fis = openFileInput(MemoryFileNames.PROFILE_PICTURE + "_" + userDetails.getUserID());
			bm = BitmapFactory.decodeStream(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (bm != null) {
			img_profilePicture.setImageBitmap(BitmapScaler.scaleCenterCrop(bm, Constants.PROFILEPICTUREHEIGHT, Constants.PROFILEPICTUREWIDTH));
			img_profilePicture.setMaxHeight(Constants.PROFILEPICTUREHEIGHT);
			img_profilePicture.setMaxWidth(Constants.PROFILEPICTUREWIDTH);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.view_other_profile, menu);
		return true;
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setTitle(R.string.txt_view_other_profile_name);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			Intent homeIntent = new Intent(this, ViewProfileTabActivity.class);
			homeIntent.putExtra(MemoryFileNames.COMINGFROMOTHERPROFILE, true);
			NavUtils.navigateUpTo(this, homeIntent);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}

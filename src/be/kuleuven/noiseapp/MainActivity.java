package be.kuleuven.noiseapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import be.kuleuven.noiseapp.auth.GetInfoInForeground;
import be.kuleuven.noiseapp.noisehunt.GetNoiseHuntStateTask;
import be.kuleuven.noiseapp.noisehunt.NoiseHuntActivity;

import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends Activity {
    private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
	private static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1001;
    private static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;
    private SharedPreferences sp;
    private TextView txt_userName;
    
	public MainActivity() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		
		getAccountInfo();
		getNoiseHuntState();
		
		ImageButton btn_RandomRecord = (ImageButton) findViewById(R.id.btn_random_record);
		btn_RandomRecord.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// onPause();
				Intent i = new Intent(getApplicationContext(),
						RandomRecordActivity.class);
//				Intent i = new Intent(getApplicationContext(),
//						TestRecordActivity.class);
				startActivity(i);
			}
		});

		ImageButton btn_soundBattle = (ImageButton) findViewById(R.id.btn_sound_battle);
		btn_soundBattle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// onPause();
				Intent i = new Intent(getApplicationContext(),
						SoundBattleActivity.class);
				startActivity(i);
			}
		});
		
		ImageButton btn_soundCheckin = (ImageButton) findViewById(R.id.btn_sound_checkin);
		btn_soundCheckin.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), SoundCheckinActivity.class);
				startActivity(i);
			}
		});

		ImageButton btn_noiseHunt = (ImageButton) findViewById(R.id.btn_noise_hunt);
		btn_noiseHunt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// onPause();
				Intent i = new Intent(getApplicationContext(),
						NoiseHuntActivity.class);
				startActivity(i);
			}
		});

		ImageButton btn_viewProfile = (ImageButton) findViewById(R.id.btn_view_profile);
		btn_viewProfile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// onPause();
				Intent i = new Intent(getApplicationContext(),
						ViewProfileTabActivity.class);
				startActivity(i);
			}
		});
		
		ImageButton btn_showMap = (ImageButton) findViewById(R.id.btn_show_map);
		btn_showMap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// onPause();
				Intent i = new Intent(getApplicationContext(),
						ShowMapActivity.class);
				startActivity(i);
			}
		});
	}

	private void getNoiseHuntState() {
		new GetNoiseHuntStateTask(this).execute();
	}

	private void getAccountInfo() {
		//TODO Check for every record, otherwise sync!
		String firstName = sp.getString("firstName", null);
		String lastName = sp.getString("lastName", null);
		String googleID = sp.getString("googleID", null);
		String email = sp.getString("email", null);
		String pictureURL = sp.getString("pictureURL", null);
		long id = sp.getLong("userID", 0L);
		if(firstName == null || lastName == null || googleID == null || email == null || pictureURL == null || id == 0){
			//Account Manager for Google Login
			AccountManager am = AccountManager.get(this); // "this" references the current Context
			Account[] accounts = am.getAccountsByType("com.google");
	
			new GetInfoInForeground(MainActivity.this, accounts[0].name, SCOPE, REQUEST_CODE_RECOVER_FROM_AUTH_ERROR)
	        .execute();
		}
		else {
			TextView txt_userName = (TextView) this.findViewById(R.id.txt_userName);
			txt_userName.setText("Hello, " + firstName);
		}
	}

	@TargetApi(14)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
//		super.onCreateOptionsMenu(menu);
//		MenuItem profile = menu.add(0, 1, 0, "Profile"); // groupID,itemID,order,title
//		profile.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//		profile.setIcon(R.drawable.social_person);
//		MenuItem map = menu.add(0, 2, 1, "Map");
//		map.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//		map.setIcon(R.drawable.location_map);
		return true;
	}

	public void showFirstName(final String message) {
		runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	txt_userName = (TextView) findViewById(R.id.txt_userName);
            	txt_userName.setText("Hello, " + message + "!");
            }
        });
	}
	
    /**
     * This method is a hook for background threads and async tasks that need to launch a dialog.
     * It does this by launching a runnable under the UI thread.
     */
    public void showErrorDialog(final int code) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
              Dialog d = GooglePlayServicesUtil.getErrorDialog(
                  code,
                  MainActivity.this,
                  REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
              d.show();
            }
        });
    }
}

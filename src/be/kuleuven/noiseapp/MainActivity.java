package be.kuleuven.noiseapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainActivity extends Activity {
	public MainActivity() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ImageButton btn_RandomRecord = (ImageButton) findViewById(R.id.btn_random_record);
		btn_RandomRecord.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// onPause();
				Intent i = new Intent(getApplicationContext(),
						RandomRecordActivity.class);
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
}

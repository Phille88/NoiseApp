package be.kuleuven.noiseapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	public MainActivity() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button btn_RandomRecord = (Button) findViewById(R.id.btn_random_record);
		Button btn_soundBattle = (Button) findViewById(R.id.btn_sound_battle);

		btn_RandomRecord.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// onPause();
				Intent i = new Intent(getApplicationContext(),
						RandomRecordActivity.class);
				startActivity(i);
			}
		});

		btn_soundBattle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// onPause();
				Intent i = new Intent(getApplicationContext(),
						SoundBattleActivity.class);
				startActivity(i);
			}
		});
	}

	@TargetApi(14)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// MenuInflater inflater = getMenuInflater(); //Te gebruiken voor
		// settings! wordt opgeroepen door de menu button (de drie puntjes)
		// inflater.inflate(R.menu.activity_main, menu);
		super.onCreateOptionsMenu(menu);
		MenuItem profile = menu.add(0, 1, 0, "Profile"); // groupID,itemID,order,title
		profile.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		profile.setIcon(R.drawable.social_person);
		MenuItem map = menu.add(0, 2, 1, "Map");
		map.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		map.setIcon(R.drawable.location_map);
		return true;
	}
}

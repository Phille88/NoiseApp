package com.example.noiseapp;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class RandomRecordActivity extends Activity {

	@TargetApi(14)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_random_record);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.title_activity_random_record);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intentHome = new Intent(this, MainActivity.class);
		switch (item.getItemId()) {

		case android.R.id.home:
			// app icon in action bar clicked; go home
			this.finish();
			startActivity(intentHome);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}

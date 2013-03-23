package be.kuleuven.noiseapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class NoiseHuntActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_noise_hunt);
		setupActionBar();
		
		/**
		 * Walk in the Park
		 */
		Button btn_walkinthepark = (Button) findViewById(R.id.btn_walkinthepark);
		if(!((NoiseHuntState) this.getApplication()).isWalkInTheParkDone()){
			btn_walkinthepark.setBackgroundResource(R.drawable.img_btn_hunt_active);
			btn_walkinthepark.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(getApplicationContext(), WalkInTheParkActivity.class);
					startActivity(intent);
				}
			});
		}
		else
			btn_walkinthepark.setBackgroundResource(R.drawable.img_btn_hunt_done);
		
		/**
		 * BLITZKRIEG
		 */
		Button btn_blitzkrieg = (Button) findViewById(R.id.btn_blitzkrieg);
		if(!((NoiseHuntState) this.getApplication()).isWalkInTheParkDone())
			btn_blitzkrieg.setBackgroundResource(R.drawable.img_btn_hunt_inactive);
		else if(((NoiseHuntState) this.getApplication()).isWalkInTheParkDone() && !((NoiseHuntState) this.getApplication()).isBlitzkriegDone()){
			btn_blitzkrieg.setBackgroundResource(R.drawable.img_btn_hunt_active);
			btn_blitzkrieg.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				
			}
		});
		}
		else 
			btn_walkinthepark.setBackgroundResource(R.drawable.img_btn_hunt_done);
		
		/**
		 * Party Time
		 */
		Button btn_partytime = (Button) findViewById(R.id.btn_partytime);
		if(!((NoiseHuntState) this.getApplication()).isBlitzkriegDone())
			btn_partytime.setBackgroundResource(R.drawable.img_btn_hunt_inactive);
		else if(((NoiseHuntState) this.getApplication()).isBlitzkriegDone() && !((NoiseHuntState) this.getApplication()).isPartyTimeDone()){
			btn_partytime.setBackgroundResource(R.drawable.img_btn_hunt_active);
			btn_partytime.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
				}
			});
		}
		else
			btn_partytime.setBackgroundResource(R.drawable.img_btn_hunt_done);
		
		/**
		 * Riverside
		 */
		Button btn_riverside = (Button) findViewById(R.id.btn_riverside);
		if(!((NoiseHuntState) this.getApplication()).isPartyTimeDone())
			btn_riverside.setBackgroundResource(R.drawable.img_btn_hunt_inactive);
		else if(((NoiseHuntState) this.getApplication()).isPartyTimeDone() && !((NoiseHuntState) this.getApplication()).isRiversideDone()){
			btn_riverside.setBackgroundResource(R.drawable.img_btn_hunt_active);
			btn_riverside.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
				}
			});
		}
		else
			btn_riverside.setBackgroundResource(R.drawable.img_btn_hunt_done);
		
		/**
		 * Trainspotting
		 */
		Button btn_trainspotting = (Button) findViewById(R.id.btn_trainspotting);
		if(!((NoiseHuntState) this.getApplication()).isRiversideDone())
			btn_trainspotting.setBackgroundResource(R.drawable.img_btn_hunt_inactive);
		else if(((NoiseHuntState) this.getApplication()).isRiversideDone() && !((NoiseHuntState) this.getApplication()).isTrainspottingDone()){
			btn_trainspotting.setBackgroundResource(R.drawable.img_btn_hunt_active);
			btn_trainspotting.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
				}
			});
		}
		else
			btn_trainspotting.setBackgroundResource(R.drawable.img_btn_hunt_done);
		
		/**
		 * Morning Glory
		 */
		Button btn_morningglory = (Button) findViewById(R.id.btn_morningglory);
		if(!((NoiseHuntState) this.getApplication()).isRiversideDone())
			btn_morningglory.setBackgroundResource(R.drawable.img_btn_hunt_inactive);
		else if(((NoiseHuntState) this.getApplication()).isRiversideDone() && !((NoiseHuntState) this.getApplication()).isTrainspottingDone()){
			btn_morningglory.setBackgroundResource(R.drawable.img_btn_hunt_active);
			btn_morningglory.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
				}
			});
		}
		else
			btn_morningglory.setBackgroundResource(R.drawable.img_btn_hunt_done);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setTitle(R.string.title_activity_noise_hunt);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.noise_hunt, menu);
		return true;
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
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}

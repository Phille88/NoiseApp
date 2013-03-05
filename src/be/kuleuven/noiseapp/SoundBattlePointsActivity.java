package be.kuleuven.noiseapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.PopupWindow;

public class SoundBattlePointsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sound_battle_points);
		// Show the Up button in the action bar.
		setupActionBar();
		showPopup();
	}
	
	private void showPopup(){
//		final Button btn_sound_battle_ok = (Button) findViewById(R.id.btn_sound_battle_ok);
//		btn_sound_battle_ok.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
		LayoutInflater layoutInflater  = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);  
	    View popupView = layoutInflater.inflate(R.layout.popup_sound_battle_badge, null);  
	    
	    final PopupWindow popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);  
	    
	    ImageButton btnDismiss = (ImageButton)popupView.findViewById(R.id.btn_popup_sound_battle_points_ok);
	    btnDismiss.setOnClickListener(new ImageButton.OnClickListener(){
	     @Override
	     public void onClick(View v) {
	    	 popupWindow.dismiss();
	     }
	     });
	    DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
	    popupWindow.setHeight(metrics.heightPixels);
	    popupWindow.setWidth(metrics.widthPixels);
	    findViewById(R.id.layout_sound_battle_points).post(new Runnable() {
	    	   public void run() {
	    		    popupWindow.showAtLocation(findViewById(R.id.layout_sound_battle_points), Gravity.CENTER_HORIZONTAL, 0, 0);
	    	   }
	    	});
//			   }});
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setTitle(R.string.title_activity_sound_battle);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.sound_battle_points, menu);
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
//			NavUtils.navigateUpFromSameTask(this);
			Intent homeIntent = new Intent(this, MainActivity.class);
			NavUtils.navigateUpTo(this, homeIntent);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}

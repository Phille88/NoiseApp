package be.kuleuven.noiseapp;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import be.kuleuven.noiseapp.exception.NotInLeuvenException;
import be.kuleuven.noiseapp.location.NoiseLocation;
import be.kuleuven.noiseapp.location.SoundBattleLocationGenerator;
import be.kuleuven.noiseapp.location.SoundBattleLocation;

public class SoundBattleRecordActivity extends RecordActivity {
	
	private ArrayList<SoundBattleLocation> SBLocations = new ArrayList<SoundBattleLocation>();
	private static final double MINIMUM_DISTANCE_SBL = 5; //metres
	
	private boolean battleLocationsInitialized;

	@Override
	protected void showPopup() {
		LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View popupView = layoutInflater.inflate(
				R.layout.popup_explanation, null);

		final PopupWindow popupWindow = new PopupWindow(popupView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		TextView txt_title = (TextView) popupView.findViewById(R.id.txt_explanation_title);
		txt_title.setText(R.string.txt_sound_battle_name);
		TextView txt_desc = (TextView) popupView.findViewById(R.id.txt_explanation);
		txt_desc.setText(R.string.txt_sound_battle_explanation);

		ImageButton btnDismiss = (ImageButton) popupView.findViewById(R.id.btn_popup_explanation_ok);
		btnDismiss.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
			}
		});
		DisplayMetrics metrics = getApplicationContext().getResources()
				.getDisplayMetrics();
		popupWindow.setHeight(metrics.heightPixels);
		popupWindow.setWidth(metrics.widthPixels);
		findViewById(R.id.layout_map_record).post(new Runnable() {
			@Override
			public void run() {
				popupWindow.showAtLocation(
						findViewById(R.id.layout_map_record),
						Gravity.CENTER_HORIZONTAL, 0, 0);
			}
		});
	}


	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void setActionBarTitle() {
			getActionBar().setTitle(R.string.txt_sound_battle_name);
	}
	
	@Override
	protected OnClickListener recordButtonListener(){
		return new OnClickListener(){
			private Thread t;

			@Override
			public void onClick(View v) {
					if (!isProviderFixed())
						Toast.makeText(getApplicationContext(),
								"Wait for the GPS to have a fixed location",
								Toast.LENGTH_LONG).show();
					else if (!getClosestNoiseLocationToRecord().isClose(
							currentLocation))
						Toast.makeText(
								getApplicationContext(),
								"You have to get closer to a Sound Battle Location",
								Toast.LENGTH_LONG).show();
					else {

						// prepare for a progress bar dialog
						progressBar = new ProgressDialog(v.getContext()) {
							@Override
							public void onBackPressed() {
								this.dismiss();
								t.interrupt();
								t = null;
								return;
							}
							
							@Override
							public boolean onTouchEvent(MotionEvent e){
								this.dismiss();
								t.interrupt();
								t = null;
								return true;
							}
						};
						progressBar.setCancelable(true);
						progressBar.setMessage("Recording...");
						progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
						progressBar.setProgress(0);
						progressBar.setMax(100);
						progressBar.show();

						// reset progress bar status
						progressBarStatus = 0;

						// create the progress dialog thread
						t = new Thread(new Runnable() {

							@Override
							public void run() {
								Thread thisThread = Thread.currentThread();
								while (progressBarStatus < 100
										&& thisThread == t) {
									// comp is too fast, sleep 1 second
									try {
										Thread.sleep(1000);

									} catch (InterruptedException e) {
										e.printStackTrace();
										progressBarStatus = 0;
									}
									// process some tasks
									progressBarStatus = doSomeTasks();

									

									// Update the progress bar
									progressBarHandler.post(new Runnable() {
										@Override
										public void run() {
											progressBar
													.setProgress(progressBarStatus);
										}
									});
								}

								// ok, task is done
								if (progressBarStatus >= 100) {

									// sleep 2 seconds, so that you can see the
									// 100%
									try {
										Thread.sleep(2000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}

									getClosestNoiseLocationToRecord()
											.setRecorded(true);

									// close the progress bar dialog
									progressBar.dismiss();
									Thread.currentThread().interrupt();

									if (isEverythingRecorded()) {
										Intent i = new Intent(
												getApplicationContext(),
												SoundBattlePointsActivity.class);
										startActivity(i);
										finish();
									}
								}
							}
						});
						t.start();
					}
			}
		};
	}

	private void initializeBattleLocations() {
		SoundBattleLocationGenerator p = new SoundBattleLocationGenerator(
				getApplicationContext(), currentLocation);
		try {
			p.generate();
		} catch (NotInLeuvenException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(),
					"You are not near Leuven. Restart the application when you are there.",
					Toast.LENGTH_LONG).show();
			try {
				Thread.sleep(8000);//TODO testen in Antwerpen!
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			Intent homeIntent = new Intent(this, MainActivity.class);
			NavUtils.navigateUpTo(this, homeIntent);
			super.locationManager.removeUpdates(this);
			finish();
			return;
		}
		for (int i = 0; i < 3; i++) {
			SoundBattleLocation toAdd;
			
			do{
				toAdd = p.getRandomSoundBattleLocation(currentLocation);
			}
			while(contains(toAdd));
			SBLocations.add(toAdd);
		}
		updateMarkers();
	}
	
	private boolean contains(NoiseLocation other){
		for(NoiseLocation nl : SBLocations)
			if(nl.getDistance(other) <= MINIMUM_DISTANCE_SBL)
					return true;
		return false;
	}

	private void updateMarkers() {
		super.mMap.clear();
		for (SoundBattleLocation sbl : SBLocations) {
			super.mMap.addMarker(sbl.getMarker(currentLocation));
		}
	}

	/**
	 * Overridden methods of LocationListener
	 */
	@Override
	public void onLocationChanged(Location location) {
		if (isBetterLocation(location, currentLocation)) {
			setProviderFixed(true);
			currentLocation = location;
			if (!battleLocationsInitialized) {
				initializeBattleLocations();
				battleLocationsInitialized = true;
			}
			updateMarkers();
			if (timeout()) {
				zoomTo(currentLocation);
			}
		}
	}

	private SoundBattleLocation getClosestNoiseLocationToRecord() {
		double smallestDistance = Double.MAX_VALUE;
		SoundBattleLocation toReturn = null;
		for (SoundBattleLocation sbl : SBLocations)
			if (sbl.getDistance(currentLocation) < smallestDistance
					&& !sbl.isRecorded()) {
				smallestDistance = sbl.getDistance(currentLocation);
				toReturn = sbl;
			}
		return toReturn;
	}

	private boolean isEverythingRecorded() {
		for(SoundBattleLocation sbl : SBLocations)
			if(!sbl.isRecorded())
				return false;
		return true;
	}


	@Override
	protected boolean popupNeeded() {
		return true;
	}


	@Override
	protected int getActivityTitle() {
		return R.string.txt_sound_battle_name;
	}


	@Override
	protected int getPopupExplanation() {
		return R.string.txt_sound_battle_explanation;
	}

}

package be.kuleuven.noiseapp;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

import be.kuleuven.noiseapp.R;
import be.kuleuven.noiseapp.location.NoiseLocation;

import android.os.Bundle;
import android.app.ProgressDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class WalkInTheParkActivity extends RecordActivity {


	private static final double FIFTY_METERS = 50;
	private ArrayList<NoiseLocation> recordedLocations = new ArrayList<NoiseLocation>();
	private ArrayList<LatLng> parkBoundary = new ArrayList<LatLng>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initializeParkBoundary();
	}

	private void initializeParkBoundary() {
		parkBoundary.add(new LatLng(50.87574442047066,4.701322317123413));
		parkBoundary.add(new LatLng(50.875866279258275,4.7018373012542725));
		parkBoundary.add(new LatLng(50.875974597913086,4.702620506286621));
		parkBoundary.add(new LatLng(50.87603552704579,4.703006744384766));
		parkBoundary.add(new LatLng(50.87556840165932,4.703307151794434));
		parkBoundary.add(new LatLng(50.876793749588906,4.705849885940552));
		parkBoundary.add(new LatLng(50.87671928184973,4.705989360809326));
		parkBoundary.add(new LatLng(50.87640110016916,4.7056567668914795));
		parkBoundary.add(new LatLng(50.87537207220087,4.703328609466553));
		parkBoundary.add(new LatLng(50.875155431838586,4.703382253646851));
		parkBoundary.add(new LatLng(50.874755998530524,4.70190167427063));
		parkBoundary.add(new LatLng(50.87462736673647,4.701933860778809));
		parkBoundary.add(new LatLng(50.87458674609618,4.70139741897583));
		parkBoundary.add(new LatLng(50.87477630878132,4.701268672943115));
		parkBoundary.add(new LatLng(50.87488462996956,4.701719284057617));
		parkBoundary.add(new LatLng(50.87547362202403,4.701429605484009));
		parkBoundary.add(new LatLng(50.87532468220768,4.701300859451294));
		parkBoundary.add(new LatLng(50.87520282200387,4.701365232467651));
		parkBoundary.add(new LatLng(50.87574442047066,4.701322317123413));
	}

	@Override
	protected OnClickListener recordButtonListener() {
		return new OnClickListener(){
			private Thread t;

			@Override
			public void onClick(View v) {
					if (!isProviderFixed())
						Toast.makeText(getApplicationContext(),
								"Wait for the GPS to have a fixed location",
								Toast.LENGTH_SHORT).show();
					else if (!isInPark())
						Toast.makeText(
								getApplicationContext(),
								"You have to get into the city park!",
								Toast.LENGTH_SHORT).show();
					
					else if(!isFurtherThan50m()){
						Toast.makeText(
								getApplicationContext(),
								"You have to get further away from the last record location!",
								Toast.LENGTH_SHORT).show();
					}
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
						};
						progressBar.setCancelable(true);
						progressBar.setMessage("Recording...");
						progressBar
								.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
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

									recordedLocations.add(new NoiseLocation(currentLocation.getLongitude(),currentLocation.getLatitude()));

									// close the progress bar dialog
									progressBar.dismiss();
									Thread.currentThread().interrupt();

									if (isEverythingRecorded()) {
										setHuntDone();
										Intent i = new Intent(
												getApplicationContext(),
												WalkInTheParkPointsActivity.class);
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

	private boolean isEverythingRecorded() {
		return this.recordedLocations.size() == 3;
	}
	
	private boolean isInPark(){
      boolean result = false;
      for (int i = 0, j = parkBoundary.size() - 1; i < parkBoundary.size(); j = i++) {
        if ((parkBoundary.get(i).longitude > currentLocation.getLongitude()) != (parkBoundary.get(j).longitude > currentLocation.getLongitude()) &&
            (currentLocation.getLatitude() < (parkBoundary.get(j).latitude - parkBoundary.get(i).latitude) * (currentLocation.getLongitude() - parkBoundary.get(i).longitude) / (parkBoundary.get(j).longitude-parkBoundary.get(i).longitude) + parkBoundary.get(i).latitude)) {
          result = !result;
         }
      }
      return result;
	}
	
	private boolean isFurtherThan50m(){
		for(NoiseLocation nl : recordedLocations)
			if(nl.getDistance(currentLocation) <= FIFTY_METERS)
				return false;
		return true;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void setActionBarTitle() {
		getActionBar().setTitle(R.string.txt_noise_hunt_walkinthepark);
	}

	@Override
	protected boolean popupNeeded() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.txt_noise_hunt_walkinthepark;
	}

	@Override
	protected int getPopupExplanation() {
		return R.string.txt_desc_noise_hunt_walkinthepark;
	}
	
	private void setHuntDone(){
		((NoiseHuntState) this.getApplication()).setWalkInTheParkDone(true);
	}

	
}

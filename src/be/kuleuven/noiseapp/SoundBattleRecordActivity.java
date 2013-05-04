package be.kuleuven.noiseapp;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import be.kuleuven.noiseapp.exception.NotInLeuvenException;
import be.kuleuven.noiseapp.location.NoiseLocation;
import be.kuleuven.noiseapp.location.SoundBattleLocation;
import be.kuleuven.noiseapp.location.SoundBattleLocationGenerator;
import be.kuleuven.noiseapp.recording.SoundBattleRecordTask;

public class SoundBattleRecordActivity extends RecordActivity {
	
	private ArrayList<SoundBattleLocation> SBLocations = new ArrayList<SoundBattleLocation>();
	private static final double MINIMUM_DISTANCE_SBL = 5; //metres
	
	private boolean battleLocationsInitialized;

	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void setActionBarTitle() {
			getActionBar().setTitle(R.string.txt_sound_battle_name);
	}
	
	@Override
	protected OnClickListener recordButtonListener(){
		final SoundBattleRecordActivity s = this;
		return new OnClickListener(){

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
						SoundBattleRecordTask sbrt = new SoundBattleRecordTask(v,s);
						sbrt.execute(currentLocation);
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
					"You are not near Leuven. Restart the application when you are in Leuven.",
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

	public SoundBattleLocation getClosestNoiseLocationToRecord() {
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

	public boolean isEverythingRecorded() {
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
	
	@Override
	protected String getPopupDontShowAgainName(){
		return "SBR_DSA";
	}

}

package be.kuleuven.noiseapp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import be.kuleuven.noiseapp.exception.NotInLeuvenException;
import be.kuleuven.noiseapp.location.NoiseLocation;
import be.kuleuven.noiseapp.location.SoundBattleLocation;
import be.kuleuven.noiseapp.location.SoundBattleLocationGenerator;
import be.kuleuven.noiseapp.recording.SoundBattleRecordTask;
import be.kuleuven.noiseapp.soundbattle.ActiveSoundBattle;
import be.kuleuven.noiseapp.soundbattle.AddFriendTask;
import be.kuleuven.noiseapp.soundbattle.SaveSoundBattleLocations;
import be.kuleuven.noiseapp.tools.Constants;

public class SoundBattleRecordActivity extends RecordActivity {
	
	private ArrayList<SoundBattleLocation> SBLocations = new ArrayList<SoundBattleLocation>();
	private static final double MINIMUM_DISTANCE_SBL = 5; //metres
	private boolean battleLocationsInitialized;
	//private ActiveSoundBattle asb;
	private long SoundBattleID;
	private String opponentName;
	private SoundBattleRecordActivity sbra;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		sbra = this;
		
		setSoundBattleID(getIntent().getLongExtra("soundBattleID", 0L));
		setOpponentName(getIntent().getStringExtra("opponentFirstName"), getIntent().getStringExtra("opponentLastName"));
		
		showBoxAddFriend();
		if (getIntent().hasExtra("SBLlongitudes")){ //loaded SoundBattle
			loadSoundBattle();
		}
		
		Button btn_add_friend = (Button) findViewById(R.id.btn_add_friend);
		btn_add_friend.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				new AddFriendTask().execute();
			}});
	}

	/**
	 * @return the opponentName
	 */
	public String getOpponentName() {
		return opponentName;
	}
	
	private void setOpponentName(String firstName, String lastName) {
		this.opponentName = firstName + " " + lastName.charAt(0) + ".";
	}

	private void showBoxAddFriend() {
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout_map_record);
		View toAdd = getLayoutInflater().inflate(R.layout.box_add_friends,null);
		layout.addView(toAdd);
		TextView opponentName = (TextView) findViewById(R.id.txt_opponent_name);
		opponentName.setText(getOpponentName());
		
		ImageView img_opponentPicture = (ImageView) findViewById(R.id.img_opponent_profile_picture);
		Bitmap bm = null;
		try {
			FileInputStream fis = openFileInput(Constants.FILENAME_OPPONENT_PROFILE_PICTURE);
			bm = BitmapFactory.decodeStream(fis);
			fis.close();
		} 	
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
		catch (IOException e) {
			e.printStackTrace();
		}
		
		if(bm != null){
			img_opponentPicture.setImageBitmap(bm);
		}
	}

	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void setActionBarTitle() {
			getActionBar().setTitle(R.string.txt_sound_battle_name);
	}
	
	@Override
	protected OnClickListener recordButtonListener(){
		return new OnClickListener(){

			@Override
			public void onClick(View v) {
					if (!isProviderFixed())
						Toast.makeText(getApplicationContext(),
								"Wait for the GPS to have a fixed location",
								Toast.LENGTH_LONG).show();
					else if (!getClosestSoundBattleLocationToRecord().isClose(
							getCurrentLocation()))
						Toast.makeText(
								getApplicationContext(),
								"You have to get closer to a Sound Battle Location",
								Toast.LENGTH_LONG).show();
					else {
						new SoundBattleRecordTask(v,sbra).execute(getCurrentLocation());
					}
			}
		};
	}

	@SuppressWarnings("unchecked")
	private void initializeBattleLocations() {
		//new sound battle creation
			SoundBattleLocationGenerator p = new SoundBattleLocationGenerator(
					getApplicationContext(), getCurrentLocation());
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
					toAdd = p.getRandomSoundBattleLocation(getCurrentLocation());
				}
				while(contains(toAdd));
				SBLocations.add(toAdd);
			}
			new SaveSoundBattleLocations(getSoundBattleID()).execute(SBLocations);

//TODO if not in Leuven, uncomment following lines
//		else {
//			SBLocations.add(new SoundBattleLocation(4.39004, 51.20543)); //coordinaten antwerpen
//			SBLocations.add(new SoundBattleLocation(4.39017, 51.20545));
//			SBLocations.add(new SoundBattleLocation(4.39018, 51.20536));
//		}
		updateMarkers();
	}
	
	private void loadSoundBattle(){
		battleLocationsInitialized = true;
		int[] SBLIDs = getIntent().getIntArrayExtra("SBLIDs");
		double[] SBLlongitudes = getIntent().getDoubleArrayExtra("SBLlongitudes");
		double[] SBLlatitudes = getIntent().getDoubleArrayExtra("SBLlatitudes");
		boolean[] SBLrecorded = getIntent().getBooleanArrayExtra("SBLrecorded");
		setSoundBattleID(getIntent().getLongExtra("soundBattleID", 0L));
		for(int i = 0; i < SBLlongitudes.length; i++){
			SoundBattleLocation sbl = new SoundBattleLocation(SBLlongitudes[i], SBLlatitudes[i]);
			sbl.setRecorded(SBLrecorded[i]);
			sbl.setSoundBattleLocationID(SBLIDs[i]);
			SBLocations.add(sbl);
		}
		updateMarkers();
	}
	
	private boolean contains(NoiseLocation other){
		for(NoiseLocation nl : SBLocations)
			if(nl.getDistance(other) <= MINIMUM_DISTANCE_SBL)
					return true;
		return false;
	}

	public void updateMarkers() {
		super.mMap.clear();
		for (SoundBattleLocation sbl : SBLocations) {
			super.mMap.addMarker(sbl.getMarker(getCurrentLocation()));
		}
	}

	/**
	 * Overridden methods of LocationListener
	 */
	@Override
	public void onLocationChanged(Location location) {
		if (isBetterLocation(location, getCurrentLocation())) {
			setProviderFixed(true);
			setCurrentLocation(location);
			if (!battleLocationsInitialized) {
				initializeBattleLocations();
				battleLocationsInitialized = true;
			}
			updateMarkers();
			if (timeout()) {
				zoomTo(getCurrentLocation());
			}
		}
	}

	public SoundBattleLocation getClosestSoundBattleLocationToRecord() {
		double smallestDistance = Double.MAX_VALUE;
		SoundBattleLocation toReturn = null;
		for (SoundBattleLocation sbl : SBLocations)
			if (sbl.getDistance(getCurrentLocation()) < smallestDistance
					&& !sbl.isRecorded()) {
				smallestDistance = sbl.getDistance(getCurrentLocation());
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

	private void setSoundBattleID(long soundBattleID) {
		SoundBattleID = soundBattleID;
	}

	public long getSoundBattleID() {
		return SoundBattleID;
	}

}

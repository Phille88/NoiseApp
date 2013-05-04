package be.kuleuven.noiseapp.points;

import java.util.ArrayList;

import android.os.AsyncTask;
import be.kuleuven.noiseapp.noisedatabase.NoiseRecording;

public class CalculateRecordingPoints extends AsyncTask<NoiseRecording, Void, RecordingPoints>{
	
	private ArrayList<Point> points = new ArrayList<Point>();
	private ArrayList<Badge> badges = new ArrayList<Badge>();
	private NoiseRecording nr = null;


	@Override
	protected RecordingPoints doInBackground(NoiseRecording... nrs) {
		RecordingPoints toReturn = new RecordingPoints();
		nr = nrs[0];
		calculatePoints();
		calculateBadges();
		if(!points.isEmpty())
			toReturn.setPoints(points);
		if(!badges.isEmpty())
			toReturn.setBadges(badges);
		return toReturn;
	}
	
	private void calculatePoints() {
		createRecordingPoint();
		if(isFirstTimeRadius())
			createFirstTimeRadiusPoint();
	}

	private boolean isFirstTimeRadius() {
		//TODO do some checking on the radius
		if(nr.getLatitude() == 0)
			return false; //dummy code!
		return true;
	}

	private void createRecordingPoint(){
		points.add(new Point("You did a recording.",2));
	}

	private void createFirstTimeRadiusPoint() {
		points.add(new Point("It's the first time you record in this area!", 5));
	}
	
	private void calculateBadges() {
		if(isNewbie())
			createNewbieBadge();
		if(isFirstSoundBattle())
			createFirstSoundBattleBadge();
	}

	private boolean isNewbie() {
		// TODO do newbie logic
		return true;
	}

	private void createNewbieBadge() {
		badges.add(new Badge("Newbie", "This is your first recording!", 25));		
	}

	private boolean isFirstSoundBattle() {
		//TODO do some logic
		return false;
	}
	
	private void createFirstSoundBattleBadge() {
		badges.add(new Badge("First Sound Battle", "This is your first sound battle!", 50));	
	}

	private ArrayList<NoiseRecording> getNoiseRecordingsInRadius(double radius){
		//TODO mysql request to return close noise recordings
		ArrayList<NoiseRecording> nrs = new ArrayList<NoiseRecording>();
		return nrs;
	}

}

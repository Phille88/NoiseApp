package be.kuleuven.noiseapp.points;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import be.kuleuven.noiseapp.noisedatabase.NoiseRecording;
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.JSONParser;
import be.kuleuven.noiseapp.tools.JSONTags;
import be.kuleuven.noiseapp.tools.MySQLTags;

public class CalculateRandomRecordPoints extends AsyncTask<NoiseRecording, Void, RecordingPoints>{
	
	private JSONParser jsonParser = new JSONParser();
	private static String url_get_random_record_points = Constants.BASE_URL_MYSQL + "get_randomrecordpoints.php";
	
	private ArrayList<Point> points = new ArrayList<Point>();
	private ArrayList<Badge> badges = new ArrayList<Badge>();
	private NoiseRecording nr = null;


	@Override
	protected RecordingPoints doInBackground(NoiseRecording... nrs) {
		NoiseRecording nr = nrs[0];
    	
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(MySQLTags.USERID, Long.toString(nr.getUserID())));
        params.add(new BasicNameValuePair(MySQLTags.LATITUDE, Double.toString(nr.getLatitude())));
        params.add(new BasicNameValuePair(MySQLTags.LONGITUDE, Double.toString(nr.getLongitude())));
        params.add(new BasicNameValuePair(MySQLTags.DB,Double.toString(nr.getDB())));
        params.add(new BasicNameValuePair(MySQLTags.ACCURACY,Double.toString(nr.getAccuracy())));
        params.add(new BasicNameValuePair(MySQLTags.QUALITY,Double.toString(nr.getQuality())));

        JSONObject json = jsonParser.makeHttpRequest(url_get_random_record_points, "POST", params);

        Log.d("CalculateRandomRecordPoints Response", json.toString());

        try {
            int success = json.getInt(JSONTags.SUCCESS);

            if (success == 1) {
            	JSONArray points = json.getJSONArray(JSONTags.POINTS);
            	ArrayList<Point> pointslist = new ArrayList<Point>();
            	for (int i = 0; i < points.length(); i++){
            		JSONObject point = points.getJSONObject(i);
            		pointslist.add(new Point(point.getString(JSONTags.POINT_DESC), point.getInt(JSONTags.POINT_AMOUNT)));
            		//TODO new badge
            	}
            	return new RecordingPoints(pointslist, new ArrayList<Badge>());
            } else {
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
		
		
//		RecordingPoints toReturn = new RecordingPoints();
//		nr = nrs[0];
//		calculatePoints();
//		calculateBadges();
//		if(!points.isEmpty())
//			toReturn.setPoints(points);
//		if(!badges.isEmpty())
//			toReturn.setBadges(badges);
//		return toReturn;
	}
	
//	private void calculatePoints() {
//		createRecordingPoint();
//		if(isFirstTimeRadius())
//			createFirstTimeRadiusPoint();
//	}
//
//	private boolean isFirstTimeRadius() {
//		//TODO do some checking on the radius
//		if(nr.getLatitude() == 0)
//			return false; //dummy code!
//		return true;
//	}
//
//	private void createRecordingPoint(){
//		points.add(new Point("You did a recording.",2));
//	}
//
//	private void createFirstTimeRadiusPoint() {
//		points.add(new Point("It's the first time you record in this area!", 5));
//	}
//	
//	private void calculateBadges() {
//		if(isNewbie())
//			createNewbieBadge();
//		if(isFirstSoundBattle())
//			createFirstSoundBattleBadge();
//	}
//
//	private boolean isNewbie() {
//		// TODO do newbie logic
//		return true;
//	}
//
//	private void createNewbieBadge() {
//		badges.add(new Badge("Newbie", "This is your first recording!", 25));		
//	}
//
//	private boolean isFirstSoundBattle() {
//		//TODO do some logic
//		return false;
//	}
//	
//	private void createFirstSoundBattleBadge() {
//		badges.add(new Badge("First Sound Battle", "This is your first sound battle!", 50));	
//	}
//
//	private ArrayList<NoiseRecording> getNoiseRecordingsInRadius(double radius){
//		//TODO mysql request to return close noise recordings
//		ArrayList<NoiseRecording> nrs = new ArrayList<NoiseRecording>();
//		return nrs;
//	}
}

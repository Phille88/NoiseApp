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
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.JSONParser;
import be.kuleuven.noiseapp.tools.JSONTags;
import be.kuleuven.noiseapp.tools.MySQLTags;

public class CalculateNoiseHuntPoints extends AsyncTask<Long, Void, RecordingPoints>{
	
	private JSONParser jsonParser = new JSONParser();
	private static String url_get_noise_hunt_points = Constants.BASE_URL_MYSQL + "get_noisehuntpoints.php";


	@Override
	protected RecordingPoints doInBackground(Long... args) {
    	//TODO delete debugger
    	android.os.Debug.waitForDebugger();
		long userID = args[1];
		long noiseHuntID = args[0];
    	
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(MySQLTags.USERID, Long.toString(userID)));
        params.add(new BasicNameValuePair(MySQLTags.NOISEHUNT_ID, Long.toString(noiseHuntID)));
        JSONObject json = jsonParser.makeHttpRequest(url_get_noise_hunt_points, "POST", params);

        Log.d("CalculateNoiseHuntPoints Response", json.toString());

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
}
}

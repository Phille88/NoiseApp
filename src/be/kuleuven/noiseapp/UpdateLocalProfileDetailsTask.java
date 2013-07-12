package be.kuleuven.noiseapp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.ImageDownloaderTask;
import be.kuleuven.noiseapp.tools.JSONParser;
import be.kuleuven.noiseapp.tools.JSONTags;
import be.kuleuven.noiseapp.tools.MemoryFileNames;
import be.kuleuven.noiseapp.tools.MySQLTags;
import be.kuleuven.noiseapp.tools.ObjectSerializer;
import be.kuleuven.noiseapp.tools.UserDetails;

public class UpdateLocalProfileDetailsTask extends AsyncTask<Void, Void, JSONObject> {

	private static final String url_get_userprofile_details = Constants.BASE_URL_MYSQL + "get_userprofile_details.php";
	private JSONParser jsonParser = new JSONParser();
	private SharedPreferences sp;
	private long userID;
	private Activity rActivity;
	
	public UpdateLocalProfileDetailsTask(Activity rActivity){
		sp = PreferenceManager.getDefaultSharedPreferences(rActivity);
		userID = sp.getLong(MemoryFileNames.USERID, 0L);
		this.rActivity = rActivity;
	}
	
	@Override
	protected JSONObject doInBackground(Void... arg0) {
    	//TODO delete debugger
    	//android.os.Debug.waitForDebugger();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair(MySQLTags.USERID, Long.toString(userID)));
	    
	    JSONObject json = jsonParser.makeHttpRequest(url_get_userprofile_details, "POST", params);
        
        Log.d("UpdateLocalProfileDetailsResponse", json.toString());
        
        try {
            int success = json.getInt(JSONTags.SUCCESS);

            if (success == 1) {
                return json.getJSONObject(JSONTags.USERPROFILE);
            } else {
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
	}
	
	@Override
	protected void onPostExecute(JSONObject userDetailsJSON){
		long userID = 0L;
		UserDetails userDetails = null;
		try {
			userID = userDetailsJSON.getLong(JSONTags.USERID);
			BigInteger googleID = new BigInteger(userDetailsJSON.getString(JSONTags.GOOGLEID));
			String fName = userDetailsJSON.getString(JSONTags.FIRSTNAME);
			String lName = userDetailsJSON.getString(JSONTags.LASTNAME);
			String email = userDetailsJSON.getString(JSONTags.EMAIL);
			long totalPoints = userDetailsJSON.getLong(JSONTags.TOTALPOINTS);
			String pictureURL = userDetailsJSON.getString(JSONTags.PICTUREURL);
			userDetails = new UserDetails(userID,googleID, fName, lName, email, totalPoints, pictureURL);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Editor edit = sp.edit();
		edit.putLong(MemoryFileNames.USERID, userID);
		edit.putString(MemoryFileNames.USERDETAILS, ObjectSerializer.serialize(userDetails));
		edit.commit();

		//TODO size bij de foto er terug bijzetten!
		new ImageDownloaderTask(rActivity, MemoryFileNames.PROFILE_PICTURE).execute(userDetails.getPictureURL());
	}

}

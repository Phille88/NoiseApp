package be.kuleuven.noiseapp.auth;

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
import be.kuleuven.noiseapp.tools.JSONParser;

public class CreateUserProfile extends AsyncTask<String, String, Long> {
	
	private BigInteger googleID;
	private String firstName;
	private String lastName;
	private String email;
	private JSONParser jsonParser = new JSONParser();
	private String pictureURL;
	private Activity sourceActivity;
	private static String url_create_userprofile = Constants.BASE_URL_MYSQL + "create_userprofile.php";
    private static final String TAG_SUCCESS = "success";
	private static final String TAG_USERID = "userID";
	
	public CreateUserProfile(BigInteger googleID, String firstName, String lastName, String email, String pictureURL, Activity sourceActivity){
		this.googleID = googleID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.pictureURL = pictureURL;
		this.sourceActivity = sourceActivity;
	}
	/**
     * Before starting background thread Show Progress Dialog
     * */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /**
     * Creating profile
     * */
    protected Long doInBackground(String... args) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("googleID", googleID.toString()));
        params.add(new BasicNameValuePair("firstName", firstName));
        params.add(new BasicNameValuePair("lastName", lastName));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("pictureURL", pictureURL));

        // getting JSON Object
        // Note that create product url accepts POST method
        JSONObject json = jsonParser.makeHttpRequest(url_create_userprofile,
                "POST", params);

        // check log cat fro response
        Log.d("CreateUserProfile Response", json.toString());

        // check for success tag
        try {
            int success = json.getInt(TAG_SUCCESS);

            if (success == 1) {
            	return json.getLong(TAG_USERID);
            } else {
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0L;
    }

    @Override
    protected void onPostExecute(Long result) {
    	if(result != 0){
    		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(sourceActivity.getApplicationContext());
    		Editor edit = sp.edit();
    		edit.putLong("userID",result);
    		edit.commit();
    	}
    		
    }

}


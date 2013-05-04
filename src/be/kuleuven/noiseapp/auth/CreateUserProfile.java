package be.kuleuven.noiseapp.auth;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import be.kuleuven.noiseapp.tools.JSONParser;

public class CreateUserProfile extends AsyncTask<String, String, String> {
	
	private BigInteger userID;
	private String firstName;
	private String lastName;
	private String email;
	private JSONParser jsonParser = new JSONParser();
	private String pictureURL;
	private static String url_create_userprofile = "http://192.168.212.1/android_connect/create_userprofile.php";
    private static final String TAG_SUCCESS = "success";
	
	public CreateUserProfile(BigInteger userID, String firstName, String lastName, String email, String pictureURL){
		this.userID = userID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.pictureURL = pictureURL;
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
    protected String doInBackground(String... args) {

        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userID", userID.toString()));
        params.add(new BasicNameValuePair("firstName", firstName));
        params.add(new BasicNameValuePair("lastName", lastName));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("pictureURL", pictureURL));

        // getting JSON Object
        // Note that create product url accepts POST method
        JSONObject json = jsonParser.makeHttpRequest(url_create_userprofile,
                "POST", params);

        // check log cat fro response
        Log.d("Create Response", json.toString());

        // check for success tag
        try {
            int success = json.getInt(TAG_SUCCESS);

            if (success == 1) {
            	
            } else {
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * After completing background task Dismiss the progress dialog
     * **/
    protected void onPostExecute(String file_url) {
    }

}


package be.kuleuven.noiseapp.auth;

/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import be.kuleuven.noiseapp.MainActivity;
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.ImageDownloader;
import be.kuleuven.noiseapp.tools.JSONParser;

import com.google.android.gms.auth.GoogleAuthUtil;

/**
 * Display personalized greeting. This class contains boilerplate code to consume the token but
 * isn't integral to getting the tokens.
 */
public abstract class AbstractGetInfoTask extends AsyncTask<Void, Void, Void>{
    private static final String TAG = "TokenInfoTask";
	private static final String GOOGLEID_KEY = "id";
    private static final String NAME_KEY = "given_name";
    private static final String FAMILY_NAME_KEY = "family_name";
	private static final String PICTURE_KEY = "picture";
	private static final int PICTURE_SIZE = 200;
    
    JSONParser jsonParser = new JSONParser();
    
    protected MainActivity mActivity;

    protected String mScope;
    protected String mEmail;
    protected int mRequestCode;
    
    //private UserProfileDataSource datasource;

    AbstractGetInfoTask(MainActivity activity, String email, String scope, int requestCode) {
    	//initializeUserProfileDatabase(activity);
        this.mActivity = activity;
        this.mScope = scope;
        this.mEmail = email;
        this.mRequestCode = requestCode;
    }

    @Override
    protected Void doInBackground(Void... params) {
      try {
        fetchInfoFromGoogleServer();
      } catch (IOException ex) {
        onError("Following Error occured, please try again. " + ex.getMessage(), ex);
      } catch (JSONException e) {
        onError("Bad response: " + e.getMessage(), e);
      }
      return null;
    }

    protected void onError(String msg, Exception e) {
        if (e != null) {
          Log.e(TAG, "Exception: ", e);
        }
        //mActivity.show(msg);  // will be run in UI thread
    }

    /**
     * Get a authentication token if one is not available. If the error is not recoverable then
     * it displays the error message on parent activity.
     */
    protected abstract String fetchToken() throws IOException;

    /**
     * Contacts the user info server to get the profile of the user and extracts the first name
     * of the user from the profile. In order to authenticate with the user info server the method
     * first fetches an access token from Google Play services.
     * @throws IOException if communication with user info server failed.
     * @throws JSONException if the response from the server could not be parsed.
     */
    private void fetchInfoFromGoogleServer() throws IOException, JSONException {
        String token = fetchToken();
        if (token == null) {
          // error has already been handled in fetchToken()
          return;
        }
        URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + token);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        int sc = con.getResponseCode();
        if (sc == 200) {
          InputStream is = con.getInputStream();
          String response = readResponse(is);
          JSONObject profile = new JSONObject(response);
          BigInteger googleID = getGoogleID(profile);
          String firstName = getFirstName(profile);
          String lastName = getLastName(profile);
          mActivity.showFirstName(firstName);
          String pictureURL = getPicture(profile);
          saveUserProfileServer(googleID, firstName, lastName, mEmail, pictureURL);
          saveUserProfileLocal(googleID, firstName, lastName, mEmail, pictureURL);
          is.close();
          return;
        } else if (sc == 401) {
            GoogleAuthUtil.invalidateToken(mActivity, token);
            onError("Server auth error, please try again.", null);
            Log.i(TAG, "Server auth error: " + readResponse(con.getErrorStream()));
            return;
        } else {
          onError("Server returned the following error code: " + sc, null);
          return;
        }
    }

	/**
     * Reads the response from the input stream and returns it as a string.
     */
    private static String readResponse(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] data = new byte[2048];
        int len = 0;
        while ((len = is.read(data, 0, data.length)) >= 0) {
            bos.write(data, 0, len);
        }
        return new String(bos.toByteArray(), "UTF-8");
    }
    
    private void saveUserProfileLocal(BigInteger googleID, String firstName, String lastName, String email, String pictureURL){
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mActivity);
		Editor edit = sp.edit();
		edit.putString("googleID",googleID.toString());
		edit.putString("firstName", firstName);
		edit.putString("lastName", lastName);
		edit.putString("email", email);
		edit.commit();
		new ImageDownloader(mActivity, Constants.FILENAME_PROFILE_PICTURE).execute(pictureURL + "?size=" + PICTURE_SIZE);
    }
    
    private void saveUserProfileServer(BigInteger googleID, String firstName, String lastName, String email, String pictureURL){
    	new CreateUserProfile(googleID, firstName, lastName, email, pictureURL, mActivity).execute();
    }

    private BigInteger getGoogleID(JSONObject profile) throws JSONException {
    	String toReturnString = profile.getString(GOOGLEID_KEY);
    	BigInteger toReturn = new BigInteger(toReturnString);
		return toReturn;
	}

    /**
     * Parses the response and returns the first name of the user.
     * @throws JSONException if the response is not JSON or if first name does not exist in response
     */
    private String getFirstName(JSONObject profile) throws JSONException {
      return profile.getString(NAME_KEY);
    }
    
    /**
     * Parses the response and returns the last name of the user.
     * @throws JSONException if the response is not JSON or if first name does not exist in response
     */
    private String getLastName(JSONObject profile) throws JSONException {
      return profile.getString(FAMILY_NAME_KEY);
    }
    
	private String getPicture(JSONObject profile) throws JSONException {
		return profile.getString(PICTURE_KEY);
	}
}

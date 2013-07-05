package be.kuleuven.noiseapp.soundbattle;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import be.kuleuven.noiseapp.SoundBattleActivity;
import be.kuleuven.noiseapp.SoundBattleRecordActivity;
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.ImageDownloader;
import be.kuleuven.noiseapp.tools.JSONParser;

public class CreateSoundBattleTask extends AsyncTask<Long, Void, JSONObject> {

	private SoundBattleActivity sba;
	private JSONParser jsonParser = new JSONParser();
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_SOUNDBATTLE_ID = "soundBattleID";
	private static final String url_create_soundbattle = Constants.BASE_URL_MYSQL + "create_soundbattle.php";
	private static final String TAG_OPPONENT_PROFILE = "opponentDetails";
	
	public CreateSoundBattleTask(SoundBattleActivity sba){
		this.sba = sba;
	}

	@Override
	protected JSONObject doInBackground(Long... args) {
		long userID = args[0];
		long opponentID = args[1];
		
		// Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userID1", Long.toString(userID)));
        params.add(new BasicNameValuePair("userID2", Long.toString(opponentID)));

        // getting JSON Object
        // Note that create product url accepts POST method
        JSONObject json = jsonParser.makeHttpRequest(url_create_soundbattle,
                "POST", params);

        // check log cat fro response
        Log.d("CreateSoundBattle Response", json.toString());

        // check for success tag
        try {
            int success = json.getInt(TAG_SUCCESS);

            if (success == 1) {
            	return json;
            } else {
            	
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
	}
	
	@Override
	protected void onPostExecute(JSONObject jso){
		String fName = "";
		String lName = "";
		String pictureURL = "";
		Long soundBattleID = null;
		try {
			fName = jso.getJSONArray(TAG_OPPONENT_PROFILE).getJSONObject(0).getString("firstName");
			lName = jso.getJSONArray(TAG_OPPONENT_PROFILE).getJSONObject(0).getString("lastName");
			pictureURL = jso.getJSONArray(TAG_OPPONENT_PROFILE).getJSONObject(0).getString("pictureURL");
			soundBattleID = jso.getLong(TAG_SOUNDBATTLE_ID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent i = new Intent(sba.getApplicationContext(),SoundBattleRecordActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra("soundBattleID", soundBattleID);
		i.putExtra("opponentFirstName", fName);
		i.putExtra("opponentLastName", lName);
		new ImageDownloader(sba, Constants.FILENAME_OPPONENT_PROFILE_PICTURE).execute(pictureURL);// + "?size=" + Constants.SIZE_OPPONENT_PRROFILE_PICTURE);
		//TODO add opponentinfo!
		sba.startActivity(i);
	}
}

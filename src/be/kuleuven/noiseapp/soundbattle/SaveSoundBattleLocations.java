package be.kuleuven.noiseapp.soundbattle;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.JSONParser;

public class SaveSoundBattleLocations extends AsyncTask<ActiveSoundBattle, Void, Void> {

	private static final String url_create_soundbattlelocation = Constants.BASE_URL_MYSQL + "create_soundbattlelocation.php";
	private JSONParser jsonParser = new JSONParser();
	
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_SOUNDBATTLELOCATION_ID = "soundBattleLocationID";

	@Override
	protected Void doInBackground(ActiveSoundBattle... args) {
		ActiveSoundBattle asb = args[0];
		
		// Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("soundBattleID", Long.toString(asb.getSoundBattleID())));
        for(int i = 0 ; i < asb.getSBLs().size(); i++){
        	params.add(new BasicNameValuePair("longitude" + (i+1), Double.toString(asb.getSBLs().get(i).getLatLng().longitude)));
        	params.add(new BasicNameValuePair("latitude" + (i+1), Double.toString(asb.getSBLs().get(i).getLatLng().latitude)));
        }
        // getting JSON Object
        // Note that create product url accepts POST method
        JSONObject json = jsonParser.makeHttpRequest(url_create_soundbattlelocation,
                "POST", params);

        // check log cat fro response
        Log.d("CreateSoundBattleLocation Response", json.toString());

        // check for success tag
        try {
            int success = json.getInt(TAG_SUCCESS);

            if (success == 1) {
            	long lastIndex = json.getLong(TAG_SOUNDBATTLELOCATION_ID);
            	for(int i = 0; i<3;i++){
            		asb.getSBLs().get(i).setSoundBattleLocationID(lastIndex + i);
            	}
            } else {
            	
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
	}
}

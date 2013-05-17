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
import be.kuleuven.noiseapp.SoundBattleRecordActivity;
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.JSONParser;

public class CreateSoundBattleTask extends AsyncTask<Long, Void, Long> {

	private Context context;
	private JSONParser jsonParser = new JSONParser();
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_SOUNDBATTLE_ID = "soundBattleID";
	private static final String url_create_soundbattle = Constants.BASE_URL_MYSQL + "create_soundbattle.php";
	
	public CreateSoundBattleTask(Context context){
		this.context = context;
	}

	@Override
	protected Long doInBackground(Long... args) {
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
            	return json.getLong(TAG_SOUNDBATTLE_ID);
            
            } else {
            	
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
	}
	
	@Override
	protected void onPostExecute(Long soundBattleID){
		Intent i = new Intent(context,SoundBattleRecordActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra("SoundBattleID", soundBattleID);
		context.startActivity(i);
	}

}

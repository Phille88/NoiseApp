package be.kuleuven.noiseapp.soundbattle;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.JSONParser;

public class GetRandomSoundBattleTask extends AsyncTask<Void, Void, Long> {
	private Context context;
	private long userID;
	private JSONParser jsonParser = new JSONParser();
	private static String url_get_random_player = Constants.BASE_URL_MYSQL + "get_random_player.php";
    private static final String TAG_SUCCESS = "success";
	private static final String TAG_OPPONENT_ID = "opponentID";
		
	public GetRandomSoundBattleTask(Context context){
		this.context = context;
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		this.userID = sp.getLong("userID", 0L);
	}

    /**
     * Creating profile
     * */
    protected Long doInBackground(Void... args) {

        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userID", Long.toString(userID)));

        // getting JSON Object
        // Note that create product url accepts POST method
        JSONObject json = jsonParser.makeHttpRequest(url_get_random_player,
                "POST", params);

        // check log cat fro response
        Log.d("GetRandomSoundBattle Response", json.toString());

        // check for success tag
        try {
            int success = json.getInt(TAG_SUCCESS);

            if (success == 1) {
                return json.getLong(TAG_OPPONENT_ID);
            } else {
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    protected void onPostExecute(Long result){
    	new CreateSoundBattleTask(context).execute(userID, result);
    }
}

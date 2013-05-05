package be.kuleuven.noiseapp.noisedatabase;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.JSONParser;

public class GetAllNoiseRecordingsTask extends AsyncTask<Void, Void, ArrayList<NoiseRecording>> {
	
	private JSONParser jsonParser = new JSONParser();
	private static String url_get_all_noiserecordings = Constants.BASE_URL_MYSQL + "get_all_noiserecordings.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_NOISERECORDINGS = "noiserecordings";
	
    @Override
	protected ArrayList<NoiseRecording> doInBackground(Void... args) {	

        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        // getting JSON Object
        // Note that create product url accepts POST method
        JSONObject json = jsonParser.makeHttpRequest(url_get_all_noiserecordings,
                "POST", params);

        // check log cat fro response
        Log.d("GetAllNoiseRecordings Response", json.toString());

        // check for success tag
        try {
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1) {
            	ArrayList<NoiseRecording> noiseRecordingsToReturn = new ArrayList<NoiseRecording>();
            	JSONArray noiseRecordings = json.getJSONArray(TAG_NOISERECORDINGS);
            	for (int i = 0; i<noiseRecordings.length();i++){
                	JSONObject nr = (JSONObject) noiseRecordings.get(i);
                	noiseRecordingsToReturn.add(new NoiseRecording(nr.getLong("userID"), nr.getDouble("latitude"), nr.getDouble("longitude"), nr.getDouble("dB"), nr.getDouble("accuracy"), nr.getDouble("quality")));
            	}
            	return noiseRecordingsToReturn;
            } else {
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
	}

}

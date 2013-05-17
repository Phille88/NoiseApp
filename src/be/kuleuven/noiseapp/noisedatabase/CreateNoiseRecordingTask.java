package be.kuleuven.noiseapp.noisedatabase;

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

public class CreateNoiseRecordingTask  extends AsyncTask<NoiseRecording, Void, Void>{
	
	private JSONParser jsonParser = new JSONParser();
	private static String url_create_noiserecording = Constants.BASE_URL_MYSQL + "create_noiserecording.php";
    private static final String TAG_SUCCESS = "success";
	private static final String TAG_NOISERECORDING_ID = "noiseID";

    /**
     * Creating profile
     * */
    protected Void doInBackground(NoiseRecording... args) {
    	NoiseRecording nr = args[0];
    	
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userID", Long.toString(nr.getUserID())));
        params.add(new BasicNameValuePair("latitude", Double.toString(nr.getLatitude())));
        params.add(new BasicNameValuePair("longitude", Double.toString(nr.getLongitude())));
        params.add(new BasicNameValuePair("dB",Double.toString(nr.getDB())));
        params.add(new BasicNameValuePair("accuracy",Double.toString(nr.getAccuracy())));
        params.add(new BasicNameValuePair("quality",Double.toString(nr.getQuality())));

        // getting JSON Object
        // Note that create product url accepts POST method
        JSONObject json = jsonParser.makeHttpRequest(url_create_noiserecording,
                "POST", params);

        // check log cat fro response
        Log.d("CreateNoiseRecording Response", json.toString());

        // check for success tag
        try {
            int success = json.getInt(TAG_SUCCESS);

            if (success == 1) {
            	int nrID = json.getInt(TAG_NOISERECORDING_ID);
            	nr.setId(nrID);
            } else {
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}

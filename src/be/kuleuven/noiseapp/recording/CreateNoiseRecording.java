package be.kuleuven.noiseapp.recording;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import be.kuleuven.noiseapp.noisedatabase.NoiseRecording;
import be.kuleuven.noiseapp.tools.JSONParser;

public class CreateNoiseRecording  extends AsyncTask<String, String, String>{


	private BigInteger userID;
	private double latitude;
	private double longitude;
	private double dB;
	private double accuracy;
	private double quality;
	
	private JSONParser jsonParser = new JSONParser();
	private static String url_create_noiserecording = "http://192.168.212.1/android_connect/create_noiserecording.php";
    private static final String TAG_SUCCESS = "success";
		
	public CreateNoiseRecording(NoiseRecording nr){
		this.userID = nr.getUserId();
		this.latitude = nr.getLatitude();
		this.longitude = nr.getLongitude();
		this.dB = nr.getDB();
		this.accuracy = nr.getAccuracy();
		this.quality = nr.getQuality();
	}

    /**
     * Creating profile
     * */
    protected String doInBackground(String... args) {

        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userID", userID.toString()));
        params.add(new BasicNameValuePair("latitude", Double.toString(latitude)));
        params.add(new BasicNameValuePair("longitude", Double.toString(longitude)));
        params.add(new BasicNameValuePair("dB",Double.toString(dB)));
        params.add(new BasicNameValuePair("accuracy",Double.toString(accuracy)));
        params.add(new BasicNameValuePair("quality",Double.toString(quality)));

        // getting JSON Object
        // Note that create product url accepts POST method
        JSONObject json = jsonParser.makeHttpRequest(url_create_noiserecording,
                "POST", params);

        // check log cat fro response
        Log.d("Create Response", json.toString());

        // check for success tag
        try {
            int success = json.getInt(TAG_SUCCESS);

            if (success == 1) {
                //mActivity.showFirstName(firstName);
                // closing this screen
//	                finish();
            } else {
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}

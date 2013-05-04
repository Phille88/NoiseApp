package be.kuleuven.noiseapp;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import be.kuleuven.noiseapp.places.PlaceSearchTask;
import be.kuleuven.noiseapp.tools.PlaceItem;
import be.kuleuven.noiseapp.tools.PlaceItemAdapter;

public class SoundCheckinActivity extends RecordActivity implements LocationListener{
	
	// fields for Android location
	protected LocationManager locationManager;
	private static String provider;
	protected Location currentLocation;
	private static Location LEUVEN_CENTER;
	ArrayList<PlaceItem> nearbyPlaces = new ArrayList<PlaceItem>();
	PlaceItemAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabledGPS = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

		LEUVEN_CENTER = new Location("GPS");
	    LEUVEN_CENTER.setLatitude(50.877571);
		LEUVEN_CENTER.setLongitude(4.704328);
        
        // Check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to 
        // go to the settings
        if (!enabledGPS) {
            Toast.makeText(this, "Please, enable GPS to use this application.", Toast.LENGTH_LONG).show();
        }
        
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        provider = LocationManager.GPS_PROVIDER;
//        Location location = locationManager.getLastKnownLocation(provider);
//        zoomTo(location);
        currentLocation = locationManager.getLastKnownLocation(provider);
        if(currentLocation == null)
        	currentLocation = LEUVEN_CENTER;
        
		listPlaces();
	}
	
	@Override
	protected void setView(){
		setContentView(R.layout.activity_sound_checkin);
	}

	private void listPlaces() {
		ListView listView = (ListView) findViewById(R.id.list_places);
		performSearch();
		
		// Define a new Adapter
		// First parameter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the TextView to which the data is written
		// Forth - the Array of data

		adapter = new PlaceItemAdapter(this,android.R.id.text1, nearbyPlaces);
		
		listView.setAdapter(adapter); 
//		ImageButton img_btn_checkin = (ImageButton) listView..findViewById(R.id.img_btn_checkin);
//		img_btn_checkin.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				 Toast.makeText(getApplicationContext(),
//					      "Click ListItem Number ", Toast.LENGTH_LONG)
//					      .show();
//			}
//			
//		});
		listView.setOnItemClickListener(recordItemButtonListener());
				
//				new OnItemClickListener() {
//			  @Override
//			  public void onItemClick(AdapterView<?> parent, View view,
//			    int position, long id) {
//			    Toast.makeText(getApplicationContext(),
//			      "Click ListItem Number " + position, Toast.LENGTH_LONG)
//			      .show();
//			  }
//		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.sound_checkin, menu);
		return true;
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}

	@Override
	protected void addListenerToRecordButton(){
	}

	protected OnItemClickListener recordItemButtonListener(){
		return new OnItemClickListener(){
		private Thread t;

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {	
//			if(isProviderFixed()){
		
				//prepare for a progress bar dialog
				progressBar = new ProgressDialog(v.getContext()){
					@Override
					public void onBackPressed(){
						this.dismiss();
						t.interrupt();
						t = null;
						return;
					}
					
					@Override
					public boolean onTouchEvent(MotionEvent e){
						this.dismiss();
						t.interrupt();
						t = null;
						return true;
					}
				};
				progressBar.setCancelable(true);
				progressBar.setMessage("Recording...");
				progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progressBar.setProgress(0);
				progressBar.setMax(100);
				progressBar.show();

				//reset progress bar status
				progressBarStatus = 0;
				
				//create the progress dialog thread
				t = new Thread(new Runnable() {
					private String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audiorecordsample.3gp";
					@Override
					public void run() {
						Thread thisThread = Thread.currentThread();ArrayList<Double> dBs =new ArrayList<Double>();
						//ArrayList<Double> amps =new ArrayList<Double>();
						double amp = 0;
						MediaRecorder mRecorder = new MediaRecorder();
						mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
						mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
						mRecorder.setAudioChannels(2);
						mRecorder.setAudioEncodingBitRate(44100);
						mRecorder.setOutputFile(mFileName); 
						mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
						try {
				            mRecorder.prepare();
				        } catch (IOException e) {
				            Log.e("AudioRecordTest", "prepare() failed");
				        }
						mRecorder.start();
						mRecorder.getMaxAmplitude();
						while (progressBarStatus < 100 && thisThread == t) {
							for(int i = 0; i<4;i++){
								try {
									Thread.sleep(250);

								} catch (InterruptedException e) {
									e.printStackTrace();
									progressBarStatus = 0;
								}
								amp = mRecorder.getMaxAmplitude();
								dBs.add(20 * Math.log10(amp / 0.5));
								//amps.add(amp);
								}

							// process some tasks
							progressBarStatus = doSomeTasks();

							// Update the progress bar
							progressBarHandler.post(new Runnable() {
								@Override
								public void run() {
									progressBar.setProgress(progressBarStatus);
								}
							});
						}

						// ok, task is done
						if (progressBarStatus >= 100) {
							mRecorder.stop();
							mRecorder.reset();
							mRecorder.release();
							mRecorder=null;
							double avgDB = 0;
							//double avgAmp = 0;
							int size = 0;
							for(int i = 0; i < dBs.size(); i++){
								if(!dBs.get(i).isNaN() && !dBs.get(i).isInfinite() && dBs.get(i) != 0){
									avgDB += dBs.get(i);
									//avgAmp += amps.get(i);
									size++;
								}
							}
							avgDB = avgDB/size;
							//avgAmp = avgAmp/size;

							// sleep 2 seconds, so that you can see the 100%
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							// close the progress bar dialog
							progressBar.dismiss();
							addNoiseRecording("demo", currentLocation.getLatitude(), currentLocation.getLongitude(), avgDB, 10);
							
							Thread.currentThread().interrupt();
							Bundle b = new Bundle();
							b.putDouble("dBlevel", avgDB);
							Intent i = new Intent(getApplicationContext(),
									RandomRecordPointsActivity.class);
							i.putExtras(b);
							startActivity(i);
						}
					}
				});
				t.start();
//			}
//			else
//				Toast.makeText(getApplicationContext(), "Wait for the GPS to have a fixed location", Toast.LENGTH_LONG).show();
		}
	};
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void setActionBarTitle() {
		getActionBar().setTitle(R.string.txt_sound_checkin_name);
	}

	@Override
	protected boolean popupNeeded() {
		return false;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.txt_sound_checkin_name;
	}

	@Override
	protected int getPopupExplanation() {
		return 0;
	}

	@Override
	protected String getPopupDontShowAgainName() {
		return null;
	}

	@Override
	protected OnClickListener recordButtonListener() {
		return null;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		if(isBetterLocation(location,currentLocation)){
			setProviderFixed(true);
			currentLocation = location;
			if(timeout()){
				performSearch();
				adapter.notifyDataSetChanged();
			}
		}
	}

	/** PLACES **/
	private static final String PLACES_SEARCH_URL =  "https://maps.googleapis.com/maps/api/place/nearbysearch/";
	private static final String REQUEST_OUTPUT = "json";
	//private static final String REQUEST_LOCATION = "37.787930,-122.4074990";
	private static final String REQUEST_RADIUS = "100";
	private static final String REQUEST_SENSOR = "false";
	//private static final String API_KEY = "AIzaSyAqAKvwtX7uTgdDqmlOmDN3jJk86ITzSCo";
	private static final String API_KEY = "AIzaSyDepfs8lsYdSvcLqBlUTBGrEcIOZ8A93Yw"; //BROWSER API KEY?

	public void performSearch() {
		nearbyPlaces.clear();
		PlaceSearchTask pst = new PlaceSearchTask();
		try {
			pst.execute(new URI(PLACES_SEARCH_URL + REQUEST_OUTPUT + "?location=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() + "&radius="+ REQUEST_RADIUS + "&sensor=" + REQUEST_SENSOR + "&key=" + API_KEY));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			parseJSON(pst.get());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void parseJSON(String toParse) throws JSONException{
		JSONObject jsonObject = new JSONObject(toParse);
		JSONArray resultsArray = jsonObject.getJSONArray("results");
		for(int i = 0; i < resultsArray.length();i++){
			nearbyPlaces.add(new PlaceItem(resultsArray.getJSONObject(i).getString("name").toString(),
			null,
			resultsArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
			resultsArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng")));
		}
	}
}
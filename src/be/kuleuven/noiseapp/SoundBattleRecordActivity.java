package be.kuleuven.noiseapp;

import java.util.ArrayList;
import java.util.Date;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;
import be.kuleuven.noiseapp.location.NoiseLocation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class SoundBattleRecordActivity extends android.support.v4.app.FragmentActivity implements LocationListener {

	private static final boolean DEBUG_MODE = true;
	//fields for Google Maps API
	private GoogleMap mMap;
	private UiSettings mUiSettings;
	
	//fields for Android location
	private LocationManager locationManager;
    private static String provider;
    private boolean providerFixed;
	private Location currentLocation;
	private ArrayList<NoiseLocation> SBLocations = new ArrayList<NoiseLocation>();
	private static final int THREE_SECONDS = 1000 * 2;
    private float currentZoomLevel = 16;
	private Date lastTouchTime = new Date(System.currentTimeMillis()-15000);
	private static Location LEUVEN_CENTER;
	
	private Button btn_record;
	
	//fields for progress bar
	private ProgressDialog progressBar;
	private int progressBarStatus = 0;
	private Handler progressBarHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_record);
		LEUVEN_CENTER = new Location("GPS");
	    LEUVEN_CENTER.setLatitude(50.877571);
		LEUVEN_CENTER.setLongitude(4.704328);
		
		setupActionBar();
		showPopup();
		
		addListenerToRecordButton();
		
		//map initialization
		setUpMapIfNeeded();
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabledGPS = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //boolean enabledWiFi = service.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

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
        currentLocation = locationManager.getLastKnownLocation(provider);
        if(currentLocation == null)
        	currentLocation = LEUVEN_CENTER;
        initializeBattleLocations();
        zoomTo(currentLocation);
	}
	
	private void showPopup(){
//		final Button btn_sound_battle_ok = (Button) findViewById(R.id.btn_sound_battle_ok);
//		btn_sound_battle_ok.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
		LayoutInflater layoutInflater  = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);  
	    View popupView = layoutInflater.inflate(R.layout.popup_sound_battle_explanation, null);  
	    
	    final PopupWindow popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);  
	    
	    ImageButton btnDismiss = (ImageButton)popupView.findViewById(R.id.btn_popup_sound_battle_explanation_ok);
	    btnDismiss.setOnClickListener(new ImageButton.OnClickListener(){
	     @Override
	     public void onClick(View v) {
	    	 popupWindow.dismiss();
	     }
	     });
	    DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
	    popupWindow.setHeight(metrics.heightPixels);
	    popupWindow.setWidth(metrics.widthPixels);
	    findViewById(R.id.layout_map_record).post(new Runnable() {
	    	   public void run() {
	    		    popupWindow.showAtLocation(findViewById(R.id.layout_map_record), Gravity.CENTER_HORIZONTAL, 0, 0);
	    	   }
	    	});
//			   }});
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setTitle(R.string.title_activity_sound_battle);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.sound_battle_record, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
//			NavUtils.navigateUpFromSameTask(this);
			Intent homeIntent = new Intent(this, MainActivity.class);
			NavUtils.navigateUpTo(this, homeIntent);
	        locationManager.removeUpdates(this);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onPause(){
		super.onPause();
		locationManager.removeUpdates(this);
	}
	
	@Override
	public void onStop(){
		super.onStop();
		locationManager.removeUpdates(this);
	}

	public void addListenerToRecordButton() {
		btn_record = (Button) findViewById(R.id.btn_record);
		btn_record.setOnClickListener(new OnClickListener() {
			
			private Thread t;

			@Override
			public void onClick(View v) {
				if(DEBUG_MODE){
					Intent i = new Intent(getApplicationContext(),
							SoundBattlePointsActivity.class);
					startActivity(i);
				}
				else {
				if(!isProviderFixed())
					Toast.makeText(getApplicationContext(), "Wait for the GPS to have a fixed location", Toast.LENGTH_SHORT).show();
				else if(!getClosestNoiseLocationToRecord().isClose(currentLocation) && !DEBUG_MODE)
					Toast.makeText(getApplicationContext(), "You have to get closer to a Sound Battle Location", Toast.LENGTH_SHORT).show();
				else {
			
					//prepare for a progress bar dialog
					progressBar = new ProgressDialog(v.getContext()){
						@Override
						public void onBackPressed(){
							this.dismiss();
							t.interrupt();
							t = null;
							return;
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
						
						public void run() {
							Thread thisThread = Thread.currentThread();
							while (progressBarStatus < 100 && thisThread == t) {
	
								// process some tasks
								progressBarStatus = doSomeTasks();
	
								// comp is too fast, sleep 1 second
								try {
									Thread.sleep(1000);
	
								} catch (InterruptedException e) {
									e.printStackTrace();
									progressBarStatus = 0;
								}
	
								// Update the progress bar
								progressBarHandler.post(new Runnable() {
									public void run() {
										progressBar.setProgress(progressBarStatus);
									}
								});
							}
	
							// ok, task is done
							if (progressBarStatus >= 100) {
	
								// sleep 2 seconds, so that you can see the 100%
								try {
									Thread.sleep(2000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								
								getClosestNoiseLocationToRecord().setRecorded(true);
	
								// close the progress bar dialog
								progressBar.dismiss();
								Thread.currentThread().interrupt();
								
								if(isEverythingRecorded()){
									Intent i = new Intent(getApplicationContext(),
											SoundBattlePointsActivity.class);
									startActivity(i);
								}
							}
						}
					});
					t.start();
				}
				}
			}
		});
		
	}

	/**
	 * 
	 * @return
	 */
	private int doSomeTasks() {
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e){
//			e.printStackTrace();
//		}
		 switch (progressBarStatus) {
		 case 0: return 10;
		 case 10: return 20;
		 case 20: return 30;
		 case 30: return 40;
		 case 40: return 50;
		 case 50: return 60;
		 case 60: return 70;
		 case 70: return 80;
		 case 80: return 90;
		 case 90: return 100;
		 }
		 return 100;
	}

	@Override
	protected void onResume() {
		super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 3, this);
		setUpMapIfNeeded();
	}

	/**
	 * Sets up the map if it is possible to do so (i.e., the Google Play
	 * services APK is correctly installed) and the map has not already been
	 * instantiated.. This will ensure that we only ever call
	 * {@link #setUpMap()} once when {@link #mMap} is not null.
	 * <p>
	 * If it isn't installed {@link SupportMapFragment} (and
	 * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt
	 * for the user to install/update the Google Play services APK on their
	 * device.
	 * <p>
	 * A user can return to this Activity after following the prompt and
	 * correctly installing/updating/enabling the Google Play services. Since
	 * the Activity may not have been completely destroyed during this process
	 * (it is likely that it would only be stopped or paused),
	 * {@link #onCreate(Bundle)} may not be called again so we should call this
	 * method in {@link #onResume()} to guarantee that it will be called.
	 */
	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();
			}
		}
	}

	/**
	 * This is where we can add markers or lines, add listeners or move the
	 * camera. In this case, we just add a marker near Africa.
	 * <p>
	 * This should only be called once and when we are sure that {@link #mMap}
	 * is not null.
	 */
	private void setUpMap() {
		mMap.setMyLocationEnabled(true);
		mUiSettings = mMap.getUiSettings();
		mMap.setOnCameraChangeListener(new OnCameraChangeListener() {

			@Override
		    public void onCameraChange(CameraPosition pos) {
		        if (pos.zoom != currentZoomLevel){
		            currentZoomLevel = pos.zoom;
		            lastTouchTime = new Date(System.currentTimeMillis());
		        }
		    }
		});
	}

	private void initializeBattleLocations(){
		SBLocations.add(new NoiseLocation(50.872359,4.70423));
		SBLocations.add(new NoiseLocation(50.872928,4.704766));
		SBLocations.add(new NoiseLocation(50.871235,4.70511));
		updateMarkers();
	}
	
	private void updateMarkers(){
		mMap.clear();
		for(NoiseLocation nl : SBLocations){
			mMap.addMarker(nl.getMarker(currentLocation));
		}		
	}

	/** Determines whether one Location reading is better than the current Location fix
	  * @param location  The new Location that you want to evaluate
	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	  */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > THREE_SECONDS;
	    boolean isSignificantlyOlder = timeDelta < -THREE_SECONDS;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}

	/**
	 * Checks if the map is ready (which depends on whether the Google Play
	 * services APK is available. This should be called prior to calling any
	 * methods on GoogleMap.
	 */
	private boolean checkReady() {
		if (mMap == null) {
			Toast.makeText(this, R.string.txt_map_not_ready, Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		return true;
	}

	public void setZoomButtonsEnabled(View v) {
		if (!checkReady()) {
			return;
		}
		// Enables/disables the zoom controls (+/- buttons in the bottom right
		// of the map).
		mUiSettings.setZoomControlsEnabled(((CheckBox) v).isChecked());
	}

	public void setCompassEnabled(View v) {
		if (!checkReady()) {
			return;
		}
		// Enables/disables the compass (icon in the top left that indicates the
		// orientation of the
		// map).
		mUiSettings.setCompassEnabled(((CheckBox) v).isChecked());
	}

	public void setMyLocationButtonEnabled(View v) {
		if (!checkReady()) {
			return;
		}
		// Enables/disables the my location button (this DOES NOT enable/disable
		// the my location
		// dot/chevron on the map). The my location button will never appear if
		// the my location
		// layer is not enabled.
		mUiSettings.setMyLocationButtonEnabled(((CheckBox) v).isChecked());
	}

	public void setMyLocationLayerEnabled(View v) {
		if (!checkReady()) {
			return;
		}
		// Enables/disables the my location layer (i.e., the dot/chevron on the
		// map). If enabled, it
		// will also cause the my location button to show (if it is enabled); if
		// disabled, the my
		// location button will never show.
		mMap.setMyLocationEnabled(((CheckBox) v).isChecked());
	}

	public void setScrollGesturesEnabled(View v) {
		if (!checkReady()) {
			return;
		}
		// Enables/disables scroll gestures (i.e. panning the map).
		mUiSettings.setScrollGesturesEnabled(((CheckBox) v).isChecked());
	}

	public void setZoomGesturesEnabled(View v) {
		if (!checkReady()) {
			return;
		}
		// Enables/disables zoom gestures (i.e., double tap, pinch & stretch).
		mUiSettings.setZoomGesturesEnabled(((CheckBox) v).isChecked());
	}

	public void setTiltGesturesEnabled(View v) {
		if (!checkReady()) {
			return;
		}
		// Enables/disables tilt gestures.
		mUiSettings.setTiltGesturesEnabled(((CheckBox) v).isChecked());
	}

	public void setRotateGesturesEnabled(View v) {
		if (!checkReady()) {
			return;
		}
		// Enables/disables rotate gestures.
		mUiSettings.setRotateGesturesEnabled(((CheckBox) v).isChecked());
	}

	/**
	 * Overridden methods of LocationListener
	 */
	@Override
	public void onLocationChanged(Location location) {
		if(isBetterLocation(location,currentLocation)){
			setProviderFixed(true);
			currentLocation = location;
			updateMarkers();
			if(timeout()){
				zoomTo(currentLocation);
			}
		}
	}
	
	private boolean timeout() { 
		Date curDateTime = new Date(System.currentTimeMillis());  
		return curDateTime.getTime() - lastTouchTime.getTime() >= 15000;
	}

	private NoiseLocation getClosestNoiseLocationToRecord(){
		double smallestDistance = Double.MAX_VALUE;
		NoiseLocation toReturn = null;
		for(NoiseLocation nl : SBLocations)
			if(nl.getDistance(currentLocation) < smallestDistance && !nl.isRecorded()){
				smallestDistance = nl.getDistance(currentLocation);
				toReturn = nl;
			}
		return toReturn;
	}

	private void zoomTo(Location location) {
		double lat = location.getLatitude();
        double lng = location.getLongitude();
        LatLng latLng = new LatLng(lat, lng);
		zoomTo(latLng);
	}
	
	private void zoomTo(LatLng latlng){
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, currentZoomLevel));
	}

	@Override
	public void onProviderDisabled(String provider) {
		setProviderFixed(false);
	}

	@Override
	public void onProviderEnabled(String provider) {
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}

	public void setProviderFixed(boolean providerFixed) {
		this.providerFixed = providerFixed;
	}

	public boolean isProviderFixed() {
		return providerFixed;
	}
	
	private boolean isEverythingRecorded(){
		return getClosestNoiseLocationToRecord() == null;
	}

}

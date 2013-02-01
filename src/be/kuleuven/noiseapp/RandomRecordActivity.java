package be.kuleuven.noiseapp;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;

public class RandomRecordActivity extends
		android.support.v4.app.FragmentActivity implements LocationListener {
	private GoogleMap mMap;
	private UiSettings mUiSettings;
	
	private LocationManager locationManager;
    private String provider;
	private LatLng currentCoordinate;

	@TargetApi(17)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_random_record);
		setUpMapIfNeeded();
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.title_activity_random_record);
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabledGPS = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
       // boolean enabledWiFi = service.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        // Check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to 
        // go to the settings
        if (!enabledGPS) {
            Toast.makeText(this, "GPS signal not found", Toast.LENGTH_LONG).show();
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            Toast.makeText(this, "Selected Provider " + provider, Toast.LENGTH_SHORT).show();
            onLocationChanged(location);
        } else {

            //do something
        }
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intentHome = new Intent(this, MainActivity.class);
		switch (item.getItemId()) {

		case android.R.id.home:
			// app icon in action bar clicked; go home
			this.finish();
			startActivity(intentHome);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
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
		double lat =  location.getLatitude();
        double lng = location.getLongitude();
        currentCoordinate = new LatLng(lat, lng);
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentCoordinate, 10));
		mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}
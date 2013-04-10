package be.kuleuven.noiseapp.location;

import android.location.Location;
import be.kuleuven.noiseapp.R;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;

public class SoundBattleLocation extends NoiseLocation {
	//colors of the markers
	//private static final BitmapDescriptor FAR = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
//	private static final BitmapDescriptor ON_THE_WAY = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
//	private static final BitmapDescriptor CLOSE = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
//	private static final BitmapDescriptor RECORDED = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
	private static final BitmapDescriptor FAR = BitmapDescriptorFactory.fromResource(R.drawable.img_marker_far);
	private static final BitmapDescriptor ON_THE_WAY = BitmapDescriptorFactory.fromResource(R.drawable.img_marker_ontheway);
	private static final BitmapDescriptor CLOSE = BitmapDescriptorFactory.fromResource(R.drawable.img_marker_close);
	private static final BitmapDescriptor RECORDED = BitmapDescriptorFactory.fromResource(R.drawable.img_marker_recorded);
	
	private static final double TEN_METRES = 10;
	private static final double FIFTY_METERS = 50;

	public SoundBattleLocation(double longitude, double latitude) {
		super(longitude, latitude);
		this.setRecorded(false);
	}
	
	public MarkerOptions getMarker(Location currentLocation){
		if (isRecorded())
			return new MarkerOptions().position(getLatLng())
					.title("Sound Battle Location")
					.snippet("You have successfully recorded this place!")
					.icon(RECORDED);
		else if (isClose(currentLocation))
			return new MarkerOptions().position(getLatLng())
					.title("Sound Battle Location")
					.snippet("You are ready to record at this lcoation!")
					.icon(CLOSE);
		else if (isOnTheWay(currentLocation))
			return new MarkerOptions().position(getLatLng())
					.title("Sound Battle Location")
					.snippet("You are almost there!").icon(ON_THE_WAY);
		else
			return new MarkerOptions().position(getLatLng())
					.title("Sound Battle Location")
					.snippet("Come closer to record here!").icon(FAR);
	}
	
	public boolean isClose(Location l){
	    return getDistance(l) <= TEN_METRES && !isRecorded();
	}
	
	public boolean isOnTheWay(Location l){
	    return getDistance(l) > TEN_METRES && getDistance(l) <= FIFTY_METERS && !isRecorded();
	}
	
	public boolean isFar(Location l){
	    return getDistance(l) > FIFTY_METERS && !isRecorded();
	}

}

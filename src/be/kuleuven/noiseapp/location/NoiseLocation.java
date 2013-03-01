package be.kuleuven.noiseapp.location;

import android.location.Location;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class NoiseLocation{
	private LatLng latLng;
	private boolean recorded;
	//colors of the markers
	private static final BitmapDescriptor FAR = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
	private static final BitmapDescriptor ON_THE_WAY = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
	private static final BitmapDescriptor CLOSE = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
	private static final BitmapDescriptor RECORDED = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
	
	private static final double TWENTY_METRES = 20;
	private static final double FIFTY_METERS = 50;
	
	public NoiseLocation(double latitude, double longitude){
		this.setLatLng(latitude,longitude);
		this.setRecorded(false);
	}

	public void setLatLng(double latitude, double longitude) {
		this.latLng = new LatLng(latitude,longitude);
	}

	public LatLng getLatLng() {
		return latLng;
	}
	
	public MarkerOptions getMarker(Location currentLocation){
		if (isRecorded())
			return new MarkerOptions().position(getLatLng())
					.title("Sound Battle Location")
					.snippet("You have already recorded this place!")
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

	public void setRecorded(boolean recorded) {
		this.recorded = recorded;
	}

	public boolean isRecorded() {
		return recorded;
	}
	
	public boolean isClose(Location l){
	    return getDistance(l) <= TWENTY_METRES && !isRecorded();
	}
	
	public boolean isOnTheWay(Location l){
	    return getDistance(l) > TWENTY_METRES && getDistance(l) <= FIFTY_METERS && !isRecorded();
	}
	
	public boolean isFar(Location l){
	    return getDistance(l) > FIFTY_METERS && !isRecorded();
	}
	
	/**
	 * Returns the distance between two coordinates.
	 * 
	 * @param l1
	 * @param l
	 * @return
	 */
	public double getDistance(Location l){
		double earthRadius = 3958.75;
	    double dLat = Math.toRadians(l.getLatitude()-getLatLng().latitude);
	    double dLng = Math.toRadians(l.getLongitude()-getLatLng().longitude);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(getLatLng().latitude)) * Math.cos(Math.toRadians(l.getLatitude())) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    int meterConversion = 1609;
	    return dist*meterConversion;
	}

}

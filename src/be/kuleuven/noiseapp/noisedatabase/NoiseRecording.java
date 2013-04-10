package be.kuleuven.noiseapp.noisedatabase;

import be.kuleuven.noiseapp.R;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class NoiseRecording {
	  private long id;
	  private long userId;
	  private double latitude;
	  private double longitude;
	  private double dB;
	  private double accuracy;
	  
	  private static final double HUNDRED_DB = 100;
	  private static final double NINETY_DB = 90; 
	  private static final double SIXTY_DB = 60; 
	  
		private static final BitmapDescriptor LOUD = BitmapDescriptorFactory.fromResource(R.drawable.img_marker_loud);
		private static final BitmapDescriptor MEDIUMLOUD = BitmapDescriptorFactory.fromResource(R.drawable.img_marker_mediumloud);
		private static final BitmapDescriptor MEDIUMSTILL = BitmapDescriptorFactory.fromResource(R.drawable.img_marker_mediumstill);
		private static final BitmapDescriptor STILL = BitmapDescriptorFactory.fromResource(R.drawable.img_marker_still);
	  
	  public long getId() {
	    return id;
	  }

	  public void setId(long id) {
	    this.id = id;
	  }

	/**
	 * @return the userId
	 */
	public long getUserId() {
		return userId;
	}

	/**
	 * @param string the userId to set
	 */
	public void setUserId(long string) {
		this.userId = string;
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the db
	 */
	public double getDB() {
		return dB;
	}

	/**
	 * @param dB the db to set
	 */
	public void setDB(double dB) {
		this.dB = dB;
	}

	/**
	 * @return the accuracy
	 */
	public double getAccuracy() {
		return accuracy;
	}

	/**
	 * @param accuracy the accuracy to set
	 */
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}
	
	@Override
	public String toString(){
		return "User: " + getUserId() + "\nLocation: " + getLongitude() + ", " + getLatitude() + "\nNoise Level: " + getDB() + "dB\nAccuracy: " + getAccuracy();
	}

	public MarkerOptions getMarker() {
		if(getDB() > HUNDRED_DB)
			return new MarkerOptions().position(new LatLng(getLatitude(),getLongitude()))
			.title("dB: " + getDB())
			.icon(LOUD);
		else if (getDB() > NINETY_DB)
			return new MarkerOptions().position(new LatLng(getLatitude(),getLongitude()))
			.title("dB: " + getDB())
			.icon(MEDIUMLOUD);
		else if (getDB() > SIXTY_DB)
			return new MarkerOptions().position(new LatLng(getLatitude(),getLongitude()))
			.title("dB: " + getDB())
			.icon(MEDIUMSTILL);
		else 
			return new MarkerOptions().position(new LatLng(getLatitude(),getLongitude()))
			.title("dB: " + getDB())
			.icon(STILL);
	}
}
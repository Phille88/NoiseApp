package be.kuleuven.noiseapp.noisedatabase;

import java.io.Serializable;
import java.math.BigInteger;

import android.graphics.Color;
import be.kuleuven.noiseapp.R;
import be.kuleuven.noiseapp.points.RecordingPoints;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class NoiseRecording implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private long id;
	private BigInteger userID;
	private double latitude;
	private double longitude;
	private double dB;
	private double accuracy;
	private double quality;
	private RecordingPoints recordingPoints;
	  
	private static final double EIGHTY_DB = 80;
	private static final double SIXTY_DB = 60; 
	private static final double FORTY_DB = 40; 
	  
	private static final BitmapDescriptor LOUD = BitmapDescriptorFactory.fromResource(R.drawable.img_marker_loud);
	private static final BitmapDescriptor MEDIUMLOUD = BitmapDescriptorFactory.fromResource(R.drawable.img_marker_mediumloud);
	private static final BitmapDescriptor MEDIUMSTILL = BitmapDescriptorFactory.fromResource(R.drawable.img_marker_mediumstill);
	private static final BitmapDescriptor STILL = BitmapDescriptorFactory.fromResource(R.drawable.img_marker_still);
	  
	public NoiseRecording(){
	}
	
	public NoiseRecording(BigInteger userID, double latitude, double longitude, double dB, double accuracy, double quality){
		this.userID = userID;
		this.latitude = latitude;
		this.longitude = longitude;
		this.dB = dB;
		this.accuracy = accuracy;
		this.setQuality(quality);
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
	    this.id = id;
	}

	/**
	 * @return the userId
	 */
	public BigInteger getUserId() {
		return userID;
	}

	/**
	 * @param string the userId to set
	 */
	public void setUserID(BigInteger userID) {
		this.userID = userID;
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
		if(getDB() > EIGHTY_DB)
			return new MarkerOptions().position(new LatLng(getLatitude(),getLongitude()))
			.title("dB: " + getDB())
			.icon(LOUD);
		else if (getDB() > SIXTY_DB)
			return new MarkerOptions().position(new LatLng(getLatitude(),getLongitude()))
			.title("dB: " + getDB())
			.icon(MEDIUMLOUD);
		else if (getDB() > FORTY_DB)
			return new MarkerOptions().position(new LatLng(getLatitude(),getLongitude()))
			.title("dB: " + getDB())
			.icon(MEDIUMSTILL);
		else 
			return new MarkerOptions().position(new LatLng(getLatitude(),getLongitude()))
			.title("dB: " + getDB())
			.icon(STILL);
	}
	
	public int getLoudnessColor(){
		if(getDB() > EIGHTY_DB)
			return Color.rgb(255, 7, 24);
		else if (getDB() > SIXTY_DB)
			return Color.rgb(255, 96, 5);
		else if (getDB() > FORTY_DB)
			return Color.rgb(255, 216, 0);
		else
			return Color.rgb(0, 255, 33);
	}

	public void setQuality(double quality) {
		this.quality = quality;
	}

	public double getQuality() {
		return quality;
	}

	public void setRecordingPoints(RecordingPoints recordingPoints) {
		this.recordingPoints = recordingPoints;
	}

	public RecordingPoints getRecordingPoints() {
		return recordingPoints;
	}
}



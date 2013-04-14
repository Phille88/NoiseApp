package be.kuleuven.noiseapp.noisedatabase;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class NoiseRecordingsDataSource {
	 private SQLiteDatabase database;
	 private NoiseRecordingSQLiteHelper dbHelper;
	 private String[] allColumns = { NoiseRecordingSQLiteHelper.COLUMN_ID,
	      NoiseRecordingSQLiteHelper.COLUMN_USERID,
	      NoiseRecordingSQLiteHelper.COLUMN_LATITUDE,
	      NoiseRecordingSQLiteHelper.COLUMN_LONGITUDE,
	      NoiseRecordingSQLiteHelper.COLUMN_DB,
	      NoiseRecordingSQLiteHelper.COLUMN_ACCURACY};
	 
	 public NoiseRecordingsDataSource(Context context) {
		 dbHelper = new NoiseRecordingSQLiteHelper(context);
	 }
	 
	 public void open() throws SQLException {
		 database = dbHelper.getWritableDatabase();
	 }

	 public void close() {
	    dbHelper.close();
	 }
	
	 public NoiseRecording createNoiseRecording(String userid, double lat, double lon, double dB, double acc) {
		 ContentValues values = new ContentValues();
	     values.put(NoiseRecordingSQLiteHelper.COLUMN_USERID, userid);
	     values.put(NoiseRecordingSQLiteHelper.COLUMN_LATITUDE, lat);
	     values.put(NoiseRecordingSQLiteHelper.COLUMN_LONGITUDE, lon);
	     values.put(NoiseRecordingSQLiteHelper.COLUMN_DB, dB);
	     values.put(NoiseRecordingSQLiteHelper.COLUMN_ACCURACY, acc);
	     long insertId = database.insert(NoiseRecordingSQLiteHelper.TABLE_NOISERECORDINGS, null,values);
	     Cursor cursor = database.query(NoiseRecordingSQLiteHelper.TABLE_NOISERECORDINGS,
	        allColumns, NoiseRecordingSQLiteHelper.COLUMN_ID + " = " + insertId, null,
	        null, null, null);
	    cursor.moveToFirst();
	    NoiseRecording newNoiseRecording = cursorToNoiseRecording(cursor);
	    cursor.close();
	    return newNoiseRecording;
	  }
	
	 public void deleteNoiseRecording(NoiseRecording noiseRecording) {
	    long id = noiseRecording.getId();
	    System.out.println("Comment deleted with id: " + id);
	    database.delete(NoiseRecordingSQLiteHelper.TABLE_NOISERECORDINGS, NoiseRecordingSQLiteHelper.COLUMN_ID
	    + " = " + id, null);
	  }
	 
	 public void deleteAllNoiseRecordings(){
		 ArrayList<NoiseRecording> noiseRecordings = getAllNoiseRecordings();
		 for(NoiseRecording nr : noiseRecordings) {
			 deleteNoiseRecording(nr);
		 }
	 }
	
	  public ArrayList<NoiseRecording> getAllNoiseRecordings() {
		  ArrayList<NoiseRecording> noiseRecordings = new ArrayList<NoiseRecording>();
	
	    Cursor cursor = database.query(NoiseRecordingSQLiteHelper.TABLE_NOISERECORDINGS,
	        allColumns, null, null, null, null, null);
	
	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	NoiseRecording noiseRecording = cursorToNoiseRecording(cursor);
	    	noiseRecordings.add(noiseRecording);
	      cursor.moveToNext();
	    }
	    // Make sure to close the cursor
	    cursor.close();
	    return noiseRecordings;
	  }
	
	  private NoiseRecording cursorToNoiseRecording(Cursor cursor) {
		NoiseRecording noiseRecording = new NoiseRecording();
	    noiseRecording.setId(cursor.getLong(0));
	    noiseRecording.setUserId(cursor.getLong(1));
	    noiseRecording.setLatitude(Double.parseDouble(cursor.getString(2)));
	    noiseRecording.setLongitude(Double.parseDouble(cursor.getString(3)));
	    noiseRecording.setDB(Double.parseDouble(cursor.getString(4)));
	    noiseRecording.setAccuracy(Double.parseDouble(cursor.getString(5)));
	    return noiseRecording;
	  }

	public void read() {
		 database = dbHelper.getReadableDatabase();
		
	}
}
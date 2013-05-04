package be.kuleuven.noiseapp.noisedatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NoiseRecordingSQLiteHelper extends SQLiteOpenHelper {
	
	public static final String TABLE_NOISERECORDINGS = "NoiseRecordings";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_USERID = "_userid";
	public static final String COLUMN_LATITUDE = "_latitude";
	public static final String COLUMN_LONGITUDE = "_longitude";
	public static final String COLUMN_DB = "_db";
	public static final String COLUMN_ACCURACY = "_accuracy";

	private static final String DATABASE_NAME = "noiserecordings.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
	    + TABLE_NOISERECORDINGS + "(" 
	    + COLUMN_ID + " integer primary key autoincrement, " 
	    + COLUMN_USERID + " text not null, "
	    + COLUMN_LATITUDE + " text not null, "
	    + COLUMN_LONGITUDE + " text not null, "
	    + COLUMN_DB + " text not null, "
	    + COLUMN_ACCURACY + " text not null);";
	
	
	public NoiseRecordingSQLiteHelper(Context context) {
		    super(context, DATABASE_NAME, null, DATABASE_VERSION);
		  }

	@Override
	  public void onCreate(SQLiteDatabase database) {
	    database.execSQL(DATABASE_CREATE);
	  }

	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(NoiseRecordingSQLiteHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOISERECORDINGS);
	    onCreate(db);
	  }
}

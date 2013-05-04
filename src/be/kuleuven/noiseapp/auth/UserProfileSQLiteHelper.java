package be.kuleuven.noiseapp.auth;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserProfileSQLiteHelper extends SQLiteOpenHelper {
	
	public static final String TABLE_USERPROFILES = "UserProfiles";
	//public static final String COLUMN_ID = "_id";
	public static final String COLUMN_USERID = "_userid";
	public static final String COLUMN_FIRST_NAME = "_first_name";
	public static final String COLUMN_LAST_NAME = "_last_name";
	public static final String COLUMN_POINTS = "_points";

	private static final String DATABASE_NAME = "profiles.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
	    + TABLE_USERPROFILES + "(" 
	    //+ COLUMN_ID + " integer primary key autoincrement, " 
	    + COLUMN_USERID + " text not null, "
	    + COLUMN_FIRST_NAME + " text not null, "
	    + COLUMN_LAST_NAME + " text not null, "
	    + COLUMN_POINTS + " text not null);";
	
	
	public UserProfileSQLiteHelper(Context context) {
		    super(context, DATABASE_NAME, null, DATABASE_VERSION);
		  }

	@Override
	  public void onCreate(SQLiteDatabase database) {
	    database.execSQL(DATABASE_CREATE);
	  }

	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(UserProfileSQLiteHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERPROFILES);
	    onCreate(db);
	  }
}

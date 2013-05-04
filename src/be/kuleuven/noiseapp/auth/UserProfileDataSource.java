package be.kuleuven.noiseapp.auth;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class UserProfileDataSource {
	 private SQLiteDatabase database;
	 private UserProfileSQLiteHelper dbHelper;
	 private String[] allColumns = { //UserProfileSQLiteHelper.COLUMN_ID,
	      UserProfileSQLiteHelper.COLUMN_USERID,
	      UserProfileSQLiteHelper.COLUMN_FIRST_NAME,
	      UserProfileSQLiteHelper.COLUMN_LAST_NAME,
	      UserProfileSQLiteHelper.COLUMN_POINTS};
	 
	 public UserProfileDataSource(Context context) {
		 dbHelper = new UserProfileSQLiteHelper(context);
	 }
	 
	 public void open() throws SQLException {
		 database = dbHelper.getWritableDatabase();
	 }

	 public void close() {
	    dbHelper.close();
	 }
	
	 public UserProfile createUserProfile(int userID, String firstName, String lastName, int points) {
		 ContentValues values = new ContentValues();
	     values.put(UserProfileSQLiteHelper.COLUMN_USERID, userID);
	     values.put(UserProfileSQLiteHelper.COLUMN_FIRST_NAME, firstName);
	     values.put(UserProfileSQLiteHelper.COLUMN_LAST_NAME, lastName);
	     values.put(UserProfileSQLiteHelper.COLUMN_POINTS, points);
	     long insertId = database.insert(UserProfileSQLiteHelper.TABLE_USERPROFILES, null,values);
	     Cursor cursor = database.query(UserProfileSQLiteHelper.TABLE_USERPROFILES,
	        allColumns, UserProfileSQLiteHelper.COLUMN_USERID + " = " + insertId, null,
	        null, null, null);
	    cursor.moveToFirst();
	    UserProfile newNoiseRecording = cursorToUserProfile(cursor);
	    cursor.close();
	    return newNoiseRecording;
	  }
	
	 public void deleteUserProfile(UserProfile noiseRecording) {
	    long userID = noiseRecording.getUserID();
	    System.out.println("Profile deleted with id: " + userID);
	    database.delete(UserProfileSQLiteHelper.TABLE_USERPROFILES, UserProfileSQLiteHelper.COLUMN_USERID
	    + " = " + userID, null);
	  }
	 
	 public void deleteAllUserProfiles(){
		 ArrayList<UserProfile> userProfiles = getAllUserProfiles();
		 for(UserProfile up : userProfiles) {
			 deleteUserProfile(up);
		 }
	 }
	
	  public ArrayList<UserProfile> getAllUserProfiles() {
		  ArrayList<UserProfile> userProfiles = new ArrayList<UserProfile>();
	
	    Cursor cursor = database.query(UserProfileSQLiteHelper.TABLE_USERPROFILES,
	        allColumns, null, null, null, null, null);
	
	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	UserProfile userProfile = cursorToUserProfile(cursor);
	    	userProfiles.add(userProfile);
	      cursor.moveToNext();
	    }
	    // Make sure to close the cursor
	    cursor.close();
	    return userProfiles;
	  }
	
	  private UserProfile cursorToUserProfile(Cursor cursor) {
		UserProfile userProfile = new UserProfile();
	    userProfile.setUserID(cursor.getLong(0));
	    return userProfile;
	  }

	public void read() {
		 database = dbHelper.getReadableDatabase();
		
	}
}
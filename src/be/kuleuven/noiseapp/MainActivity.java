package be.kuleuven.noiseapp;

import static be.kuleuven.noiseapp.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static be.kuleuven.noiseapp.CommonUtilities.SENDER_ID;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;
import be.kuleuven.noiseapp.auth.GetInfoInForeground;
import be.kuleuven.noiseapp.auth.UpdateLocalProfileDetailsTask;
import be.kuleuven.noiseapp.auth.UserDetails;
import be.kuleuven.noiseapp.friends.UpdateFriendsListLocalTask;
import be.kuleuven.noiseapp.noisehunt.GetNoiseHuntStateTask;
import be.kuleuven.noiseapp.noisehunt.NoiseHuntActivity;
import be.kuleuven.noiseapp.profile.ViewProfileTabActivity;
import be.kuleuven.noiseapp.randomrecord.RandomRecordActivity;
import be.kuleuven.noiseapp.showmap.ShowMapActivity;
import be.kuleuven.noiseapp.soundbattle.SoundBattleActivity;
import be.kuleuven.noiseapp.soundcheckin.SoundCheckinActivity;
import be.kuleuven.noiseapp.tools.MemoryFileNames;
import be.kuleuven.noiseapp.tools.ObjectSerializer;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends Activity {
    private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
	private static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1001;
    private static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;
    private SharedPreferences sp;
    private AsyncTask<Void, Void, Void> mRegisterTask;
	static long userID;
	static String email;
  
    //TODO About button?

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		
		if(!isNetworkAvailable() && !isLocationAvailable()){
			Toast message = Toast.makeText(this, "You need an active internet connection and have your GPS enabled for this application.\nPlease check your connection and location settings.", Toast.LENGTH_LONG);
			message.setGravity(Gravity.CENTER,0,0);
			message.show();
			finish();
		}
		else if(!isNetworkAvailable()){
			Toast message = Toast.makeText(this, "You need an active internet connection for this application.\nPlease check your connection settings.", Toast.LENGTH_LONG);
			message.setGravity(Gravity.CENTER,0,0);
			message.show();
			finish();
		}
		else if(!isLocationAvailable()){
			Toast message = Toast.makeText(this, "You need to have your GPS enabled for this application.\nPlease check you location settings.", Toast.LENGTH_LONG);
			message.setGravity(Gravity.CENTER,0,0);
			message.show();
			finish();
		}
		else{
			getAccountInfo();
			getNoiseHuntState();
			getFriendsList();
			registerToGCM();
			
			ImageButton btn_RandomRecord = (ImageButton) findViewById(R.id.btn_random_record);
			btn_RandomRecord.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View arg0) {
					Intent i = new Intent(getApplicationContext(), RandomRecordActivity.class);
					startActivity(i);
				}
			});
	
			ImageButton btn_soundBattle = (ImageButton) findViewById(R.id.btn_sound_battle);
			btn_soundBattle.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View arg0) {
					Intent i = new Intent(getApplicationContext(), SoundBattleActivity.class);
					startActivity(i);
				}
			});
			
			ImageButton btn_soundCheckin = (ImageButton) findViewById(R.id.btn_sound_checkin);
			btn_soundCheckin.setOnClickListener(new OnClickListener(){
	
				@Override
				public void onClick(View v) {
					Intent i = new Intent(getApplicationContext(), SoundCheckinActivity.class);
					startActivity(i);
				}
			});
	
			ImageButton btn_noiseHunt = (ImageButton) findViewById(R.id.btn_noise_hunt);
			btn_noiseHunt.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View arg0) {
					Intent i = new Intent(getApplicationContext(), NoiseHuntActivity.class);
					startActivity(i);
				}
			});
	
			ImageButton btn_viewProfile = (ImageButton) findViewById(R.id.btn_view_profile);
			btn_viewProfile.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View arg0) {
					Intent i = new Intent(getApplicationContext(), ViewProfileTabActivity.class);
					startActivity(i);
				}
			});
			
			ImageButton btn_showMap = (ImageButton) findViewById(R.id.btn_show_map);
			btn_showMap.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View arg0) {
					Intent i = new Intent(getApplicationContext(), ShowMapActivity.class);
					startActivity(i);
				}
			});
		}
	}
	
	private void registerToGCM(){
		new RegisterToGCMTask(this).execute();
	}

	protected void registerToGCMTask() {
		while(sp.getLong(MemoryFileNames.USERID, 0L) == 0L || sp.getString(MemoryFileNames.USERDETAILS, null) == null){
			//wait
		}
		MainActivity.userID = sp.getLong(MemoryFileNames.USERID, 0L);
		MainActivity.email = ((UserDetails) ObjectSerializer.deserialize(sp.getString(MemoryFileNames.USERDETAILS, null))).getEmail();
		//new RegisterToGCMTask(this).execute();
		// Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);
 
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);
         
        this.registerReceiver(mHandleMessageReceiver, new IntentFilter(
                DISPLAY_MESSAGE_ACTION));
        
        // Get GCM registration id
        final String regId = GCMRegistrar.getRegistrationId(this);
        
		// Check if regid already presents
        if (regId.equals("")) {
            // Registration is not present, register now with GCM           
            GCMRegistrar.register(this, SENDER_ID);
        } else {
            // Device is already registered on GCM
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.              
//                Toast.makeText(this.getApplicationContext(), "Already registered with GCM", Toast.LENGTH_LONG).show();
            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {
 
                    @Override
                    protected Void doInBackground(Void... params) {
                        // Register on our server
                        // On server creates a new user
                        ServerUtilities.register(context, sp.getLong(MemoryFileNames.USERID, 0L), ((UserDetails) ObjectSerializer.deserialize(sp.getString(MemoryFileNames.USERDETAILS, null))).getEmail(), regId);
                        return null;
                    }
 
                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }
 
                };
                mRegisterTask.execute(null, null, null);
            }
        }
	}
	
	 /**
     * Receiving push messages
     * */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
            // Waking up mobile if it is sleeping
            WakeLocker.acquire(getApplicationContext()); 
            
            /**
             * Take appropriate action on this message
             * depending upon your app requirement
             * For now i am just displaying it on the screen
             * */
             
            // Showing received message           
//            Toast.makeText(context, "New Message: " + newMessage, Toast.LENGTH_LONG).show();
             
            // Releasing wake lock
            WakeLocker.release();
        }
    };


	private void getFriendsList() {
		new UpdateFriendsListLocalTask(this).execute();
	}

	private void getNoiseHuntState() {
		new GetNoiseHuntStateTask(this).execute();
	}

	private void getAccountInfo() {
		long userID = sp.getLong(MemoryFileNames.USERID, 0L);
		UserDetails userDetails = (UserDetails) ObjectSerializer.deserialize(sp.getString(MemoryFileNames.USERDETAILS, null));
		if(userDetails == null || userID == 0L){
			AccountManager am = AccountManager.get(this);
			Account[] accounts = am.getAccountsByType("com.google");
	
			new GetInfoInForeground(MainActivity.this, accounts[0].name, SCOPE, REQUEST_CODE_RECOVER_FROM_AUTH_ERROR).execute();
			
		}
		else {
			new UpdateLocalProfileDetailsTask(this).execute();
		}
	}

	@TargetApi(14)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		return true;
	}
	
    /**
     * This method is a hook for background threads and async tasks that need to launch a dialog.
     * It does this by launching a runnable under the UI thread.
     */
    public void showErrorDialog(final int code) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
              Dialog d = GooglePlayServicesUtil.getErrorDialog(
                  code,
                  MainActivity.this,
                  REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
              d.show();
            }
        });
    }
    
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    
    private boolean isLocationAvailable(){
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        return service.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    
    @Override
    protected void onDestroy(){
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        try {
            unregisterReceiver(mHandleMessageReceiver);
            GCMRegistrar.onDestroy(this);
        } catch (Exception e) {
            Log.e("UnRegister Receiver Error", "> " + e.getMessage());
        }
        super.onDestroy();
    }
}

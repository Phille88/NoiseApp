package be.kuleuven.noiseapp;

import java.util.ArrayList;

import be.kuleuven.noiseapp.tools.PlaceItem;
import be.kuleuven.noiseapp.tools.PlaceItemAdapter;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class SoundCheckinActivity extends RecordActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listPlaces();
	}
	
	@Override
	protected void setView(){
		setContentView(R.layout.activity_sound_checkin);
	}

	private void listPlaces() {
		ListView listView = (ListView) findViewById(R.id.list_places);
		ArrayList<PlaceItem> values = new ArrayList<PlaceItem>();
		values.add(new PlaceItem("Café De Rector",null,0,0));
		values.add(new PlaceItem("Café Oase",null,0,0));
		values.add(new PlaceItem("De Komeet",null,0,0));
		values.add(new PlaceItem("De Giraf",null,0,0));
		values.add(new PlaceItem("Café Belge",null,0,0));
		values.add(new PlaceItem("Apéro",null,0,0));
		values.add(new PlaceItem("Allegria",null,0,0));
		values.add(new PlaceItem("De Moete",null,0,0));
		values.add(new PlaceItem("De Spuye",null,0,0));
		values.add(new PlaceItem("Libertad",null,0,0));
		
		// Define a new Adapter
		// First parameter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the TextView to which the data is written
		// Forth - the Array of data

		PlaceItemAdapter adapter = new PlaceItemAdapter(this,android.R.id.text1, values);
		
		listView.setAdapter(adapter); 
//		ImageButton img_btn_checkin = (ImageButton) listView..findViewById(R.id.img_btn_checkin);
//		img_btn_checkin.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				 Toast.makeText(getApplicationContext(),
//					      "Click ListItem Number ", Toast.LENGTH_LONG)
//					      .show();
//			}
//			
//		});
		listView.setOnItemClickListener(recordItemButtonListener());
				
//				new OnItemClickListener() {
//			  @Override
//			  public void onItemClick(AdapterView<?> parent, View view,
//			    int position, long id) {
//			    Toast.makeText(getApplicationContext(),
//			      "Click ListItem Number " + position, Toast.LENGTH_LONG)
//			      .show();
//			  }
//		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.sound_checkin, menu);
		return true;
	}

	@Override
	protected void addListenerToRecordButton(){
	}

	protected OnItemClickListener recordItemButtonListener(){
		return new OnItemClickListener(){
		private Thread t;

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {	
			if(isProviderFixed()){
		
				//prepare for a progress bar dialog
				progressBar = new ProgressDialog(v.getContext()){
					@Override
					public void onBackPressed(){
						this.dismiss();
						t.interrupt();
						t = null;
						return;
					}
				};
				progressBar.setCancelable(true);
				progressBar.setMessage("Recording...");
				progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progressBar.setProgress(0);
				progressBar.setMax(100);
				progressBar.show();

				//reset progress bar status
				progressBarStatus = 0;
				
				//create the progress dialog thread
				t = new Thread(new Runnable() {
					
					@Override
					public void run() {
						Thread thisThread = Thread.currentThread();
						while (progressBarStatus < 100 && thisThread == t) {
								// comp is too fast, sleep 1 second
							try {
								Thread.sleep(1000);

							} catch (InterruptedException e) {
								e.printStackTrace();
								progressBarStatus = 0;
							}

							// process some tasks
							progressBarStatus = doSomeTasks();

							// Update the progress bar
							progressBarHandler.post(new Runnable() {
								@Override
								public void run() {
									progressBar.setProgress(progressBarStatus);
								}
							});
						}

						// ok, task is done
						if (progressBarStatus >= 100) {

							// sleep 2 seconds, so that you can see the 100%
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							// close the progress bar dialog
							progressBar.dismiss();
							Thread.currentThread().interrupt();
							Intent i = new Intent(getApplicationContext(),
									RandomRecordPointsActivity.class);
							startActivity(i);
						}
					}
				});
				t.start();
			}
			else
				Toast.makeText(getApplicationContext(), "Wait for the GPS to have a fixed location", Toast.LENGTH_SHORT).show();
		}
	};
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void setActionBarTitle() {
		getActionBar().setTitle(R.string.txt_sound_checkin_name);
	}

	@Override
	protected boolean popupNeeded() {
		return false;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.txt_sound_checkin_name;
	}

	@Override
	protected int getPopupExplanation() {
		return 0;
	}

	@Override
	protected OnClickListener recordButtonListener() {
		return null;
	}

}

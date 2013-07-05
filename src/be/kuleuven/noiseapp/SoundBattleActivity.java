package be.kuleuven.noiseapp;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
import be.kuleuven.noiseapp.soundbattle.GetRandomSoundBattleTask;
import be.kuleuven.noiseapp.soundbattle.LoadSoundBattleTask;
import be.kuleuven.noiseapp.soundbattle.GetAllSoundBattlesTask;
import be.kuleuven.noiseapp.soundbattle.SoundBattleItem;
import be.kuleuven.noiseapp.soundbattle.SoundBattleItemAdapter;
import be.kuleuven.noiseapp.soundbattle.SoundBattleItemAdapter.RowType;
import be.kuleuven.noiseapp.soundbattle.iSoundBattleListItem;

public class SoundBattleActivity extends Activity {

//	private ArrayList<iSoundBattleListItem> openSoundBattles = new ArrayList<iSoundBattleListItem>();
//	private ArrayList<iSoundBattleListItem> pendingSoundBattles = new ArrayList<iSoundBattleListItem>();
//	private ArrayList<iSoundBattleListItem> finishedSoundBattles = new ArrayList<iSoundBattleListItem>();
	private ArrayList<iSoundBattleListItem> soundBattles = new ArrayList<iSoundBattleListItem>();
	private SoundBattleActivity sba;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sba = this;
		setContentView(R.layout.activity_sound_battle);
		setupActionBar();
		
		performSearch();
		
		Button btn_new_game = (Button) findViewById(R.id.btn_new_game); //TODO GUI: make a nice button
		btn_new_game.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				new GetRandomSoundBattleTask(sba).execute();
			}
		});
	}

	private void performSearch() {
		new GetAllSoundBattlesTask(this).execute();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setTitle(R.string.txt_sound_battle_name);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.random_sound_battle, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			Intent homeIntent = new Intent(this, MainActivity.class);
			NavUtils.navigateUpTo(this, homeIntent);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

//	/**
//	 * @param openSoundBattlesList the openSoundBattles to set
//	 */
//	public void setOpenSoundBattles(ArrayList<iSoundBattleListItem> openSoundBattlesList) {
//		this.openSoundBattles = openSoundBattlesList;
//	}
//
//	/**
//	 * @param pendingSoundBattles the pendingSoundBattles to set
//	 */
//	public void setPendingSoundBattles(
//			ArrayList<iSoundBattleListItem> pendingSoundBattles) {
//		this.pendingSoundBattles = pendingSoundBattles;
//	}
//
//	/**
//	 * @param finishedSoundBattles the finishedSoundBattles to set
//	 */
//	public void setFinishedSoundBattles(ArrayList<iSoundBattleListItem> finishedSoundBattles) {
//		this.finishedSoundBattles = finishedSoundBattles;
//	}
	
	public void setSoundBattles(ArrayList<iSoundBattleListItem> soundBattleItems){
		this.soundBattles = soundBattleItems;
	}

	public void listBattles() {
		ListView listView = (ListView) findViewById(R.id.list_sound_battles);
		
		// Define a new Adapter
		// First parameter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the TextView to which the data is written
		// Forth - the Array of data

		final SoundBattleItemAdapter adapter = new SoundBattleItemAdapter(this,android.R.id.text1, soundBattles);
		final SoundBattleActivity sba = this;
		listView.setAdapter(adapter); 
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				SoundBattleItem item = (SoundBattleItem) adapter.getItem(position);
				if(item.getViewType() == RowType.LIST_OPEN_ITEM.ordinal())
					new LoadSoundBattleTask(sba).execute(item.getSoundBattleID());
			}
		});
		
//		final SoundBattleItemAdapter pendingadapter = new SoundBattleItemAdapter(this,android.R.id.text1, pendingSoundBattles);
//		listView.setAdapter(pendingadapter); 
//		
//		final SoundBattleItemAdapter finishedAdapter = new SoundBattleItemAdapter(this,android.R.id.text1,finishedSoundBattles);
//		listView.setAdapter(finishedAdapter); 
	}

}

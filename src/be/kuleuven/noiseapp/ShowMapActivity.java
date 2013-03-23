package be.kuleuven.noiseapp;

import java.util.ArrayList;

import be.kuleuven.noiseapp.noisedatabase.NoiseRecording;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.annotation.TargetApi;
import android.os.Build;

public class ShowMapActivity extends RecordActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_show_map);
		showNoiseRecordings();
		
		Button btn_record = (Button) findViewById(R.id.btn_record);
		btn_record.setVisibility(View.INVISIBLE);
	}
	
	@Override
	protected void addListenerToRecordButton(){
	}

	@Override
	protected OnClickListener recordButtonListener() {
		return null;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void setActionBarTitle() {
		getActionBar().setTitle(R.string.txt_show_map_name);
	}

	@Override
	protected boolean popupNeeded() {
		return false;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.txt_show_map_name;
	}

	@Override
	protected int getPopupExplanation() {
		return 0;
	}
	
	private void showNoiseRecordings(){
		datasource.read();
		super.mMap.clear();
		ArrayList<NoiseRecording> recordings = super.datasource.getAllNoiseRecordings();
		for (NoiseRecording nr : recordings){
			super.mMap.addMarker(nr.getMarker());
		}
		datasource.close();
	}

}

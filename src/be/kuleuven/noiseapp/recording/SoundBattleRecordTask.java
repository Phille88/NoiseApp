package be.kuleuven.noiseapp.recording;

import android.content.Intent;
import android.view.View;
import be.kuleuven.noiseapp.SoundBattleWaitActivity;
import be.kuleuven.noiseapp.location.SoundBattleLocation;
import be.kuleuven.noiseapp.noisedatabase.NoiseRecording;
import be.kuleuven.noiseapp.soundbattle.SaveSoundBattleRecordingTask;
import be.kuleuven.noiseapp.soundbattle.SoundBattleRecordActivity;

public class SoundBattleRecordTask extends RecordingTask {
	
	SoundBattleRecordActivity rActivity;

	public SoundBattleRecordTask(View v, SoundBattleRecordActivity rActivity) {
		super(v, rActivity);
		this.rActivity = rActivity;
	}
	
	@Override
	public void onPostExecute(NoiseRecording result){
		super.onPostExecute(result);
		
		SoundBattleLocation sbl = rActivity.getClosestSoundBattleLocationToRecord();
		sbl.setRecorded(true);
		rActivity.updateMarkers();

		//store state
		new SaveSoundBattleRecordingTask(sbl).execute(result);

		if (rActivity.isEverythingRecorded()) {			
			Intent i = new Intent(rActivity.getApplicationContext(), SoundBattleWaitActivity.class);
			rActivity.startActivity(i);
			rActivity.finish();
		}
		finish();
	}
}

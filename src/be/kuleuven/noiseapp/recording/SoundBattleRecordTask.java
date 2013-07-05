package be.kuleuven.noiseapp.recording;

import java.util.concurrent.ExecutionException;

import android.content.Intent;
import android.view.View;
import be.kuleuven.noiseapp.SoundBattlePointsActivity;
import be.kuleuven.noiseapp.SoundBattleRecordActivity;
import be.kuleuven.noiseapp.location.SoundBattleLocation;
import be.kuleuven.noiseapp.noisedatabase.NoiseRecording;
import be.kuleuven.noiseapp.points.CalculateRecordingPoints;
import be.kuleuven.noiseapp.points.RecordingPoints;
import be.kuleuven.noiseapp.soundbattle.SaveSoundBattleRecordingTask;

public class SoundBattleRecordTask extends RecordingTask {
	
	SoundBattleRecordActivity rActivity;

	public SoundBattleRecordTask(View v, SoundBattleRecordActivity rActivity) {
		super(v, rActivity);
		this.rActivity = rActivity;
	}
	
	@Override
	public void onPostExecute(NoiseRecording result){
		super.onPostExecute(result);
		
		//store points
		RecordingPoints rr = null;
		try {
			rr = new CalculateRecordingPoints().execute(result).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		result.setRecordingPoints(rr);
		
		SoundBattleLocation sbl = rActivity.getClosestSoundBattleLocationToRecord();
		sbl.setRecorded(true);
		rActivity.updateMarkers();

		//store state
		new SaveSoundBattleRecordingTask(sbl).execute(result);

		if (rActivity.isEverythingRecorded()) {
			Intent i = new Intent(rActivity.getApplicationContext(),
					SoundBattlePointsActivity.class);
			rActivity.startActivity(i);
			rActivity.finish();
		}
		finish();
	}
}

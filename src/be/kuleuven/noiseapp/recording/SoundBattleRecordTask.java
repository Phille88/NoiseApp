package be.kuleuven.noiseapp.recording;

import java.util.concurrent.ExecutionException;

import android.content.Intent;
import android.view.View;
import be.kuleuven.noiseapp.SoundBattlePointsActivity;
import be.kuleuven.noiseapp.SoundBattleRecordActivity;
import be.kuleuven.noiseapp.noisedatabase.NoiseRecording;
import be.kuleuven.noiseapp.points.CalculateRecordingPoints;
import be.kuleuven.noiseapp.points.RecordingPoints;

public class SoundBattleRecordTask extends RecordingTask {

	public SoundBattleRecordTask(View v, SoundBattleRecordActivity rActivity) {
		super(v, rActivity);
	}
	
	@Override
	public void onPostExecute(NoiseRecording result){
		super.onPostExecute(result);
		
		RecordingPoints rr = null;
		try {
			rr = new CalculateRecordingPoints().execute(result).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result.setRecordingPoints(rr);

		((SoundBattleRecordActivity) rActivity).getClosestNoiseLocationToRecord()
				.setRecorded(true);

		if (((SoundBattleRecordActivity) rActivity).isEverythingRecorded()) {
			Intent i = new Intent(rActivity.getApplicationContext(),
					SoundBattlePointsActivity.class);
			rActivity.startActivity(i);
			rActivity.finish();
		}
		

//		Bundle b = new Bundle();
//		b.putSerializable("noiseRecording",result);
//		Intent i = new Intent(rActivity.getApplicationContext(),
//				RandomRecordPointsActivity.class);
//		i.putExtras(b);
//		rActivity.startActivity(i);
//		rActivity.finish();
		finish();
	}
}

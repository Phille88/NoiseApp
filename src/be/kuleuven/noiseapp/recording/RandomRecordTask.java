package be.kuleuven.noiseapp.recording;

import java.util.concurrent.ExecutionException;

import android.content.Intent;
import android.view.View;
import be.kuleuven.noiseapp.RandomRecordActivity;
import be.kuleuven.noiseapp.RandomRecordPointsActivity;
import be.kuleuven.noiseapp.noisedatabase.NoiseRecording;
import be.kuleuven.noiseapp.points.CalculateRandomRecordPoints;
import be.kuleuven.noiseapp.points.RecordingPoints;
import be.kuleuven.noiseapp.tools.MemoryFileNames;

public class RandomRecordTask extends RecordingTask {

	public RandomRecordTask(View v, RandomRecordActivity rActivity) {
		super(v, rActivity);
	}
	
	@Override
	public void onPostExecute(NoiseRecording result){
		super.onPostExecute(result);
		RecordingPoints rr = null;
		try {
			rr = new CalculateRandomRecordPoints().execute(result).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		result.setRecordingPoints(rr);
		
		Intent i = new Intent(rActivity.getApplicationContext(), RandomRecordPointsActivity.class);
		i.putExtra(MemoryFileNames.LAST_NOISERECORDING,result);
		rActivity.startActivity(i);
		//rActivity.finish();
		finish();
	}

}

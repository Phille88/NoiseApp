package be.kuleuven.noiseapp.recording;

import java.util.concurrent.ExecutionException;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import be.kuleuven.noiseapp.RandomRecordActivity;
import be.kuleuven.noiseapp.RandomRecordPointsActivity;
import be.kuleuven.noiseapp.noisedatabase.NoiseRecording;
import be.kuleuven.noiseapp.points.CalculateRecordingPoints;
import be.kuleuven.noiseapp.points.RecordingPoints;

public class RandomRecordTask extends RecordingTask {

	public RandomRecordTask(View v, RandomRecordActivity rActivity) {
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
		Bundle b = new Bundle();
		b.putSerializable("noiseRecording",result);
		Intent i = new Intent(rActivity.getApplicationContext(),
				RandomRecordPointsActivity.class);
		i.putExtras(b);
		rActivity.startActivity(i);
		rActivity.finish();
		finish();
	}

}

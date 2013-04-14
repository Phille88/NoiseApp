package be.kuleuven.noiseapp;

import java.io.IOException;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

public class RandomRecordActivity extends RecordActivity {
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	

	@Override
	protected void showPopup() {
		LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View popupView = layoutInflater.inflate(
				R.layout.popup_explanation, null);

		final PopupWindow popupWindow = new PopupWindow(popupView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		TextView txt_title = (TextView) popupView.findViewById(R.id.txt_explanation_title);
		txt_title.setText(R.string.txt_random_record_name);
		TextView txt_desc = (TextView) popupView.findViewById(R.id.txt_explanation);
		txt_desc.setText(R.string.txt_random_record_explanation);

		ImageButton btnDismiss = (ImageButton) popupView
				.findViewById(R.id.btn_popup_explanation_ok);
		btnDismiss.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
			}
		});
		DisplayMetrics metrics = getApplicationContext().getResources()
				.getDisplayMetrics();
		popupWindow.setHeight(metrics.heightPixels);
		popupWindow.setWidth(metrics.widthPixels);
		findViewById(R.id.layout_map_record).post(new Runnable() {
			@Override
			public void run() {
				popupWindow.showAtLocation(
						findViewById(R.id.layout_map_record),
						Gravity.CENTER_HORIZONTAL, 0, 0);
			}
		});
	}


	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void setActionBarTitle() {
		getActionBar().setTitle(R.string.txt_random_record_name);
	}
	
	@Override
	protected OnClickListener recordButtonListener(){
		return new OnClickListener(){
		private Thread t;

		@Override
		public void onClick(View v) {	
			//if(isProviderFixed()){
		
				//prepare for a progress bar dialog
				progressBar = new ProgressDialog(v.getContext()){
					@Override
					public void onBackPressed(){
						this.dismiss();
						t.interrupt();
						t = null;
						return;
					}
					
					@Override
					public boolean onTouchEvent(MotionEvent e){
						this.dismiss();
						t.interrupt();
						t = null;
						return true;
					}
				};
				progressBar.setCancelable(true);
				progressBar.setCanceledOnTouchOutside(true);
				progressBar.setMessage("Recording...");
				progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progressBar.setProgress(0);
				progressBar.setMax(100);
				progressBar.show();

				//reset progress bar status
				progressBarStatus = 0;
				
				//create the progress dialog thread
				t = new Thread(new Runnable() {
					
					private String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audiorecordsample.3gp";

					@Override
					public void run() {
						Thread thisThread = Thread.currentThread();
						ArrayList<Double> dBs =new ArrayList<Double>();
						//ArrayList<Double> amps =new ArrayList<Double>();
						double amp = 0;
						MediaRecorder mRecorder = new MediaRecorder();
						mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
						mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
						mRecorder.setAudioChannels(2);
						mRecorder.setAudioEncodingBitRate(44100);
						mRecorder.setOutputFile(mFileName); 
						mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
						try {
				            mRecorder.prepare();
				        } catch (IOException e) {
				            Log.e("AudioRecordTest", "prepare() failed");
				        }
						mRecorder.start();
						mRecorder.getMaxAmplitude();
						
						while (progressBarStatus < 100 && thisThread == t) {
							for(int i = 0; i<4;i++){
							try {
								Thread.sleep(250);

							} catch (InterruptedException e) {
								e.printStackTrace();
								progressBarStatus = 0;
							}
							amp = mRecorder.getMaxAmplitude();
							dBs.add(20 * Math.log10(amp / 1f));
							//amps.add(amp);
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
							mRecorder.stop();
							mRecorder.reset();
							mRecorder.release();
							mRecorder=null;
							double avgDB = 0;
							//double avgAmp = 0;
							int size = 0;
							for(int i = 0; i < dBs.size(); i++){
								if(!dBs.get(i).isNaN() && !dBs.get(i).isInfinite() && dBs.get(i) != 0){
									avgDB += dBs.get(i);
									//avgAmp += amps.get(i);
									size++;
								}
							}
							avgDB = avgDB/size;
							//avgAmp = avgAmp/size;

							// sleep 2 seconds, so that you can see the 100%
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							// close the progress bar dialog
							progressBar.dismiss();
							addNoiseRecording("demo", currentLocation.getLatitude(), currentLocation.getLongitude(), avgDB, 10);
							
							Thread.currentThread().interrupt();
							Bundle b = new Bundle();
							b.putDouble("dBlevel", avgDB);
							Intent i = new Intent(getApplicationContext(),
									RandomRecordPointsActivity.class);
							i.putExtras(b);
							startActivity(i);
						}
					}
				});
				t.start();
//			}
//			else
//				Toast.makeText(getApplicationContext(), "Wait for the GPS to have a fixed location", Toast.LENGTH_SHORT).show();
		}
	};
	}

	@Override
	protected boolean popupNeeded() {
		return true;
	}
	
	@Override
	protected int getActivityTitle(){
		return R.string.txt_random_record_name;
	}

	@Override
	protected int getPopupExplanation() {
		return R.string.txt_random_record_explanation;
	}
}
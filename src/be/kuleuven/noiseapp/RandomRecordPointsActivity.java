package be.kuleuven.noiseapp;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import be.kuleuven.noiseapp.noisedatabase.NoiseRecording;
import be.kuleuven.noiseapp.points.Badge;
import be.kuleuven.noiseapp.points.Point;
import be.kuleuven.noiseapp.points.RecordingPoints;
import be.kuleuven.noiseapp.tools.MemoryFileNames;

public class RandomRecordPointsActivity extends Activity {
	RecordingPoints rp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_random_record_points);
		// Show the Up button in the action bar.
		setupActionBar();
		
		NoiseRecording nr = (NoiseRecording) getIntent().getSerializableExtra(MemoryFileNames.LAST_NOISERECORDING);
		rp = nr.getRecordingPoints();
		
		TextView txt_dblevel = (TextView) findViewById(R.id.txt_dblevel);
		String dbLevel = new DecimalFormat("#").format(nr.getDB());
		txt_dblevel.setText(dbLevel + " dB");
		txt_dblevel.setTextColor(nr.getLoudnessColor());
		
		createPointDescriptions();
	}

	private void createPointDescriptions() {
		ArrayList<TableRow> tableRows = new ArrayList<TableRow>();
		for(Point p : rp.getPoints()){
			TableRow tr = new TableRow(this);
			TextView descriptionToAdd = new TextView(this);
			descriptionToAdd.setText(p.getDescription());
			
			TextView pointToAdd = new TextView(this);
			pointToAdd.setText("+ " + p.getPoint());
			pointToAdd.setGravity(Gravity.RIGHT);
			
			tr.addView(descriptionToAdd);
			tr.addView(pointToAdd);
			
			tableRows.add(tr);
		}
		
		for(Badge b : rp.getBadges()){
			TableRow tr = new TableRow(this);
			TextView descriptionToAdd = new TextView(this);
			descriptionToAdd.setText(b.getDescription());
			
			TextView pointToAdd = new TextView(this);
			pointToAdd.setText("+ " + b.getPoint());
			pointToAdd.setGravity(Gravity.RIGHT);
			
			tr.addView(descriptionToAdd);
			tr.addView(pointToAdd);

			tableRows.add(tr);
		}
		
		TableRow totalRow = new TableRow(this);
		TableLayout.LayoutParams trlp = new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		trlp.setMargins(0, 15, 0, 0);
		totalRow.setLayoutParams(trlp);

		TextView descriptionToAdd = new TextView(this);
		descriptionToAdd.setText("Total:");
		descriptionToAdd.setTextSize(20);
		descriptionToAdd.setTypeface(null, Typeface.BOLD);
		
		TextView pointToAdd = new TextView(this);
		pointToAdd.setText("+ " + rp.getTotalPoints());
		pointToAdd.setTextSize(20);
		pointToAdd.setTypeface(null, Typeface.BOLD);
		pointToAdd.setGravity(Gravity.RIGHT);
		
		totalRow.addView(descriptionToAdd);
		totalRow.addView(pointToAdd);
		
		tableRows.add(totalRow);
		
		TableLayout tbl_points = (TableLayout) this.findViewById(R.id.tbl_points);
		for(TableRow tr : tableRows)
			tbl_points.addView(tr);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setTitle(R.string.title_activity_random_record_points);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.random_record_points, menu);
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
			//NavUtils.navigateUpFromSameTask(this);
			Intent homeIntent = new Intent(this, MainActivity.class);
			NavUtils.navigateUpTo(this, homeIntent);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}

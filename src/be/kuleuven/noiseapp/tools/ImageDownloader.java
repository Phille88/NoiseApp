package be.kuleuven.noiseapp.tools;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

import be.kuleuven.noiseapp.MainActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

	MainActivity mActivity;
	BigInteger userID;
	private String fileName;
	
	public ImageDownloader(MainActivity activity, BigInteger userID, String fileName) {
		this.mActivity = activity;
		this.userID = userID;
		this.fileName = fileName;
	  }
	
    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
    	FileOutputStream fos;
		try {
			fos = mActivity.openFileOutput(fileName, Context.MODE_PRIVATE);
			result.compress(CompressFormat.PNG, 90, fos); //quality ignored, because of png format
        	fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mActivity = null;
    }
}
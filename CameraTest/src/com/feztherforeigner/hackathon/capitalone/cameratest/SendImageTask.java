package com.feztherforeigner.hackathon.capitalone.cameratest;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;

public class SendImageTask extends AsyncTask<String, Void, InputStream>{

	@Override
	protected InputStream doInBackground(String... params) {
		URL url = null;
		try {
			url = new URL("http://capitalfour.herokuapps.com");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		InputStream in = null;
		try {
			in = new BufferedInputStream(con.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			con.disconnect();
		}
		return in;
	}

}

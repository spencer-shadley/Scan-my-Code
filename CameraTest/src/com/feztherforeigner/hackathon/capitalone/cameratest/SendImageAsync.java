package com.feztherforeigner.hackathon.capitalone.cameratest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.client.ClientProtocolException;

import android.os.AsyncTask;
import android.util.Log;

class SendImageAsync extends AsyncTask<URL, Integer, Long> {
	
	String filePath;
	
	public SendImageAsync(String filePathToPhoto) {
		filePath = filePathToPhoto;
	}
	
    protected Long doInBackground(URL... urls) {
    	
    	try {
			String s = retrievePictureApache();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		return null;
    }

    protected void onProgressUpdate(Integer... progress) {
       
    }

    protected void onPostExecute(Long result) {
       
    }
    
    public String retrievePictureApache() throws ClientProtocolException, IOException {

		//MultipartEntity entity = new MultipartEntity();
		//entity.addPart("file", new FileBody(new File(mCurrentPhotoPath)));

		//		new SendImageTask().execute("http://capitalfour.herokuapps.com");

		// wrap in AsyncTask
		
		URL url = new URL("http://capitalfour.herokuapp.com");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");

		con.setDoInput(true);
		con.setDoOutput(true);
		con.setRequestProperty("Content-Type", "multipart/form-data;boundary=*****");

		OutputStream os = con.getOutputStream();

		DataOutputStream dos = new DataOutputStream(os);
		FileInputStream fileInputStream = new FileInputStream(new File(
				filePath));
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		dos.writeBytes(twoHyphens + boundary + lineEnd);
		dos.writeBytes("Content-Disposition: post-data; name=uploadedfile;filename="
				+ filePath + lineEnd);


		// create a buffer of maximum size

		int bytesAvailable = fileInputStream.available();
		int maxBufferSize = 1000;
		// int bufferSize = Math.min(bytesAvailable, maxBufferSize);
		byte[] buffer = new byte[bytesAvailable];

		// read file and write it into form...

		int bytesRead = fileInputStream.read(buffer, 0, bytesAvailable);

		while (bytesRead > 0) {
			dos.write(buffer, 0, bytesAvailable);
			bytesAvailable = fileInputStream.available();
			bytesAvailable = Math.min(bytesAvailable, maxBufferSize);
			bytesRead = fileInputStream.read(buffer, 0, bytesAvailable);
		}


		// send multipart form data necesssary after file data...

		dos.writeBytes(lineEnd);
		dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

		// close streams
		fileInputStream.close();
		dos.flush();
		dos.close();


		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(con
					.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				Log.e("Dialoge Box", "Message: " + line);
			}
			rd.close();

		} catch (IOException ioex) {
			Log.e("MediaPlayer", "error: " + ioex.getMessage(), ioex);
		}


		/*Bitmap picture = BitmapFactory.decodeFile(mCurrentPhotoPath);

		picture.compress(Bitmap.CompressFormat.JPEG, 100, 
				os);*/

		/*ByteBuffer bf = ByteBuffer.allocate(picture.getWidth()*picture.getHeight()*4); 
		picture.copyPixelsToBuffer(bf); 
		os.write(bf.array());*/

		return readStream(con.getInputStream());


		//		return response.getEntity().getContent().toString();
	}

	private String readStream(InputStream in) {
		BufferedReader reader = null;

		String line = "";
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			line = "";
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return line;
	}
}
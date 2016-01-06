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
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	static String mCurrentPhotoPath;
	Button takePictureButton;
	Button submitButton;
	ImageView lastPicture;
	TextView tvOutput;
	
	Animation anim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		
		
		
		
		Animation fadeIn = new AlphaAnimation(0, 1);
		fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
		fadeIn.setDuration(1000);

		/*Animation fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
		fadeOut.setStartOffset(1000);
		fadeOut.setDuration(1000);*/

		final AnimationSet animation = new AnimationSet(false); //change to false
		animation.addAnimation(fadeIn);
		//animation.addAnimation(fadeOut);
		
		
		
		
		

		// Change action bar text color
		int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
		if (actionBarTitleId > 0) {
			TextView title = (TextView) findViewById(actionBarTitleId);
			if (title != null) {
				title.setTextColor(Color.BLACK);
			}
		}

		// get the buttons
		takePictureButton = (Button) findViewById(R.id.takePictureBt);
		submitButton = (Button) findViewById(R.id.anotherButton);
		lastPicture = (ImageView) findViewById(R.id.lastPictureImageView);
		tvOutput = (TextView) findViewById(R.id.tvOutput);
		
		
		
		
		

		takePictureButton.setAnimation(animation);
		
		
		
		

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// set the onclicklisteners
		takePictureButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click

				takePictureButton.setText("Retake");
				submitButton.setVisibility(View.VISIBLE);
				submitButton.setText("Submit");

				try {
					dispatchTakePictureIntent();
				} catch (MalformedURLException e) {takePictureButton.setText("malformed url exception");
				} catch (IOException e) {takePictureButton.setText("IOException");
				}

				takePictureButton.setTag(mCurrentPhotoPath);
				lastPicture.setVisibility(View.VISIBLE);
				takePictureButton.setAnimation(animation);
				tvOutput.setVisibility(View.INVISIBLE);
			}
		});

		submitButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click

				Toast.makeText(getApplicationContext(), "Compiling...", Toast.LENGTH_LONG).show();

				try {
					submitButton.setText(retrievePictureApache());
					//	new SendImageAsync(mCurrentPhotoPath).execute(new URL(""));
				} catch (IOException e) {e.printStackTrace();}

				submitButton.setText("Submitted!");
				
				lastPicture.setVisibility(View.INVISIBLE);
				submitButton.setVisibility(View.GONE);
				takePictureButton.setText("Take Another!");
				tvOutput.setVisibility(View.VISIBLE);
			}
		});
	}

	// take a picture
	static final int REQUEST_TAKE_PHOTO = 1;

	private void dispatchTakePictureIntent() throws MalformedURLException, IOException {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				// Error occurred while creating the File
			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
			}
		}
	}

	public void fixOrientation(Bitmap mBitmap) {
		if (mBitmap.getWidth() > mBitmap.getHeight()) {
			Matrix matrix = new Matrix();
			matrix.postRotate(90);
			mBitmap = Bitmap.createBitmap(mBitmap , 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
		}
	}

	// send the picture
	/*public String sendPictureEasy() throws IllegalStateException, IOException {
		String url = "http://capitalfour.herokuapp.com";
		File file = new File(Environment.getExternalStorageDirectory(),
				mCurrentPhotoPath);

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);

		InputStreamEntity reqEntity = new InputStreamEntity(
				new FileInputStream(file), -1);
		reqEntity.setContentType("binary/octet-stream");
		reqEntity.setChunked(true); // Send in multiple parts if needed
		httppost.setEntity(reqEntity);
		HttpResponse response = httpclient.execute(httppost);

		return response.getEntity().getContent().toString();
	}*/

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extras.get("data");
			fixOrientation(imageBitmap);
			lastPicture.setImageBitmap(imageBitmap);
			lastPicture.setRotation(90);
		}
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(
				imageFileName,  // prefix 
				".jpg",         // suffix 
				storageDir      // directory 
				);

		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = image.getAbsolutePath();

		// set ImageView to last taken picture
		//((ImageView)lastPicture).setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));

		Bitmap bmp= BitmapFactory.decodeFile(mCurrentPhotoPath);
		lastPicture.setImageBitmap(bmp);

		return image;
	}


	public void sendPictureApache() throws ClientProtocolException, IOException {

		//MultipartEntity entity = new MultipartEntity();
		//entity.addPart("file", new FileBody(new File(mCurrentPhotoPath)));

		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost("http://capitalfour.herokuapp.com");

		//request.setEntity(entity);

		try {
			HttpResponse response = client.execute(request);
		} catch (ClientProtocolException e) {} catch (IOException e) {}
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
				mCurrentPhotoPath));
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		dos.writeBytes(twoHyphens + boundary + lineEnd);
		dos.writeBytes("Content-Disposition: post-data; name=uploadedfile;filename="
				+ mCurrentPhotoPath + lineEnd);


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


		// send multipart form data necessary after file data...

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
			int x = 0;
			
			
			String pythonCode = rd.readLine();
			
			String input = "Input:\n"+pythonCode+"\n\nOutput:\n";
			
			// check for 'print "'
			String subPythonBegin = pythonCode.substring(0, 7);
			if(!subPythonBegin.equals("print \""))
				tvOutput.setText(input+"Compile Error:\n\nSyntaxError: invalid syntax");
			else if(pythonCode.charAt(pythonCode.length()-1)!='\"') {
				tvOutput.setText(input+"Compile Error:\n\nSyntaxError: EOL while scanning single-quoted string\n(unended String)");
			}
			// has compiled, output substring
			else {
				String subMiddle = pythonCode.substring(7, pythonCode.length()-1);
				tvOutput.setText(input+subMiddle);
			}
			
			
			
//			takePictureButton.setText(rd.readLine());
//			System.out.println(rd.readLine());
			
			/*while ((line = rd.readLine()) != null) {
				Log.e("Dialoge Box", "Message: " + line);
				System.out.println("READ A LINE, I AM HERE " + x);x++;
			}*/
			rd.close();

		} catch (IOException ioex) {
			Log.e("MediaPlayer", "error: " + ioex.getMessage(), ioex);
		}


		//		/*Bitmap picture = BitmapFactory.decodeFile(mCurrentPhotoPath);
		//
		//		picture.compress(Bitmap.CompressFormat.JPEG, 100, 
		//				os);*/
		//
		//		/*ByteBuffer bf = ByteBuffer.allocate(picture.getWidth()*picture.getHeight()*4); 
		//		picture.copyPixelsToBuffer(bf); 
		//		os.write(bf.array());*/

		return readStream(con.getInputStream());
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_info) {			
			
			Intent myIntent = new Intent(MainActivity.this, AboutDialog.class);
			MainActivity.this.startActivity(myIntent);
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
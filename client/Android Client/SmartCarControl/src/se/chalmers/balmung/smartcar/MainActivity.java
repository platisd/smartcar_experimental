package se.chalmers.balmung.smartcar;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import se.chalmers.balmung.smartcar.JoystickView;
import se.chalmers.balmung.smartcar.JoystickView.OnJoystickMoveListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

/* written by simeon ivanov and yurou zhao */

public class MainActivity extends Activity {
    private SharedPreferences settings;
    private Editor settingsEditor;
	private Socket socket;
	private String serverPort;
	private String serverIp;	
	private String prevMsg = "";
    private JoystickView joystick;
    private JoystickView cameraJoystick;
    private View centreCameraButton;
    private int picturesTaken;
    private boolean pictureTaken = true;
    private boolean automation;
    private String automationMode;
	ImageView videoView;
    private int displayWidth;
    private int displayHeight;
	Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            new DownloadImageTask(videoView).execute("http://" + serverIp + "/pic.jpg");
            videoView.getLayoutParams().width = displayWidth;
    		videoView.getLayoutParams().height = displayHeight;
            
            timerHandler.postDelayed(this, 40);
        }
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		centreCameraButton = findViewById(R.id.centreCamera);
		videoView = (ImageView) findViewById(R.id.videoView);
		displayWidth = videoView.getLayoutParams().width;
		displayHeight = videoView.getLayoutParams().height;
		
		joystick = (JoystickView) findViewById(R.id.joystickView);
		joystick.setOnJoystickMoveListener(new OnJoystickMoveListener() {

            @Override
            public void onValueChanged(int angle, int power, int direction) {
            	// Scale speed
            	if (angle < -90 || angle > 90) {
            		power *= -1;
            	}
            	
            	// X to Y adapter
            	if (angle > 90) {
            		angle = 180 - angle;
            	} else if (angle < -90) {
            		angle = -(180 + angle);
            	}
                	send("!" + angle + ":" + power + "*");
            }
        }, JoystickView.DEFAULT_LOOP_INTERVAL);
		
		cameraJoystick = (JoystickView) findViewById(R.id.cameraJoystickView);
		cameraJoystick.setOnJoystickMoveListener(new OnJoystickMoveListener() {

            @Override
            public void onValueChanged(int angle, int power, int direction) {
            	// Scale speed
            	if (angle <= -45 || angle >= 135) {
            		power *= -1;
            	}
            	
            	// X to Y adapter
            	if (angle > 90) {
            		angle = 180 - angle;
            	} else if (angle < -90) {
            		angle = -(180 + angle);
            	}
                	send("!" + angle + ":" + power + "~");
            }
        }, JoystickView.DEFAULT_LOOP_INTERVAL);
		
		timerHandler.postDelayed(timerRunnable, 0);	
    }
	
	@Override
	protected void onResume() {
		super.onResume();	
		
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		serverIp = settings.getString("serverIp", "10.0.2.2");
		serverPort = settings.getString("serverPort", "8787");
		picturesTaken = settings.getInt("picturesTaken", 1);
		settingsEditor = settings.edit();
		automation = settings.getBoolean("automation", false);
		automationMode = settings.getString("automationMode", "");
		
		if (automation) {
			joystick.setVisibility(View.GONE);
			cameraJoystick.setVisibility(View.GONE);
			centreCameraButton.setVisibility(View.GONE);
		} else {
			joystick.setVisibility(View.VISIBLE);
			cameraJoystick.setVisibility(View.VISIBLE);
			centreCameraButton.setVisibility(View.VISIBLE);
		}
		
		new Thread(new Connect()).start();
	}
	
	protected void onPause() {
		super.onPause();
		
        settingsEditor.putInt("picturesTaken", picturesTaken);
        settingsEditor.apply();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(this, SettingsActivity.class);
		
//		if (item.getItemId() == R.id.action_gallery) {
//			intent = new Intent(this, GalleryActivity.class);
//		}
		
		startActivity(intent);
		return super.onOptionsItemSelected(item);
	}
		
	public void onCameraCentre(View view) {
		send("centercamera");
	}
	
	public void onTakePicture(View view) {
		if (! pictureTaken) return;
		
		pictureTaken = false;
		
		if (! isExternalStorageReadable() && ! isExternalStorageReadable()) return;
		
		Long currentTime = System.currentTimeMillis() / 1000L; // Long time
		
		// Create pictures directory
		File picturesDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "SmartCar");
		picturesDirectory.mkdirs();
		
		String picturesDirectoryPath = picturesDirectory.getPath();
		String newImagePath = picturesDirectoryPath + File.separator + "smartcar" + currentTime + ".jpg";
		
		File file = new File(newImagePath);
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		Bitmap bitmap = ((BitmapDrawable) videoView.getDrawable()).getBitmap();
		
		if (bitmap == null) {
			Toast.makeText(MainActivity.this, "Video not streaming.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		FileOutputStream fos = null;
		
		try {
	        file.createNewFile();
	        picturesTaken++;
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		
		try {
			fos = new FileOutputStream(file);
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
		
		try {
	        fos.write(bytes.toByteArray());
	        fos.close();
	        Toast.makeText(MainActivity.this, "Image stored: " + newImagePath, Toast.LENGTH_SHORT).show();
	        pictureTaken = true;
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public void send(String message) {
		if (! prevMsg.contains(message)) {
			prevMsg = message;
			
			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())), true);
				out.println(message);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	class Connect implements Runnable {

		@Override
		public void run() {
			try {
				InetAddress serverAddr = InetAddress.getByName(serverIp);
				socket = new Socket(serverAddr, Integer.parseInt(serverPort));
					
				send("stopfollowingsister");
				
				if (automation) {
					if (automationMode == "followsister") {
						send(automationMode);
					} else {
						send("*" + automationMode + "*");
					}
				}
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    ImageView bmImage;

	    public DownloadImageTask(ImageView bmImage) {
	        this.bmImage = bmImage;
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
	        bmImage.setImageBitmap(result);
	    }
	}
	
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

	public boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
}

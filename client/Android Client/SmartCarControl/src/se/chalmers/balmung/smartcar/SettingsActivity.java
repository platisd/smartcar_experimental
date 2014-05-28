package se.chalmers.balmung.smartcar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;
/* written by simeon ivanov and yurou zhao */
public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        // Get the Raspberry Pi's IP.
        if (! preferences.contains("serverIp")) {
        	Editor preferencesEditor = preferences.edit();
        	preferencesEditor.putString("serverIp", connectedDeviceIp());
        	preferencesEditor.apply();
        	
        	// Refresh the current activity to get the updated preferences.
        	finish();
        	startActivity(getIntent());
        }
    }	
    
    public String connectedDeviceIp(){
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("/proc/net/arp"));
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				String[] tokens = inputLine.split(" +");
				if (tokens != null && tokens[0].length() > 7 && tokens[3].equalsIgnoreCase("e8:4e:06:19:c1:1f")){
					//if it's an ip and then the mac address of our car, use it
					return tokens[0];
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				br.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		return "192.168.";
	}
}
package SmartCarJavaClient;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import carMapping.FileParser;
import carMapping.MapLayer;
import carMapping.ReadSocket;
import carMapping.SonarData;

/*************
 * this is the main class for smartcar java client
 * @author lijiaxin
 *this class instantiate the SmartCarGui class which is the main user interface ,and Setting window for user to change the preferences. 
 */
public class testrun {
	public static joystick.ClientSocket userOutput = null; // socket for user to communicate with control unit.
	public static MapLayer mapLayer; // the panel for display the data sent back from the control unit.  
	static URL url = null;// url for image feed back 
	public static SmartCarGui gui;// main user interface
	public static SettingWindow setting ;// Setting window for user to change the preferences
	public static boolean connection = false; // flag for indicate the conection status
	public static boolean stream = false;// flag for indicate the image feedback stream status
	public static ArrayList<SonarData> packages ;	
	public static void main(String[] args) {
		//userOutput = new ClientOutput("192.168.43.10", 8787);//"127.0.0.1" //  192.168.1.101
		//url =  new URL("https://github.com/platisd/smartcar/blob/master/pictures/smartcar_prototype_view.jpg?raw=true");
	
		File file = new File("sonar.txt");
		// test for repaint, parses, ready for use.
		/*
					try {
						packages = new FileParser(file).read();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			mapLayer = new MapLayer (packages);
		*/	
			packages = new ArrayList<SonarData>();	
			
			
			mapLayer = new MapLayer ();
		
		
		 gui = new SmartCarGui();
		 setting = new SettingWindow();
		gui.setSize(1280, 720);
		gui.setLocationRelativeTo(null);
		setting.setSize(400, 300);
		setting.setLocationRelativeTo(null);
		//Main.pack();
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setVisible(true);
	
	
	}
}

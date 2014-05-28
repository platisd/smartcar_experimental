package carMapping;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
/****************
 * simple test case main program for mapLayer class.
 * @author lijiaxin
 *
 */

public class SelfMappingMain {
	public static void main(String[] args) {
		
		ArrayList<SonarData> packages = null ;	
		File file = new File("sonar.txt");
		// test for repaint, pass, ready for use.
					try {
						packages = new FileParser(file).read();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			MapLayer mapLayer = new MapLayer (packages);
			
			
			JFrame frame = new JFrame("Smart Car Exploring Map");// Create a frame
			frame.setSize(600,600);// Set the frame size
			// Center a frame
			frame.add(mapLayer);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
			frame.setVisible(true);// Display the frame
			
			
			
		 }
			
}

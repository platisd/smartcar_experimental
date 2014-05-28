import java.io.*;
import java.net.Socket;

import javax.swing.JFrame;
/* written by jiaxin li and shanlin tong */
public class SmartCarControlMAIN {
	
	public static void main(String[] args) {
		ClientOutput userOutput = new ClientOutput();
		ControlPanel cPanel = new ControlPanel(userOutput);
		cPanel.setTitle("SmartCar Client");
		cPanel.setSize(250,250);
		cPanel.setLocationRelativeTo(null);
		cPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		cPanel.setVisible(true);
	}
}

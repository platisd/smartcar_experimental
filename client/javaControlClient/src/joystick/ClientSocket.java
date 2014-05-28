package joystick;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

import SmartCarJavaClient.controlPanel;
import SmartCarJavaClient.testrun;

/* written by dimitrios platis */

public class ClientSocket {
	public Socket socket;
	public PrintWriter out;
	public BufferedReader in;
	public ClientSocket(String host , int port){
		try { //192.168.1.100     "127.0.0.1"
			socket = new Socket(host, port);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			send2pi ("client connect");		
			SmartCarJavaClient.SettingWindow.btnconnect.setText("disConnect");
			controlPanel.lblStatus.setForeground(Color.green);
			controlPanel.lblStatus.setText("connected");
			testrun.connection = true;
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null,
				    "Connection Error !",
				    "Connection Error",
				    JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void send2pi (String GUIinput){
		System.out.println(GUIinput);
		out.println(GUIinput);
	}
	
	public void send2pi (int[] GUIinput){
		System.out.println(GUIinput);
		out.println(GUIinput);
	}
	
	public void close(){
		out.close();
	}
}

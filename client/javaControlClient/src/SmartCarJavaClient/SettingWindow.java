package SmartCarJavaClient;

import javax.swing.JFrame;

import java.awt.Toolkit;

import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.prefs.Preferences;

import javax.swing.JList;

import java.awt.Dimension;

import javax.swing.JSlider;

import carMapping.ReadSocket;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
/* written by jiaxin li and shanlin tong */
public class SettingWindow extends JFrame {
	private JTextField textFieldIP;
	private JTextField textFieldPort;
	public static JButton btnconnect;
	public static JButton btnstream;
	
	// Retrieve the user preference node for the package com.mycompany
	Preferences prefs = Preferences.userNodeForPackage(SettingWindow.class);
	static final String DEFAULT_IP = "127.0.0.1";//192.168.1.101   127.0.0.1  192.168.43.10
	static final int DEFAULT_PORT = 22;
	// Preference key name
	//final String PREF_NAME = "IP";

	// Set the value of the preference
	//String newValue = "a string";
	//prefs.put (PREF_NAME, newValue);

	// Get the value of the preference;
	// default value is returned if the preference does not exist
	//String defaultValue = "default string";
	//String propertyValue = prefs.get(PREF_NAME, defaultValue); // "a string"
	private JTextField txtHttp;
	
	
	
	
	public SettingWindow() {
		prefs.put ("IP","192.168.43.10");
		prefs.putInt ("Port",8787);
		
		setTitle("Settings");
		setIconImage(Toolkit.getDefaultToolkit().getImage("car.jpg"));
		getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panelconnect = new JPanel();
		panelconnect.setToolTipText("Connection Settings");
		panelconnect.setBorder(new LineBorder(new Color(0, 0, 0)));
		getContentPane().add(panelconnect);
		panelconnect.setLayout(new GridLayout(3, 2, 0, 4));
		
		JLabel lblIP = new JLabel("IP Address :");
		panelconnect.add(lblIP);
		
		textFieldIP = new JTextField();
		textFieldIP.setText(prefs.get("IP", DEFAULT_IP));
		panelconnect.add(textFieldIP);
		textFieldIP.setColumns(10);
		
		JLabel lblPort = new JLabel("Port :");
		panelconnect.add(lblPort);
		
		textFieldPort = new JTextField();
		panelconnect.add(textFieldPort);
		textFieldPort.setText(Integer.toString(prefs.getInt("Port", DEFAULT_PORT)));
		textFieldPort.setColumns(4);
		
		JLabel lblNewLabel = new JLabel("");
		panelconnect.add(lblNewLabel);
		
		btnconnect = new JButton("Connect");
		btnconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				prefs.put ("IP",textFieldIP.getText());
				prefs.putInt ("Port",Integer.parseInt(textFieldPort.getText()));
				
				if(testrun.connection){
					testrun.userOutput.close();
					controlPanel.lblStatus.setForeground(Color.red);
					controlPanel.lblStatus.setText("Not connected");
					btnconnect.setText("Connect");
					testrun.connection = false;					
				}
				else{
				testrun.userOutput = new joystick.ClientSocket(prefs.get("IP", DEFAULT_IP), prefs.getInt("Port", DEFAULT_PORT));
				joystick.JoystickPanel.out = testrun.userOutput;
				ReadSocket rsk = new ReadSocket( testrun.userOutput);	
				Thread rst = new Thread(rsk);
				rst.start();
				}
			}
		});
		panelconnect.add(btnconnect);
		
		JPanel panelMap = new JPanel();
		panelMap.setBorder(new LineBorder(new Color(0, 0, 0)));
		getContentPane().add(panelMap);
		
		JLabel lblMapSize = new JLabel("Map settings");
		panelMap.add(lblMapSize);
		
		JPanel panelStream = new JPanel();
		panelStream.setBorder(new LineBorder(new Color(0, 0, 0)));
		getContentPane().add(panelStream);
		panelStream.setLayout(new GridLayout(3, 2, 0, 4));
		
		JLabel lblImageUrl = new JLabel("Image URL :");
		panelStream.add(lblImageUrl);
		
		txtHttp = new JTextField();
		txtHttp.setText("http://"+prefs.get("IP", DEFAULT_IP)+"/pic.jpg");
		txtHttp.setPreferredSize(new Dimension(6, 10));
		panelStream.add(txtHttp);
		txtHttp.setColumns(20);
		
		JLabel lblNewLabel_2 = new JLabel("Stream Rate : ");
		panelStream.add(lblNewLabel_2);
		
		JSlider slider = new JSlider();
		
		slider.setMaximum(1000);
		slider.setMinimum(30);
		slider.setValue(500);
		panelStream.add(slider);
		
		JLabel lblNewLabel_1 = new JLabel("");
		panelStream.add(lblNewLabel_1);
		
		btnstream = new JButton("Start Streaming");
		btnstream.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(testrun.stream){
					testrun.url = null;
					testrun.stream = false;
					btnstream.setText("Start Streaming");
				}
				else{
					try {
						//url =  new URL("https://github.com/platisd/smartcar/blob/master/pictures/smartcar_prototype_view.jpg?raw=true");
						testrun.url =  new URL(txtHttp.getText());
						
						testrun.stream = true;
						btnstream.setText("Stop Streaming");
				//		File f = new File (url.toURI());
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						JOptionPane.showMessageDialog(null,"URL Error !", "URL Error", JOptionPane.ERROR_MESSAGE);
					} 
				}
			}
		});
		panelStream.add(btnstream);
	}

}

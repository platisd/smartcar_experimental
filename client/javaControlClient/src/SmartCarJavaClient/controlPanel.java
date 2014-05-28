package SmartCarJavaClient;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicArrowButton;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.FlowLayout;
import java.awt.SystemColor;
import java.io.File;
import java.io.IOException;

import joystick.JoystickPanel;


import javax.swing.JRadioButton;
import javax.swing.UIManager;
/* written by jiaxin li and shanlin tong */
public class controlPanel extends JPanel {
	
	public static JLabel lblStatus;
	
	public controlPanel() {
		setBackground(UIManager.getColor("Button.background"));
		
		setPanel();
		
		this.revalidate();
		this.repaint();
	}
	
	private void setPanel() {
		
		//Icon centerIcon = new ImageIcon("center_icon.jpg");
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {180, 240};
		gridBagLayout.rowHeights = new int[] {180, 60, 120};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0};
		setLayout(gridBagLayout);
		JPanel CameraJoystickPanel = new JPanel();  // to hold camera joystick 
		
		
		//to set up camera joystick
		CameraJoystickPanel.setLayout(new GridLayout(3, 3, 0, 0));
		
		BasicArrowButton btnR = new BasicArrowButton(BasicArrowButton.EAST);
		btnR.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO To rightward camera 
				if(testrun.connection){
					
						testrun.userOutput.send2pi("*hor10*");
					
					
				}
			}
			
		});
		
		BasicArrowButton btnL = new BasicArrowButton(BasicArrowButton.WEST);
		btnL.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO To leftward camera 
				if(testrun.connection){
					
						testrun.userOutput.send2pi("*hor-10*");
					
					
				}
			}
			
		});
		
		JLabel label = new JLabel("");
		CameraJoystickPanel.add(label);
		
		BasicArrowButton btnUp_1 = new BasicArrowButton(BasicArrowButton.NORTH);
		btnUp_1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO To upward camera  
				if(testrun.connection){
					
						testrun.userOutput.send2pi("*ver10*");
					
					
				}
			}
			
		});
		CameraJoystickPanel.add(btnUp_1);
		
		JLabel label_1 = new JLabel("");
		CameraJoystickPanel.add(label_1);
		CameraJoystickPanel.add(btnL);
		
		JButton btncentrlcam = new JButton("");
		btncentrlcam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(testrun.connection){
					
					
					testrun.userOutput.send2pi("centercamera");
				}
			}
		});
		btncentrlcam.setBackground(SystemColor.controlHighlight);
		btncentrlcam.setIcon(new ImageIcon("aim.png"));
		CameraJoystickPanel.add(btncentrlcam);
		CameraJoystickPanel.add(btnR);
		
		JLabel label_2 = new JLabel("");
		CameraJoystickPanel.add(label_2);
		
		
		BasicArrowButton btnDown_1 = new BasicArrowButton(BasicArrowButton.SOUTH);
		btnDown_1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO To downward camera 
				if(testrun.connection){
					
						testrun.userOutput.send2pi("*ver-10*");
					
					
				}
			}
			
		});
		CameraJoystickPanel.add(btnDown_1);
		
		JPanel ArrowPanel = new JPanel();
		
		
		
		
		//to set up arrow panel for manual navigation 
		ArrowPanel.setLayout(new GridLayout(3, 3, 0, 0));
		
		BasicArrowButton btnUp = new BasicArrowButton(BasicArrowButton.NORTH);
		btnUp.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO To drive forward
				if(testrun.connection){
					testrun.userOutput.send2pi("*go10*");
				}
			}
			
		});
		
		JButton btnpark = new JButton("");
		btnpark.setBackground(SystemColor.controlHighlight);
		btnpark.setIcon(new ImageIcon("park.png"));
		ArrowPanel.add(btnpark);
		ArrowPanel.add(btnUp);
		
		BasicArrowButton btnLeft = new BasicArrowButton(BasicArrowButton.WEST);
		btnLeft.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO To drive leftward
				if(testrun.connection){
					testrun.userOutput.send2pi("*turn-90*");
				}
			}
			
		});
		
		JButton btnlane = new JButton("");
		btnlane.setBackground(SystemColor.controlHighlight);
		btnlane.setIcon(new ImageIcon("lane.png"));
		ArrowPanel.add(btnlane);
		ArrowPanel.add(btnLeft);
		
		JButton btnscan = new JButton("");
		btnscan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(testrun.connection){
					testrun.userOutput.send2pi("*scan*");
				}
			}
		});
		btnscan.setBackground(SystemColor.controlHighlight);
		btnscan.setIcon(new ImageIcon("scan.png"));
		ArrowPanel.add(btnscan);
		
		BasicArrowButton btnRight = new BasicArrowButton(BasicArrowButton.EAST);
		btnRight.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO To drive rightward
				if(testrun.connection){
					testrun.userOutput.send2pi("*turn90*");
				}
			}
			
		});
		ArrowPanel.add(btnRight);
		
		JButton btnexplore = new JButton("");
		btnexplore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(testrun.connection){
					testrun.userOutput.send2pi("*explore*");
				}
			}
		});
		btnexplore.setIcon(new ImageIcon("explore.png"));
		btnexplore.setBackground(SystemColor.controlHighlight);
		ArrowPanel.add(btnexplore);
		GridBagConstraints gbc_ArrowPanel = new GridBagConstraints();
		gbc_ArrowPanel.fill = GridBagConstraints.BOTH;
		gbc_ArrowPanel.insets = new Insets(0, 0, 5, 5);
		gbc_ArrowPanel.gridx = 0;
		gbc_ArrowPanel.gridy = 0;
		add(ArrowPanel, gbc_ArrowPanel);
		
		BasicArrowButton btnDown = new BasicArrowButton(BasicArrowButton.SOUTH);
		btnDown.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO To drive backward
				if(testrun.connection){
					testrun.userOutput.send2pi("*go-10*");
				}
			}
			
		});
		ArrowPanel.add(btnDown);
		
		JButton btnstop = new JButton("");
		btnstop.setIcon(new ImageIcon(controlPanel.class.getResource("/com/sun/java/swing/plaf/windows/icons/Error.gif")));
		btnstop.setBackground(SystemColor.controlHighlight);
		ArrowPanel.add(btnstop);
		
		JoystickPanel joystickPanel = new JoystickPanel(new Dimension(260, 260), SmartCarJavaClient.testrun.userOutput);
		joystickPanel.setMinimumSize(new Dimension(260,260));
		joystickPanel.setVisible(true);
		joystickPanel.revalidate();
		joystickPanel.repaint();
		GridBagConstraints gbc_joystickPanel = new GridBagConstraints();
		gbc_joystickPanel.gridheight = 2;
		gbc_joystickPanel.insets = new Insets(0, 0, 5, 0);
		gbc_joystickPanel.gridx = 1;
		gbc_joystickPanel.gridy = 0;
		add(joystickPanel, gbc_joystickPanel);
		GroupLayout gl_joystickPanel = new GroupLayout(joystickPanel);
		gl_joystickPanel.setHorizontalGroup(
			gl_joystickPanel.createParallelGroup(Alignment.LEADING)
				.addGap(0, 260, Short.MAX_VALUE)
		);
		gl_joystickPanel.setVerticalGroup(
			gl_joystickPanel.createParallelGroup(Alignment.LEADING)
				.addGap(0, 248, Short.MAX_VALUE)
		);
		joystickPanel.setLayout(gl_joystickPanel);
		GridBagConstraints gbc_CameraJoystickPanel = new GridBagConstraints();
		gbc_CameraJoystickPanel.fill = GridBagConstraints.BOTH;
		gbc_CameraJoystickPanel.insets = new Insets(0, 0, 0, 5);
		gbc_CameraJoystickPanel.gridheight = 2;
		gbc_CameraJoystickPanel.gridx = 0;
		gbc_CameraJoystickPanel.gridy = 1;
		add(CameraJoystickPanel, gbc_CameraJoystickPanel);
		
		JButton btncam = new JButton("");
		btncam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(testrun.stream){
					String filename = Integer.toString((int)(System.currentTimeMillis() / 1000L));
					System.out.println(filename);
					File outputfile = new File(filename+".jpg");
					//"D:lijiaxin/"+
//					if(!outputfile.exists()){
//						try {
//							outputfile.createNewFile();
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
					
//					}
				
					try {
						outputfile.createNewFile();
						ImageIO.write(videoPanel.bufImg, "jpg", outputfile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						JOptionPane.showMessageDialog(null,"Can not capture the picture !", "Picture Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				
				
			}
		});
		btncam.setBackground(SystemColor.controlHighlight);
		btncam.setIcon(new ImageIcon("camera.png"));
		CameraJoystickPanel.add(btncam);
		JPanel modepanel = new JPanel();
		modepanel.setVisible(true);
		GridBagConstraints gbc_modepanel = new GridBagConstraints();
		gbc_modepanel.anchor = GridBagConstraints.WEST;
		gbc_modepanel.fill = GridBagConstraints.VERTICAL;
		gbc_modepanel.gridx = 1;
		gbc_modepanel.gridy = 2;
		add(modepanel, gbc_modepanel);
		modepanel.setLayout(new GridLayout(2, 2, 0, 0));
		
		JRadioButton rdbtnauto = new JRadioButton("Auto Mode");
		modepanel.add(rdbtnauto);
		
		JRadioButton rdbtnmanual = new JRadioButton("Manual Mode");
		modepanel.add(rdbtnmanual);
		ButtonGroup bg = new ButtonGroup();
		bg.add(rdbtnauto);
		bg.add(rdbtnmanual);
		
		JLabel lblConnection = new JLabel("Connection ");
		modepanel.add(lblConnection);
		
		lblStatus= new JLabel(""); //lblStatus.setForeground(Color.RED);
		
		lblStatus.setForeground(Color.red);
		lblStatus.setText("Not connected");
		
		//lblStatus.setBackground(Color.RED);
		modepanel.add(lblStatus);
		this.repaint();
	
	}
}

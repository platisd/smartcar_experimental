package SmartCarJavaClient;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JMenuItem;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.FlowLayout;
import java.io.IOException;

import carMapping.MapLayer;
import java.awt.Toolkit;
/* written by jiaxin li and shanlin tong */
public class SmartCarGui extends JFrame {
	
	
	private videoPanel videoPanel;
	private controlPanel controlPanel;
	
	public SmartCarGui() {
		setIconImage(Toolkit.getDefaultToolkit().getImage("car.jpg"));
		setTitle("Smart Car Control -- V1.0.0");
		
		
		
		setContent();

		
		
	}
	
	private void setContent() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnOption = new JMenu("Option");
		menuBar.add(mnOption);
		
		JCheckBoxMenuItem chckbxmntmExit = new JCheckBoxMenuItem("Exit");
		chckbxmntmExit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
					System.exit(0);
			}
			
		});
		
		JMenuItem mntmSettings = new JMenuItem("Settings");
		mnOption.add(mntmSettings);
		mntmSettings.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
					testrun.setting.setVisible(true);
			}
			
		});
		
		JMenuItem mntmSave = new JMenuItem("Save map");
		mnOption.add(mntmSave);
		
		JMenuItem mntmsavedata = new JMenuItem("Save Data");
		mnOption.add(mntmsavedata);
		mnOption.add(chckbxmntmExit);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {600, 320};
		gridBagLayout.rowHeights = new int[] {240, 360};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0};
		getContentPane().setLayout(gridBagLayout);
		
//		//MapPanel mapLayer = new MapPanel();
//		mappannel= new MapPanel();
//		GroupLayout gl_mappannel = new GroupLayout(mappannel);
//		gl_mappannel.setHorizontalGroup(
//			gl_mappannel.createParallelGroup(Alignment.LEADING)
//				//.addComponent(mapLayer, GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE)
//		);
//		gl_mappannel.setVerticalGroup(
//			gl_mappannel.createParallelGroup(Alignment.LEADING)
//				//.addComponent(mapLayer, GroupLayout.DEFAULT_SIZE, 609, Short.MAX_VALUE)
//		);
//		mappannel.setLayout(gl_mappannel);
		
		MapLayer mapLayer = testrun.mapLayer;
		GridBagConstraints gbc_mapLayer = new GridBagConstraints();
		gbc_mapLayer.gridheight = 2;
		gbc_mapLayer.insets = new Insets(0, 0, 5, 5);
		gbc_mapLayer.fill = GridBagConstraints.BOTH;
		gbc_mapLayer.gridx = 0;
		gbc_mapLayer.gridy = 0;
		getContentPane().add(mapLayer, gbc_mapLayer);
		
			videoPanel = new videoPanel();
		
		GridBagConstraints gbc_videoPanel = new GridBagConstraints();
		gbc_videoPanel.fill = GridBagConstraints.BOTH;
		gbc_videoPanel.insets = new Insets(0, 0, 5, 0);
		gbc_videoPanel.gridx = 1;
		gbc_videoPanel.gridy = 0;
		getContentPane().add(videoPanel, gbc_videoPanel);
		videoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		controlPanel = new controlPanel();
		GridBagConstraints gbc_controlPanel = new GridBagConstraints();
		gbc_controlPanel.fill = GridBagConstraints.BOTH;
		gbc_controlPanel.gridx = 1;
		gbc_controlPanel.gridy = 1;
		getContentPane().add(controlPanel, gbc_controlPanel);

	}
	
	}


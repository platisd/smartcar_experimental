import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/* written by jiaxin li and shanlin tong */
public class ControlPanel extends JFrame implements MouseListener, ChangeListener, ActionListener{
	JSlider slider;
	JButton btnLeft, btnRight;
	Timer buttonTimer = new Timer(80,this);
	String outputValue = "";
	ClientOutput usersOutput;
	public ControlPanel(ClientOutput userOutput){
		this.usersOutput = userOutput;
		JPanel mainPanel = new JPanel();
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout(0, 0));

		JPanel threeButtonsPanel = new JPanel();
		mainPanel.add(threeButtonsPanel, BorderLayout.CENTER);
		threeButtonsPanel.setLayout(new BorderLayout(0, 0));

		JPanel leftPanel = new JPanel();
		threeButtonsPanel.add(leftPanel, BorderLayout.WEST);
		leftPanel.setLayout(new BorderLayout(0, 0));

		btnLeft = new JButton("LEFT");
		leftPanel.add(btnLeft);
		btnLeft.addMouseListener(this);

		JPanel speedPanel = new JPanel();
		threeButtonsPanel.add(speedPanel, BorderLayout.CENTER);
		speedPanel.setLayout(new BorderLayout(0, 0));
		slider = new JSlider();
		slider.addChangeListener(this);
		slider.setValue(0);
		slider.setMinimum(-1);
		slider.setMaximum(1);
		slider.setOrientation(SwingConstants.VERTICAL);
		speedPanel.add(slider);

		JPanel rightPanel = new JPanel();
		threeButtonsPanel.add(rightPanel, BorderLayout.EAST);
		rightPanel.setLayout(new BorderLayout(0, 0));

		btnRight = new JButton("RIGHT");
		btnRight.addMouseListener(this);
		rightPanel.add(btnRight);

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == slider){
			if (!slider.getValueIsAdjusting()){
				int sliderValue = slider.getValue();
				if (sliderValue == 0){
					usersOutput.send2pi("stop");
				}else if( sliderValue == 1){
					usersOutput.send2pi("forward");
				}else if (sliderValue == -1){
					usersOutput.send2pi("backward");
				}
			}
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getSource() == btnLeft){
			outputValue = "left";
			buttonTimer.start();
		}
		if (e.getSource() == btnRight){
			outputValue = "right";
			buttonTimer.start();
		}



	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if ((e.getSource() == btnLeft) || (e.getSource() == btnRight)){
			buttonTimer.stop();
			usersOutput.send2pi("middle");
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		usersOutput.send2pi(outputValue);

	}
}

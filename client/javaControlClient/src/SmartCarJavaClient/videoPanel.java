package SmartCarJavaClient;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JLabel;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
/* written by jiaxin li and shanlin tong */

public class videoPanel extends JPanel  implements ActionListener {
	int count =0 ;
	//private ImageComponent img  = new ImageComponent(testrun.url);
	
	public static BufferedImage bufImg;
	ImageIcon pic;
	JLabel lblimg  ;
	static Timer refresh ;
	public videoPanel()  {
		refresh = new Timer(100,this); 
		setPanel();

		refresh.start();
	}
	
	private void setPanel() {
		//setBackground(Color.DARK_GRAY);
		setBackground(UIManager.getColor("Button.background"));
		setLayout(null);		
		pic = getpic();
		lblimg = new JLabel(pic);		
		add(lblimg);

		//add(img);
		
	}
	
	private ImageIcon getpic(){
		if (testrun.stream){
			//return new ImageIcon(testrun.url);
			
				try {
					bufImg=ImageIO.read(testrun.url);
					return new ImageIcon(bufImg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			
		}
		else{
			return new ImageIcon(videoPanel.class.getResource("/javax/swing/plaf/basic/icons/image-failed.png"));
		}
		return pic;
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == refresh){
			
			pic = getpic();
				pic.getImage().flush();
				lblimg.setIcon(pic);
				
			}
			
			
			
		}
	


public  void setRefreshrate(int rate){
	refresh = new Timer(rate,this);
	refresh.start();
	}
}



/*
class ImageComponent extends JComponent {
    private  BufferedImage img = null;

    public ImageComponent(URL url)  {
        try {
			img = ImageIO.read(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));

    }
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), this);

    }
}
*/
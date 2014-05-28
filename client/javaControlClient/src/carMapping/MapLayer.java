package carMapping;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
	/*********************
	 * The Jpanel for displaing map with the feedback from Sensors. 
	 * @author lijiaxin
	 *
	 */
// define clockwise positive counter-clock wise negative , sonar from 0 - 180 ,car orientation from -180 - 180
public class MapLayer extends JPanel {
	
	int xCenter;
	int yCenter;
	Graphics2D g2;
	Graphics g;
	public static ArrayList<SonarData> dataList = new ArrayList<SonarData>();
	
	static final int Radius = 10;
	public MapLayer (){
		this.setSize(600, 600);
		this.setBackground(Color.DARK_GRAY);
		xCenter = getWidth() / 2;
		yCenter = getHeight() / 2;
		
	}
	
	public MapLayer (ArrayList<SonarData> dataList){
		this();
		this.dataList = dataList;
		
	}
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			this.g = g;
			g2 = (Graphics2D) g;
			for(SonarData sd : dataList){
				paintSonarData (sd);
			}
			 
			paintCar ();			
		}
		
		public void paintSonarData (SonarData sd ){
			g2.setColor(Color.WHITE);
			if(sd.distsnce < 0 ||sd.distsnce> SonarData.MaxDistance ){ // no object found in the range
				g2.fillArc(xCenter+ sd.xcoordinate - sd.MaxDistance/2,yCenter - sd.ycoordinate - sd.MaxDistance/2, sd.MaxDistance,sd.MaxDistance,  -sd.Orientation-sd.BeamAngle/2, sd.BeamAngle);
			}
			else{
			g2.fillArc(xCenter+ sd.xcoordinate - sd.distsnce/2,yCenter - sd.ycoordinate - sd.distsnce/2, sd.distsnce,sd.distsnce,  -sd.Orientation-sd.BeamAngle/2, sd.BeamAngle);
			//System.out.println(sd.Orientation-sd.BeamAngle);
			//g2.setColor(Color.BLACK);
			//g2.setStroke(new BasicStroke(5));
			//g2.drawArc(xCenter+ sd.xcoordinate - sd.distsnce/2,yCenter - sd.ycoordinate - sd.distsnce/2, sd.distsnce,sd.distsnce, -sd.Orientation-sd.BeamAngle/2, sd.BeamAngle);
			}
		}
		private void paintCar (){
			int carx = xCenter;
			int cary = yCenter;
			int carOrient = 0;
			int sonarOrient = 0;
			int xStart,yStart,xEnd,yEnd;
			if (dataList.size() != 0){
			carx = dataList.get(dataList.size()-1).xcoordinate + xCenter ;
			cary = yCenter -(dataList.get(dataList.size()-1).ycoordinate) ;
			carOrient =  dataList.get(dataList.size()-1).carOrientation;
			sonarOrient = dataList.get(dataList.size()-1).Orientation;
			}
			g2.setStroke(new BasicStroke(2));			
			
			g2.setColor(Color.RED);
			g2.drawOval(carx - Radius/2, cary- Radius/2 , Radius, Radius);
			g2.setColor(Color.BLUE);
			xStart = (int) (Math.cos(Math.toRadians(carOrient)) * -2*Radius) + carx;
			yStart = yEnd = cary -(int) (Math.sin(Math.toRadians(carOrient)) * 2*Radius);
			xEnd = (int) (Math.cos(Math.toRadians(carOrient)) * 2*Radius) + carx;
			yEnd = cary + (int) (Math.sin(Math.toRadians(carOrient)) * 2*Radius);
			g2.drawLine( xStart ,  yStart , xEnd , yEnd );// carx - 2*Radius ,  cary , carx + 2*Radius, cary
			g2.setColor(Color.GREEN);
			// x = (int) Math.sin(Math.toRadians(carOrient)) * 2*Radius + carx;
			// y = cary -(int) Math.cos(Math.toRadians(carOrient)) * 2*Radius;
			 xEnd = (int) (Math.sin(Math.toRadians(carOrient)) * 2*Radius) + carx;
			 yEnd = cary -(int) (Math.cos(Math.toRadians(carOrient)) * 2*Radius);
			g2.drawLine(xEnd ,  yEnd , carx , cary );//carx , cary - 2*Radius , carx , cary
			g2.setColor(Color.RED);
			//g2.drawLine();
		}
		public void  updateData(){
			paintComponent(g);
		}
		
}

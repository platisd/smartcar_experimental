package carMapping;
import java.io.Serializable;
/*********************
 * The class pack the sensor data as objects.
 * @author lijiaxin
 *
 */

public class SonarData implements Serializable {
	public static final int Nr = 5;
	public int distsnce;
	public int xcoordinate;
	public int ycoordinate;
	public int Orientation;
	public int carOrientation;
	public static final int BeamAngle = 20; 
	public static final int MaxDistance = 100;
	public static final int Datasize = 5;
	public SonarData(int distsnce, int sonarOrientation,  int xcoordinate , int ycoordinate, int carOrientation ){
		this.distsnce =  distsnce;
		this.Orientation = -sonarOrientation + carOrientation;
		this.xcoordinate = xcoordinate;
		this.ycoordinate = ycoordinate;
		this.carOrientation = carOrientation;
	}
	
	public SonarData(int [] data ){
		if (data.length == Datasize){
			this.distsnce =  data[0];
			this.Orientation = data[1];
			this.xcoordinate = data[2];
			this.ycoordinate = data[3];
			this.carOrientation = data[4];
		}
		else {
			System.out.print("data error !");
		}
	}
	
}

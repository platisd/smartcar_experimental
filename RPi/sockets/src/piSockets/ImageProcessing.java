package piSockets;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
// import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/* written by dimitris platis
  */
public class ImageProcessing implements Runnable{
	final int PIX_SIZE = 5;
	BufferedImage img = null;
	int targetColorOccurrences =0;
	int[] blobCoordinates = new int[2];
	boolean blobFound = false;
	boolean keepFollowing = true;
	public ImageProcessing(){

	}

	public void pixelize(String imageLink) {
		File image = new File(imageLink);
		try {
			img = ImageIO.read(image);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	void init(){
		blobFound = false;
		blobCoordinates = new int[2];
		targetColorOccurrences =0;
	}

	void locateBlob(){
		Raster src = img.getData();
		init();
		int threshold = 10;
		for(int y = 0; y < img.getHeight(); y ++) {
			for(int x = 0; x < img.getWidth(); x++) {
				double[] pixel = new double[3];
				pixel = src.getPixel(x, y, pixel);
				if (isTargetColor(pixel)){
					discoverBlob(x,y);
					if (targetColorOccurrences<threshold){
						targetColorOccurrences = 0;
					}else{
						blobCoordinates[0] = x;
						blobCoordinates[1] = y;
						blobFound = true;
						y = img.getHeight()+1; //so to break from the bigger loop
						break;
					}
				}
			}
		}

	}

	void discoverBlob(int x, int y) {
		Raster src = img.getData();
		double[] pixel = new double[3];
		pixel = src.getPixel(x, y, pixel);
		if (isTargetColor(pixel) && targetColorOccurrences<20){
//			     	System.out.println("X: " + x + " y: " + y);
			targetColorOccurrences++;
			if (x+1<img.getWidth()) discoverBlob(x+1,y);
			if (y+1<img.getHeight()) discoverBlob(x,y+1);
		}

	}

	boolean isTargetColor(double pixel[]){
		if (pixel[0]>170 && pixel[1]<120 && pixel[2]<120){
			return true;
		}else{
			return false;
		}
	}

	public void findRelativePosition(int[] coordinates) {
//		byte[] goLeft = {4,8};
//		byte[] goRight = {8,4};
		byte[] keepStraight = {8,8};
//		int tolerance = 100;
//		int blobX = coordinates[0];
//		int blobY = coordinates[1];
//		int centerX = img.getWidth()/2;
//		int centerY = img.getHeight()/2;
//		System.out.println("blobX: " + blobX + " blobY: " + blobY);
//		if (blobX>((centerX + tolerance)/2)){
////			System.out.println("right");
//			SocketReceive.outputList.add(goRight);
//		}else if (blobX<((centerX - tolerance)/2)){
//			System.out.println("left");
//			SocketReceive.outputList.add(goLeft);
//		}else{
//			System.out.println("front");
			SocketReceive.outputList.add(keepStraight);
//		}
		
//		if(blobY>((centerY + tolerance)/2)){
//			System.out.println("slower");
//		}
//		if (blobY<((centerY - tolerance)/2)){
//			System.out.println("faster");
//		}
	}

	@Override
	public void run() {
		while (keepFollowing){
		//	pixelize("/home/dimi/stream/scene.jpg");
			pixelize("/dev/shm/www/pic.jpg");
			locateBlob();
			if (blobFound){
				findRelativePosition(blobCoordinates);
			}else{
				System.out.println("nothing found");
				byte[] stop = {0,0};
				SocketReceive.outputList.add(stop);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.out.println("dead");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}

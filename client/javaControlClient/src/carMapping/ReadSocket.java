package carMapping;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import SmartCarJavaClient.testrun;
import joystick.ClientSocket;

/****************
 * a multithread class for reading datas from socket.
 * @author lijiaxin
 *
 */

public class ReadSocket implements Runnable {
	//BufferedReader input ;
	//InputStreamReader InputReader;
	PrintStream out;
	DataInputStream in;
	ClientSocket clsocket;
	int [] record = new int [SonarData.Nr];
	public ReadSocket (ClientSocket socket){
		this.clsocket = socket;
		
	}
	@Override
	public void run() {
		System.out.println("listening to input");
		// TODO Auto-generated method stub
		while(true){
			System.out.println("socket receive running !" );
			//input =  socket.in;
			try {
				//InputReader = new InputStreamReader((clsocket.socket.getInputStream()));
				//input = new BufferedReader(InputReader);
				out = new PrintStream(clsocket.socket.getOutputStream());
				in = new DataInputStream(clsocket.socket.getInputStream());
				String dat ;
				while ((dat = in.readLine()) != null) {
					
				System.out.println("client socket receive:" +dat);
					if (dat.startsWith("#")){
						
					
					String [] tokens = dat.substring(1).split(",");	        
			        for (int i =0 ;i < tokens.length; i++){
			        	record [i] = Integer.parseInt(tokens[i] );
			        	System.out.print(record [i]+ " ");
			        }
			        System.out.println();
			        MapLayer.dataList.add(new SonarData(record));
			        //testrun.mapLayer = new MapLayer(testrun.mapLayer.dataList);
			        
			        
			        testrun.mapLayer.update( testrun.mapLayer.g);
			        testrun.mapLayer.revalidate();
			        testrun.mapLayer.repaint();
			        }
				}
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
	}

}

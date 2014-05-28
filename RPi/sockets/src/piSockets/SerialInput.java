package piSockets;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

/* written by dimitris platis and yilmaz caglar */
public class SerialInput {
	static boolean obstaclesInitialized = false;
	public SerialInput(String input) {
		if (input.startsWith("ping")){
			pingBack(input);
		}else if (input.startsWith("obst")){
			createObstacle(input);
		}else if (input.startsWith("#")){
			relayData2Client(input);
		}
		
	}
	
	private void relayData2Client(String input) {
		try {
			PrintWriter out = new PrintWriter(SocketsMain.piSocket.getOutputStream(), true);
			out.println(input);
		} catch (IOException e) {
			System.out.println("no socket established");
			e.printStackTrace();
		}

		
	}

	private void createObstacle(String input) {
		String[] obstacleDimentions = input.split("obst");
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/home/dimi/myPages/obstacles", obstaclesInitialized)));
			obstaclesInitialized = true;
			out.println(obstacleDimentions[1]);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void pingBack(String input) {
		String[] pingStamp = input.split("ping");
		String pong = "*pong" + pingStamp[1] + "*";
		byte[] reply = pong.getBytes(Charset.forName("UTF-8"));
		SocketReceive.outputList.add(reply);
	}

}

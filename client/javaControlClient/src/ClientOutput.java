
import java.io.PrintWriter;
import java.net.Socket;

/* written by jiaxin li and shanlin tong */
public class ClientOutput {
	Socket socket;
	PrintWriter out;
	public ClientOutput(){
		try {
			socket = new Socket("192.168.1.102", 8787);
			out = new PrintWriter(socket.getOutputStream(), true);


		} catch (Exception ex) {

		}
	}
	
	public void send2pi (String GUIinput){
		System.out.println(GUIinput);
		out.println(GUIinput);
	}
	
	public void close(){
		out.close();
	}
}

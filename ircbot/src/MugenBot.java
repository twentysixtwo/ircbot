import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MugenBot {

	private static final boolean DEBUG = false;
	
	//Server constants
	public static String hostName;
	public static int ircPort;
	public static String nick;
	public static String login; 
	public static String channel;
	
	private static BufferedWriter writer;
	private static BufferedReader reader;
	private static Socket mySock = new Socket();
	
	public MugenBot(String name, int port) throws IOException {
		/*Constructor
		 * should this init and connect?
		 * get/set methods
		 */
		//set hostname,port, nick
		setHostName(name);
		setIrcPort(port);
		
		//Initialize socket and writer/readers
		mySock = new Socket(hostName, ircPort);
		writer = new BufferedWriter(new OutputStreamWriter(mySock.getOutputStream()));
		reader = new BufferedReader(new InputStreamReader(mySock.getInputStream()));
		
	}
	
	private void setNick(String name) {
		nick = name;
	}
	
	private String getNick() {
		return nick;
	}
	
	private void setIrcPort(int port) {
		ircPort = port;
	}
	
	private int getIrcPort() {
		return ircPort;
	}
	
	private void setHostName(String name) {
		hostName = name;
	}
	
	private String getHostName() {
		return hostName;
	}
	private void connectBot() {
		//what is this for?
		botActivate();
		
	}
	
	private void botActivate() {
		// Read lines. botPing on necessary. 
		
	}

	private void botPing() {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args) throws Exception {
		//Testing
		MugenBot test = new MugenBot("goliath",6667);
		test.connectBot();
	}

}

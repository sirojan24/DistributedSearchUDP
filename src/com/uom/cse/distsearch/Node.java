package com.uom.cse.distsearch;

import com.uom.cse.distsearch.model.NodeInfo;
import com.uom.cse.distsearch.model.Request;
import com.uom.cse.distsearch.model.QueryInfo;
import com.uom.cse.distsearch.util.Constant;
import com.uom.cse.distsearch.util.Constant.Command;
import com.uom.cse.distsearch.util.MovieList;
import com.uom.cse.distsearch.util.Utility;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class Node extends Server {
	/**
	 * Logger to log the events.
	 */
	private static final Logger LOGGER = Logger.getLogger(Node.class);

	private NodeApp app;

	private final List<NodeInfo> peerList;

	private final List<QueryInfo> queryList;

	private final MovieList movieList;

	public Node(String nodeIp, int nodePort, String username, String filename, NodeApp app) {
		this.ip = nodeIp;
		//this.port = nodePort;
		this.username = username;
		this.app = app;
		
		movieList = new MovieList(filename);

		peerList = new ArrayList<NodeInfo>();

		queryList = new ArrayList<QueryInfo>();
	}
	
	public Node(String filename) {
		
		movieList = new MovieList(filename);

		peerList = new ArrayList<NodeInfo>();

		queryList = new ArrayList<QueryInfo>();
	}

	public void run() {
		try {
			port = start();

			String regString = "0114 REG " + ip + " " + port + " " + username;

			sendTcpToBootstrapServer(regString, Constant.BOOTSTRAP_SERVER_HOST, Constant.BOOTSTRAP_SERVER_PORT);
		} catch (SocketException e) {
			System.out.println("Failed to start the node: " + e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void handleJoin(NodeInfo node, NodeInfo sender, boolean toRight) {

	}

	public void handleLeave(NodeInfo node) {
		peerList.remove(node);
	}

	public void join(NodeInfo info) {
		// add this node as peer if not already exist
		addAsPeer(info);
		System.out.println("Joined : " + info.getIp() + " " + info.getPort());
		// join to obtained node
		String newJoinString = "0114 JOIN " + ip + " " + port;
		send(newJoinString, info.getIp(), info.getPort());
	}

	public void addAsPeer(NodeInfo info) {
		if (!peerList.contains(info)) {
			peerList.add(info);
			
			app.addNeighbour("user_" + info.getPort(), info.getIp());
		}
	}

	@Override
	public synchronized void onRequest(Request request) {
		String message = request.getMessage();
		String senderIP = request.getHost();
		int senderPort = request.getPort();

		StringTokenizer tokenizer = new StringTokenizer(message, " ");
		String length = tokenizer.nextToken();
		String command = tokenizer.nextToken();
		if (Command.REGOK.equals(command)) {
			int no_nodes = Integer.parseInt(tokenizer.nextToken());

			switch (no_nodes) {
			case 0:
				// This is the first node registered to the BootstrapServer.
				// Do nothing
				System.out.println("First node registered");
				break;

			case 1:
				String ipAddress = tokenizer.nextToken();
				int portNumber = Integer.parseInt(tokenizer.nextToken());

				// JOIN to first node
				join(new NodeInfo(ipAddress, portNumber));

				System.out.println("Second node registered");

				break;

			default:
				System.out.println("Node Registered");
				System.out.println("MESSAGE: " + message);
				List<NodeInfo> returnedNodes = new ArrayList<NodeInfo>();

				for (int i = 0; i < no_nodes; i++) {
					String ip = tokenizer.nextToken();
					String port = tokenizer.nextToken();
					String userID = tokenizer.nextToken();

					System.out.println(String.format("%s:%s - %s", ip, port, userID));

					NodeInfo node = new NodeInfo(ip, Integer.parseInt(port));
					returnedNodes.add(node);
				}

				returnedNodes = pickNRandom(returnedNodes, 2);

				NodeInfo nodeA = returnedNodes.get(0);
				NodeInfo nodeB = returnedNodes.get(1);

				// JOIN to node A
				join(nodeA);
				// JOIN to node B
				join(nodeB);

				break;

			case 9996:
				System.out.println("Failed to register. BootstrapServer is full.");
				close();
				break;

			case 9997:
				System.out.println("Failed to register. This ip and port is already used by another Node.");
				close();
				break;

			case 9998:
				System.out.println("You are already registered. Please unregister first.");
				close();
				break;

			case 9999:
				System.out.println("Error in the command. Please fix the error");
				close();
				break;
			}

		} else if (Command.UNROK.equals(command)) {
			System.out.println("Successfully unregistered this node");
		} else if (Command.JOIN.equals(command)) {
			System.out.println("JOIN request from " + senderIP + " " + senderPort);

			// get ip address and port no of the sender
			String ipAddress = tokenizer.nextToken();
			int portNumber = Integer.parseInt(tokenizer.nextToken());

			// create node info
			NodeInfo senderInfo = new NodeInfo(senderIP, senderPort);

			// add as peer
			addAsPeer(senderInfo);

			// send reply to sender
			String replyString = "0014 JOINOK 0";
			send(replyString, ipAddress, portNumber);
		} else if (Command.JOINOK.equals(command)) {
			// get returned value
			String value = tokenizer.nextToken();
			if (value.equals("0")) {
				System.out.println("Sucessfully joined to " + senderIP + " " + senderPort);
			}
		} else if (Command.LEAVE.equals(command)) {
			String ipAddress = tokenizer.nextToken();
			int portNumber = Integer.parseInt(tokenizer.nextToken());

			// remove this node from peerList
			handleLeave(new NodeInfo(ipAddress, portNumber));

			// send reply to sender
			String replyString = "0014 LEAVEOK 0";
			send(replyString, ipAddress, portNumber);
		} else if (Command.LEAVEOK.equals(command)) {
			String value = tokenizer.nextToken();
			if (value.equals("0")) {
				System.out.println("Sucessfully leaved from " + senderIP + " " + senderPort);
			}
		} else if (Command.DISCON.equals(command)) {
			disconnect();
			String reply = "0114 DISOK 0";
			send(reply, senderIP, senderPort);

			close();
			System.exit(0);

		} else if (Command.SER.equals(command)) {
			String sourceIp = tokenizer.nextToken();
			int sourcePort = Integer.parseInt(tokenizer.nextToken());
			long timestamp = Long.parseLong(tokenizer.nextToken());
			System.out.println("Search request from : " + sourceIp + " " + message);
			System.out.println("Routing table size : " + peerList.size());
			int hops = 0;

			StringBuilder queryBuilder = new StringBuilder();
			int noOfTokens = tokenizer.countTokens();
			for (int i = 1; i < noOfTokens; i++) {
				queryBuilder.append(tokenizer.nextToken());
				queryBuilder.append(' ');
			}
			String lastToken = tokenizer.nextToken();
			try {
				// no of hops is added at last
				hops = Integer.parseInt(lastToken);
			} catch (NumberFormatException e) {
				queryBuilder.append(lastToken);
			}
			String fileName = queryBuilder.toString().trim();

			LOGGER.debug("Request from " + senderIP + ":" + senderPort + " searching for " + fileName);
			List<String> results = movieList.search(fileName);

			hops++;

			NodeInfo sourceNodeInfo = new NodeInfo(sourceIp, sourcePort);

			NodeInfo senderNodeInfo = new NodeInfo(senderIP, senderPort);

			boolean isQueryRepeated = false;
			for (QueryInfo queryInfo : queryList) {
				if (queryInfo.getFilename().equals(fileName) && queryInfo.getSourceNodeInfo().equals(sourceNodeInfo)
						&& (queryInfo.getTimestamp() == timestamp)) {
					isQueryRepeated = true;
					break;
				}
			}

			// if query is not in my list
			if (!isQueryRepeated) {
				String resultString = "0114 SEROK " + results.size() + " " + ip + " " + port + " " + hops + " "
						+ timestamp;
				for (int i = 0; i < results.size(); i++) {
					resultString += " " + results.get(i);
				}
				send(resultString, sourceIp, sourcePort);

				QueryInfo newQueryInfo = new QueryInfo(sourceNodeInfo, senderNodeInfo, fileName, timestamp);
				queryList.add(newQueryInfo);

				String searchString = "0047 SER " + sourceIp + " " + sourcePort + " " + timestamp + " " + fileName + " "
						+ hops;
				propagateToPeers(searchString, senderNodeInfo, sourceNodeInfo);
			} else { // if query is already in my list
				// Discard the request
			}

		} else if (Command.SEROK.equals(command)) {
			int fileCount = Integer.parseInt(tokenizer.nextToken());

			// Remove port and ip od origin
			tokenizer.nextToken();
			tokenizer.nextToken();

			int hops = Integer.parseInt(tokenizer.nextToken());

			long queryTimestamp = Long.parseLong(tokenizer.nextToken());

			long currentTimestamp = System.currentTimeMillis();

			if (fileCount == 0) {
				System.out.println("No files found at " + senderIP + ":" + senderPort);
			}
			if (fileCount == 1) {
				String output = String.format("Number of files: %d\r\nHops: %d\r\nTime: %s millis\r\nOwner %s:%d",
						fileCount, hops, (currentTimestamp - queryTimestamp), senderIP, senderPort);
				Utility.printToFile(output);

				System.out.println("1 file found at " + senderIP + ":" + senderPort);
				String fileName = tokenizer.nextToken();
				System.out.println("\t" + fileName);
				Utility.printToFile(fileName);
				System.out.println("hops passed through : " + hops);
				System.out.println("latency in milli seconds : " + (currentTimestamp - queryTimestamp));
				Utility.printToFile("-----------------------------------------------------");
			}
			if (fileCount > 1) {
				String output = String.format("Number of files: %d\r\nHops: %d\r\nTime: %s millis\r\nOwner %s:%d",
						fileCount, hops, (currentTimestamp - queryTimestamp), senderIP, senderPort);
				Utility.printToFile(output);

				System.out.println(fileCount + " files found at " + senderIP + ":" + senderPort);
				for (int i = 0; i < fileCount; i++) {
					String fileName = tokenizer.nextToken();
					System.out.println("\t" + fileName);
					Utility.printToFile(fileName);
				}
				Utility.printToFile("-----------------------------------------------------");
				System.out.println("hops passed through : " + hops);
				System.out.println("latency in milli seconds : " + (currentTimestamp - queryTimestamp));
			}
		} else if (Command.ERROR.equals(command)) {
			System.out.println("Something went wrong.");
		} else {
			String reply = "0010 ERROR";
			send(reply, senderIP, senderPort);
		}
	}

	public List<NodeInfo> getPeerList() {
		return peerList;
	}

	public List<String> getMovies() {
		return movieList.getSelectedMovies();
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	private List<NodeInfo> pickNRandom(List<NodeInfo> lst, int n) {
		List<NodeInfo> copy = new ArrayList<NodeInfo>(lst);
		Collections.shuffle(copy);
		return copy.subList(0, n);
	}

	private void propagateToPeers(String message, NodeInfo senderNodeInfo, NodeInfo sourceNodeInfo) {
		int forwardMsgCount = 0;
		for (NodeInfo info : peerList) {
			if (!info.equals(senderNodeInfo) && !info.equals(sourceNodeInfo)) {
				send(message, info.getIp(), info.getPort());
				forwardMsgCount++;
			}
		}

		System.out.println("Forwarded messages for query " + "'" + message + "' " + forwardMsgCount);
	}

	/**
	 * Search for the given movie from this node.
	 *
	 * @param movie
	 */
	public void search(String movie) {
		long timestamp = System.currentTimeMillis();
		String searchString = "0047 SER " + ip + " " + port + " " + timestamp + " " + movie + " 0";

		int forwardMsgCount = 0;
		// send query to all peers
		for (NodeInfo info : peerList) {
			send(searchString, info.getIp(), info.getPort());
			forwardMsgCount++;
		}

		List<String> results = movieList.search(movie);

		String resultString = "0114 SEROK " + results.size() + " " + ip + " " + port + " " + 0 + " " + timestamp;
		for (int i = 0; i < results.size(); i++) {
			resultString += " " + results.get(i);
		}
		onRequest(new Request(ip, port, resultString));

		System.out.println("Forwarded messages for query " + "'" + searchString + "' " + forwardMsgCount);
	}

	public void disconnect() {
		// remove this node from all peers list
		for (NodeInfo peerInfo : peerList) {
			// send leave msg
			send("0114 LEAVE " + ip + " " + port, peerInfo.getIp(), peerInfo.getPort());
		}

		String unRegString = "0114 UNREG " + ip + " " + port + " user" + port;
		try {
			sendTcpToBootstrapServer(unRegString, Constant.BOOTSTRAP_SERVER_HOST, Constant.BOOTSTRAP_SERVER_PORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

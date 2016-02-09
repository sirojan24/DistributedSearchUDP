package com.uom.cse.distsearch;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.uom.cse.distsearch.model.NodeInfo;
import com.uom.cse.distsearch.util.Utility;

public class Test {
	public static final String QUERY_FILE = "Queries.txt";

	public static void main(String[] args) {
		System.out.println("Start testing....");
		String movieFile = "movies.txt";
		try (Node node = new Node(movieFile);) {
			// IP address
			node.setIp("10.111.12.30");

			// Start the node
			node.run();

			Utility.printToFile("Node: " + node.getIp() + ":" + node.getPort());

			Utility.printToFile("");
			
			String movies = node.getMovies().stream().collect(Collectors.joining(", "));

			Utility.printToFile("Movies: " + movies);

			Utility.printToFile("");

			List<NodeInfo> peersList = node.getPeerList();
			int count = peersList.size();
			Utility.printToFile("Number of peers: " + count);
			if (count > 0) {
				String peers = peersList.stream().map(x -> x.getIp() + ":" + x.getPort())
						.collect(Collectors.joining(", "));
				Utility.printToFile("Peers: " + peers);
			}

			Utility.printToFile("\r\n\r\n\r\n");

			System.out.println("Start searching...");

			try (Scanner scanner = new Scanner(new File(QUERY_FILE))) {
				while (scanner.hasNextLine()) {
					String query = scanner.nextLine().trim().replace(" ", "%20");
					node.search(query);
					sleep();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Stop searching");
		}

		System.out.println("Testing is finished successfully.");
	}

	public static void sleep() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
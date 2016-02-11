package com.uom.cse.distsearch.util;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Settings {
	/**
	 * static Singleton instance.
	 */
	private static Settings instance;

	private String serverIP = "";

	private String serverPort = "";

	private String nodeIP = "";

	private String nodePort = "";

	private String userName = "";

	private String outputFile = "Output.txt";

	/**
	 * Private constructor for singleton.
	 */
	private Settings() {
		Properties properties = new Properties();
		try {
			properties.load(new FileReader("settings.properties"));
			this.serverIP = properties.getProperty("SERVER_IP", "");
			this.serverPort = properties.getProperty("SERVER_PORT", "");
			this.nodeIP = properties.getProperty("NODE_IP", "");
			this.nodePort = properties.getProperty("NODE_PORT", "");
			this.outputFile = properties.getProperty("OUTPUT_FILE", "Output.txt");
			this.userName = System.getProperty("user.name");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Return a singleton instance of Settings.
	 */
	public static Settings getInstance() {
		// Double lock for thread safety.
		if (instance == null) {
			synchronized (Settings.class) {
				if (instance == null) {
					instance = new Settings();
				}
			}
		}
		return instance;
	}

	public String getServerIP() {
		return serverIP;
	}

	public String getServerPort() {
		return serverPort;
	}

	public String getUserName() {
		return userName;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public String getNodeIP() {
		return nodeIP;
	}

	public String getNodePort() {
		return nodePort;
	}

}

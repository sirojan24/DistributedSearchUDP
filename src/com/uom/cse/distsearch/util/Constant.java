package com.uom.cse.distsearch.util;

public class Constant {
	private Constant() {
	}

	public static final int BOOTSTRAP_SERVER_PORT = 8888;
	
	public static final String BOOTSTRAP_SERVER_HOST = "192.248.15.229";
	
	public static final int BUFFER_SIZE = 65536;
	
	public static final class Command {
		private Command() {
		}

		public static final String REG = "REG";
		
		public static final String UNREG = "UNREG";
		
		public static final String ECHO = "ECHO";
		
		public static final String REGOK = "REGOK";
		
		public static final String UNROK = "UNROK";
		
		public static final String JOIN = "JOIN";
		
		public static final String JOINOK = "JOINOK";
		
		public static final String LEAVE = "LEAVE";
		
		public static final String LEAVEOK = "LEAVEOK";
		
		public static final String DISCON = "DISCON";
		
		public static final String DISOK = "DISOK";
		
		public static final String SER = "SER";
		
		public static final String SEROK = "SEROK";
		
		public static final String ERROR = "ERROR";
	}
}

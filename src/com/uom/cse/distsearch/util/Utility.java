package com.uom.cse.distsearch.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

public class Utility {
	private static final String FILE_NAME = "C:/Distributed/output.txt";

	private static final Logger LOGGER = Logger.getLogger(Utility.class);

	private Utility() {
	}

	public static void printToFile(String message) {
		try (PrintWriter writer = new PrintWriter(new FileOutputStream(FILE_NAME), true);) {
			writer.println(message);
		} catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
}

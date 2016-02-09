package com.uom.cse.distsearch.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.apache.log4j.Logger;

public class MovieList {
	/**
	 * Logger to log the events.
	 */
	private static final Logger LOGGER = Logger.getLogger(MovieList.class);

	private List<String> movies = new ArrayList<String>();

	public MovieList(String fileName) {
		this.movies = selectMovies(fileName);
	}

	public List<String> search(String query) {
		List<String> list = new ArrayList<String>();

		if (query != null && !query.trim().equals("")) {
			query = query.toLowerCase();
			for (String movie : movies) {
				if (movie.toLowerCase().contains(query)) {
					// Remove the spaces
					list.add(movie.replaceAll(" ", "_"));
				}
			}
		}
		return list;
	}

	private List<String> selectMovies(String fileName) {
		List<String> list = new ArrayList<>();
		List<String> movies = new ArrayList<String>();

		try (Scanner scanner = new Scanner(new File(fileName))) {
			while (scanner.hasNextLine()) {
				list.add(scanner.nextLine());
			}
		} catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
		}

		Collections.shuffle(list);

		Random rand = new Random();
		int num = rand.nextInt(3) + 3;
		for (int i = 0; i < num; i++) {
			movies.add(list.get(i));
		}

		return movies;
	}

	public List<String> getSelectedMovies() {
		return this.movies;
	}

	@Override
	public String toString() {
		return movies.toString();
	}
}

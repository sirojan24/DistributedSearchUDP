package com.uom.cse.distsearch.view.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Movie {
private StringProperty name;
	
	public Movie(String name) {
		this.name = new SimpleStringProperty(name);
	}

	public String getName() {
		return name.get();
	}
	
	public void setName(String name){
		this.name.set(name);
	}
	
	public StringProperty NameProperty(){
		return name;
	}
	
}

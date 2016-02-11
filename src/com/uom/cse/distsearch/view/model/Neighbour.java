package com.uom.cse.distsearch.view.model;

import com.uom.cse.distsearch.model.NodeInfo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Neighbour {
	private final StringProperty IPAddress;
	private StringProperty name;
	
	public Neighbour(String IpAddress, String name) {
		this.IPAddress = new SimpleStringProperty(IpAddress);
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
	
	public String getIPAddress() {
        return IPAddress.get();
    }

    public void setIPAddress(String IPAddress) {
        this.IPAddress.set(IPAddress);
    }
    
    public StringProperty IPAddressProperty() {
        return IPAddress;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Neighbour neighbour = (Neighbour) o;

        if (!name.equals(neighbour.getName())) return false;
        
        return IPAddress.equals(neighbour.getIPAddress());

    }

    @Override
    public int hashCode() {
        int result = IPAddress.hashCode();
        result = 31 * result;
        return result;
    }
}

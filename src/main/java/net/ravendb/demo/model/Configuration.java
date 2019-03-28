package net.ravendb.demo.model;

import java.util.ArrayList;
import java.util.List;

public class Configuration {

	private List<String> locations=new ArrayList<>();
	
	private List<String> departments=new ArrayList<>();

	
	
	public List<String> getLocations() {
		return locations;
	}

	public void setLocations(List<String> locations) {
		this.locations = locations;
	}

	public List<String> getDepartments() {
		return departments;
	}

	public void setDepartments(List<String> departments) {
		this.departments = departments;
	}
	
	
	
}

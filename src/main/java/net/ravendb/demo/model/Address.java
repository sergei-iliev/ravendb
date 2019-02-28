package net.ravendb.demo.model;

import java.io.Serializable;

public class Address{
    private String line;
    private String city;
    private String region;
    private String code;
    
	public String getLine() {
		return line;
	}
	public void setLine(String line) {
		this.line = line;
	}

	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
    
    
}

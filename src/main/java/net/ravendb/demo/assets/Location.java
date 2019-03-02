package net.ravendb.demo.assets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Location{
	HOUSE,
	EMERGENCYROOM,
	HOSPITAL;
    
	@JsonCreator
    public static Location convert(String type){
        if(type==null){
            return Location.HOUSE;
        }
        
        return Location.valueOf(type);
    }
    
    @JsonValue
    public String getLocation() {        
        return this.toString();
    }    	
}

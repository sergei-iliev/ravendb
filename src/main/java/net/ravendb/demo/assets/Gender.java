package net.ravendb.demo.assets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender{
	MALE,
	FEMALE;
    
	@JsonCreator
    public static Gender convert(String status){
        if(status==null){
            return Gender.MALE;
        }
        
        return Gender.valueOf(status);
    }
    
    @JsonValue
    public String getGender() {        
        return this.toString();
    }    	
}

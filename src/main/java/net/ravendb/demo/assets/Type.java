package net.ravendb.demo.assets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;



public enum Type{
	SEVIER,MINOR,CHRONIC,NORMAL;
	@JsonCreator
    public static Type convert(String status){
        if(status==null){
            return Type.NORMAL;
        }
        
        return Type.valueOf(status);
    }
    
    @JsonValue
    public String getType() {        
        return this.toString();
    }  
}


package com.luee.wally.json;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtils {
    
    private static ObjectMapper mapper;

    
    static{
        mapper = new ObjectMapper();
    }


    public static <T> T readObject(String map,Class<T> clazz) throws JsonParseException, JsonMappingException, IOException{
        return mapper.readValue(map, clazz);
    }
	public static <T> String writeObject(T object,Class<T> clazz)throws JsonProcessingException{
	    return mapper.writeValueAsString(object);	
	}
	
    public static <T> T convertToObject(String value, Class<T> clazz) {
        return mapper.convertValue(value, clazz); 
    }
    public static String writeObject(Map<?,?> map)throws JsonProcessingException{        
        return mapper.writeValueAsString(map);       
    }

    public static  String convertToString(Object o) throws JsonProcessingException{
        return mapper.writeValueAsString(o);
    }

}

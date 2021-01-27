package com.luee.wally.api.tangocard.client.config;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class TangoCardJSON {

    private static ObjectMapper mapper;

    
    static{
        mapper = new ObjectMapper();  
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
       

        // enable features
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        

        // exclude null values
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // "As of Jackson 2.x, auto-registration will only register older JSR310Module, and not newer JavaTimeModule
        // -- this is due to backwards compatibility. Because of this make sure to either use explicit registration,
        //  or, if you want to use JavaTimeModule but also auto-registration, make sure to register JavaTimeModule
        // BEFORE calling mapper.findAndRegisterModules())." - https://github.com/FasterXML/jackson-modules-java8
        //mapper.registerModule(new JavaTimeModule());         
    }

    public static <T> T readObject(String map,Class<T> clazz) throws JsonParseException, JsonMappingException, IOException{
        return mapper.readValue(map, clazz);
    }
    
    public static <T> T convertToObject(String value, Class<T> clazz) {
        return mapper.convertValue(value, clazz); 
    }
}

package com.example.ratingservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Util {
    private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);


    public static String write(Object object){
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Failed to write object to json. {}", e.getMessage());
        }
        return null;
    }
}

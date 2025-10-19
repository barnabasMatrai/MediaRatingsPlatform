package service;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ICanMapObjects {
    private ObjectMapper objectMapper;

    public ICanMapObjects() {
        this.objectMapper = new ObjectMapper();
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}

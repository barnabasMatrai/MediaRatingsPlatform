package objectmappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.IModel;

public class JsonDeserializer {

    private final ObjectMapper objectMapper;

    public JsonDeserializer() {
        this.objectMapper = new ObjectMapper();
    }

    public <T extends IModel> T fromJson(String json, Class<T> classType) {
        try {
            return objectMapper.readValue(json, classType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize JSON to " + classType.getSimpleName(), e);
        }
    }
}

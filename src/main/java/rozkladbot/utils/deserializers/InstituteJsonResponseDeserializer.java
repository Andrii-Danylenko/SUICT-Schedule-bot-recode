package rozkladbot.utils.deserializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rozkladbot.constants.ErrorConstants;
import rozkladbot.entities.json.response.InstituteJsonResponse;

import java.util.Deque;

@Component("instituteJsonResponseDeserializer")
public class InstituteJsonResponseDeserializer implements JsonDeserializer<InstituteJsonResponse> {
    private static final Logger logger = LoggerFactory.getLogger(InstituteJsonResponseDeserializer.class);
    private final ObjectMapper mapper;

    public InstituteJsonResponseDeserializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Deque<InstituteJsonResponse> deserialize(String json) {
        try {
            JsonNode jsonNode = mapper.readTree(json);
            return mapper.readValue(jsonNode.toString(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(ErrorConstants.JSON_PROCESSING_FAILED);
        }
    }
}

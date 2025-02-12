package rozkladbot.utils.deserializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rozkladbot.constants.ErrorConstants;
import rozkladbot.entities.json.response.GroupJsonResponse;

import java.util.Deque;

@Component("groupResponseJsonDeserializer")
public class GroupJsonResponseDeserializer implements JsonDeserializer<GroupJsonResponse> {
    private static final Logger logger = LoggerFactory.getLogger(GroupJsonResponseDeserializer.class);
    private final ObjectMapper mapper;

    public GroupJsonResponseDeserializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Deque<GroupJsonResponse> deserialize(String json) {
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

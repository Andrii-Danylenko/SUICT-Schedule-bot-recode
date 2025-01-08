package rozkladbot.utils.deserializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rozkladbot.constants.AppConstants;
import rozkladbot.constants.ErrorConstants;
import rozkladbot.entities.User;

import java.util.List;


@Component("userDeserializer")
public class UserDeserializer implements JsonDeserializer<User> {
    private static final Logger logger = LoggerFactory.getLogger(UserDeserializer.class);
    private final ObjectMapper mapper;

    @Autowired
    public UserDeserializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<User> deserialize(String json) {
        try {
            JsonNode jsonNode = mapper.readTree(json).get(AppConstants.JSON_TREE_USER_OBJECT_NAME);
            return mapper.readValue(jsonNode.toString(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(ErrorConstants.JSON_PROCESSING_FAILED);
        }
    }
}
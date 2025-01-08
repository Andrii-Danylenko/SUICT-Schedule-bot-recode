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
import rozkladbot.entities.Institute;

import java.util.List;

@Component("InstituteDeserializer")
public class InstituteDeserializer implements JsonDeserializer<Institute> {
    private static final Logger logger = LoggerFactory.getLogger(InstituteDeserializer.class);
    private final ObjectMapper mapper;

    @Autowired
    public InstituteDeserializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }


    @Override
    public List<Institute> deserialize(String json) {
        try {
            JsonNode jsonNode = mapper.readTree(json).get(AppConstants.JSON_TREE_GROUPS_OBJECT_NAME);
            return mapper.readValue(jsonNode.toString(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(ErrorConstants.JSON_PROCESSING_FAILED);
        }
    }
}

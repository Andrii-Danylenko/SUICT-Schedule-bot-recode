package rozkladbot.utils.deserializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rozkladbot.constants.ErrorConstants;
import rozkladbot.entities.Lesson;

import java.util.List;

@Component("lessonDeserializer")
public class LessonDeserializer implements JsonDeserializer<Lesson> {
    private static final Logger logger = LoggerFactory.getLogger(LessonDeserializer.class);
    private final ObjectMapper mapper;

    @Autowired
    public LessonDeserializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<Lesson> deserialize(String json) {
        try {
            JsonNode jsonNode = mapper.readTree(json).get("schedule");
            return mapper.readValue(jsonNode.toString(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(ErrorConstants.JSON_PROCESSING_FAILED);
        }
    }
}

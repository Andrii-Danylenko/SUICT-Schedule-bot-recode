package rozkladbot.utils.deserializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rozkladbot.constants.ErrorConstants;
import rozkladbot.entities.PairLink;

import java.util.Deque;

import static rozkladbot.constants.AppConstants.JSON_TREE_PAIR_LINKS_OBJECT_NAME;


@Component("pairLinksDeserializer")
public class PairLinksDeserializer implements JsonDeserializer<PairLink> {
    private static final Logger logger = LoggerFactory.getLogger(PairLinksDeserializer.class);
    private final ObjectMapper mapper;

    public PairLinksDeserializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Deque<PairLink> deserialize(String json) {
        try {
            JsonNode jsonNode = mapper.readTree(json).get(JSON_TREE_PAIR_LINKS_OBJECT_NAME);
            return mapper.readValue(jsonNode.toString(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(ErrorConstants.JSON_PROCESSING_FAILED);
        }
    }
}

package rozkladbot.utils.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.stereotype.Component;
import rozkladbot.services.GroupService;

import java.io.IOException;

@Component("pairLinksGroupDeserializer")
public class PairLinksGroupDeserializer extends JsonDeserializer<Long> {
    private final GroupService groupService;

    public PairLinksGroupDeserializer(GroupService groupService) {
        this.groupService = groupService;
    }

    @Override
    public Long deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return groupService.getByName(jsonParser.getText()).getId();
    }
}
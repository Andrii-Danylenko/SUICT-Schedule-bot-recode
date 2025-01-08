package rozkladbot.utils.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rozkladbot.entities.Group;
import rozkladbot.services.GroupService;

import java.io.IOException;

@Component("groupDeserializer")
public class GroupDeserializer extends JsonDeserializer<Group> {
    private final GroupService groupService;

    @Autowired
    public GroupDeserializer(GroupService groupService) {
        this.groupService = groupService;
    }

    @Override
    public Group deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return groupService.getByName(jsonParser.getText());
    }
}

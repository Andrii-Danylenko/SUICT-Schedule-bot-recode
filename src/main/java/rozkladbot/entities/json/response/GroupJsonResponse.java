package rozkladbot.entities.json.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GroupJsonResponse {
    @JsonProperty("id")
    private long groupId;
    @JsonProperty("value")
    private String groupName;

}

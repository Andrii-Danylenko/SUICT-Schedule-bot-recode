package rozkladbot.entities.json.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GroupJsonResponse {
    @JsonProperty("id")
    private long groupId;
    @JsonProperty("value")
    private String groupName;

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}

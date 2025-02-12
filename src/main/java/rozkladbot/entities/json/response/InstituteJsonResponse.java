package rozkladbot.entities.json.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InstituteJsonResponse {
    @JsonProperty("id")
    private long id;
    @JsonProperty("value")
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "InstituteJsonResponse{" +
               "id=" + id +
               ", name='" + name + '\'' +
               '}';
    }
}

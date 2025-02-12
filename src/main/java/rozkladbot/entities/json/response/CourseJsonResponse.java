package rozkladbot.entities.json.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CourseJsonResponse {
    @JsonProperty("id")
    private long id;
    @JsonProperty("value")
    private long value;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "CourseJsonResponse{" +
               "id=" + id +
               ", value=" + value +
               '}';
    }
}

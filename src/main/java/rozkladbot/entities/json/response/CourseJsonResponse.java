package rozkladbot.entities.json.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CourseJsonResponse {
    @JsonProperty("id")
    private long id;
    @JsonProperty("value")
    private long value;

  @Override
    public String toString() {
        return "CourseJsonResponse{" +
               "id=" + id +
               ", value=" + value +
               '}';
    }
}

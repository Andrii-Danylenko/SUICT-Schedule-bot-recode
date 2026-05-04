package rozkladbot.entities.json.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InstituteJsonResponse {
    @JsonProperty("id")
    private long id;
    @JsonProperty("value")
    private String name;

  @Override
    public String toString() {
        return "InstituteJsonResponse{" +
               "id=" + id +
               ", name='" + name + '\'' +
               '}';
    }
}

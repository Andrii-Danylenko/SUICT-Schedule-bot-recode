package rozkladbot.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import rozkladbot.entities.Institute;

public class InstituteDTO {
    @JsonProperty("id")
    private String id;
    @JsonProperty("institute")
    private String name;

    public InstituteDTO(Institute institute) {
        this.id = String.valueOf(institute.getId());
        this.name = institute.getInstituteName();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

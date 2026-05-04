package rozkladbot.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import rozkladbot.entities.json.response.InstituteJsonResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "institutes")
public class Institute {
    @Id
    long id;
    @Column(unique = true, nullable = false)
    @JsonProperty("institute")
    String instituteName;
    @OneToMany(mappedBy = "institute", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    List<Faculty> facultyList = new ArrayList<>();

    public Institute() {
    }

    public Institute(long id, String instituteName) {
        this.id = id;
        this.instituteName = instituteName;
    }

  @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Institute institute = (Institute) o;
        return id == institute.id && Objects.equals(instituteName, institute.instituteName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, instituteName);
    }



    public static Institute toInstituteFromJsonResponse(InstituteJsonResponse jsonResponse) {
        Institute institute = new Institute();
        institute.setId(jsonResponse.getId());
        institute.setInstituteName(jsonResponse.getName());
        return institute;
    }

    @Override
    public String toString() {
        return "Institute{" +
               "id=" + id +
               ", instituteName='" + instituteName + '\'' +
               ", facultyList=" + facultyList +
               '}';
    }
}

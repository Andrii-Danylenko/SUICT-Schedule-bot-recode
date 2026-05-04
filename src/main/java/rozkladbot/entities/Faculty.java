package rozkladbot.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import rozkladbot.entities.json.response.FacultyJsonResponse;

import java.util.List;
import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "faculties")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Faculty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @JsonProperty("faculty")
    private long facultyId;
    @Column(unique = true, nullable = false)
    private String facultyName;
    @ManyToOne
    Institute institute;
    @OneToMany(mappedBy = "faculty", cascade = CascadeType.ALL)
    List<Group> groups;

    public Faculty() {
    }

    public Faculty(long facultyId, String facultyName, Institute institute, List<Group> groups) {
        this.facultyId = facultyId;
        this.facultyName = facultyName;
        this.institute = institute;
        this.groups = groups;
    }

  @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Faculty faculty = (Faculty) o;
        return id == faculty.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }



    public static Faculty toFacultyFromJsonResponse(FacultyJsonResponse jsonResponse) {
        Faculty faculty = new Faculty();
        faculty.setFacultyId(jsonResponse.getId());
        faculty.setFacultyName(jsonResponse.getName());
        return faculty;
    }

    @Override
    public String toString() {
        return "Faculty{" +
               "id=" + facultyId +
               ", facultyName='" + facultyName + '\'' +
               ", groups=" + groups +
               '}';
    }

}

package rozkladbot.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import rozkladbot.entities.json.response.GroupJsonResponse;

import java.util.List;
import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "groups")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @JsonProperty("groupNumber")
    private long groupId;
    @Column(name = "name")
    @JsonProperty("group")
    private String name;
    @ManyToOne
    private Faculty faculty;
    @Column(name = "course")
    @JsonProperty("course")
    private long course;
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<User> users;

    public Group(long groupId, String name, Faculty faculty, long course, List<User> users) {
        this.groupId = groupId;
        this.name = name;
        this.faculty = faculty;
        this.course = course;
        this.users = users;
    }

    public Group() {
    }

  @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return id == group.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

  @Override
    public String toString() {
        return "Group{" +
               "id=" + groupId +
               ", name='" + name + '\'' +
               ", faculty=" + faculty.getFacultyName() +
               ", course=" + course +
               '}';
    }

    public static Group toGroupFromResponse(GroupJsonResponse jsonResponse) {
        Group group = new Group();
        group.setGroupId(jsonResponse.getGroupId());
        group.setName(jsonResponse.getGroupName());
        return group;
    }

}

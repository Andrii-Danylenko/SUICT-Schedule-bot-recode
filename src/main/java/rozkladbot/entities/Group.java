package rozkladbot.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import rozkladbot.entities.json.response.GroupJsonResponse;

import java.util.List;
import java.util.Objects;

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

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long id) {
        this.groupId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
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

    public long getCourse() {
        return course;
    }

    public void setCourse(long course) {
        this.course = course;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

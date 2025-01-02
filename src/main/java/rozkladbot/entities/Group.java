package rozkladbot.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "groups")
public class Group {
    @Id
    private long id;
    @Column(name = "name")
    private String name;
    @ManyToOne
    private Faculty faculty;
    @Column(name = "course")
    private long course;
    @OneToMany(mappedBy = "group")
    private List<User> users;

    public Group(long id, String name, Faculty faculty, long course, List<User> users) {
        this.id = id;
        this.name = name;
        this.faculty = faculty;
        this.course = course;
        this.users = users;
    }

    public Group() {
    }

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
}

package rozkladbot.entities;

import jakarta.persistence.*;
import rozkladbot.enums.UserState;

import java.util.Objects;

@Entity
@Table(name = "users")
public class User {
    @Id
    private long id;
    @Column(name = "username")
    private String username;
    @ManyToOne
    Group group;
    @Transient
    UserState userState;


    public User() {
    }

    public User(long id, String username, Group group, UserState userState) {
        this.id = id;
        this.username = username;
        this.group = group;
        this.userState = userState;
    }

    public long getid() {
        return id;
    }

    public void setid(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UserState getUserState() {
        return userState;
    }

    public void setUserState(UserState userState) {
        this.userState = userState;
    }
}

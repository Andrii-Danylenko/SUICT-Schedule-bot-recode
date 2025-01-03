package rozkladbot.entities;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import rozkladbot.enums.UserRole;
import rozkladbot.enums.UserState;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    private long id;
    @Column(name = "username")
    private String username;
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    Group group;
    @Transient
    UserState userState = UserState.UNREGISTERED;
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    UserRole userRole = UserRole.USER;
    @Column(name = "is_broadcasted")
    boolean isBroadcasted = true;
    @Column(name = "last_pinned_message_id")
    int lastPinnedMessageId = 0;
    @Transient
    private int lastSentMessageId;


    public User() {
    }

    public User(
            long id,
            String username,
            Group group,
            UserState userState,
            UserRole userRole,
            int lastSentMessageId,
            boolean isBroadcasted,
            int lastPinnedMessageId) {
        this.id = id;
        this.username = username;
        this.group = group;
        this.userState = userState;
        this.userRole = userRole;
        this.lastSentMessageId = lastSentMessageId;
        this.isBroadcasted = isBroadcasted;
        this.lastPinnedMessageId = lastPinnedMessageId;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(userRole.toString()));
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return "";
    }

    public String getUsername() {
        return username;
    }

    public long getId() {
        return id;
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

    public UserState getUserState() {
        return userState;
    }

    public void setUserState(UserState userState) {
        this.userState = userState;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public int getLastSentMessageId() {
        return lastSentMessageId;
    }

    public void setLastSentMessageId(int lastSentMessageId) {
        this.lastSentMessageId = lastSentMessageId;
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", group=" + group +
               ", userState=" + userState +
               ", userRole=" + userRole +
               ", lastSentMessageId=" + lastSentMessageId +
               '}';
    }

    public boolean isBroadcasted() {
        return isBroadcasted;
    }

    public void setBroadcasted(boolean broadcasted) {
        isBroadcasted = broadcasted;
    }

    public int getLastPinnedMessageId() {
        return lastPinnedMessageId;
    }

    public void setLastPinnedMessageId(int lastPinnedMessageId) {
        this.lastPinnedMessageId = lastPinnedMessageId;
    }
}

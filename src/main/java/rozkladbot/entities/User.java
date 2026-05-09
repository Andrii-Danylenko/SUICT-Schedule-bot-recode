package rozkladbot.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.telegram.telegrambots.meta.api.objects.Update;
import rozkladbot.enums.UserRole;
import rozkladbot.enums.UserState;
import rozkladbot.json.deserializers.GroupDeserializer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements UserDetails {

  @Id
  @JsonProperty("chatId")
  private long id;
  @Column(name = "username")
  @JsonProperty("userName")
  private String username;
  @ManyToOne
  @JsonProperty("group")
  @JsonDeserialize(using = GroupDeserializer.class)
  Group group;
  @Transient
  UserState userState = UserState.UNREGISTERED;
  @Column(name = "role")
  @Enumerated(EnumType.STRING)
  @JsonProperty("role")
  UserRole userRole = UserRole.USER;
  @Column(name = "is_broadcasted")
  @JsonProperty("areInBroadcastGroup")
  boolean isBroadcasted = true;
  @Column(name = "last_pinned_message_id")
  @JsonProperty("lastPinnedMessage")
  int lastPinnedMessageId = 0;
  @Column(name = "last_sent_message_id")
  @JsonProperty("lastSentMessage")
  private int lastSentMessageId;
  @Transient
  private boolean isRegistered = false;
  @Transient
  private LocalDateTime lastInteractionDate;

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
      int lastPinnedMessageId,
      boolean isRegistered) {
    this.id = id;
    this.username = username;
    this.group = group;
    this.userState = userState;
    this.userRole = userRole;
    this.lastSentMessageId = lastSentMessageId;
    this.isBroadcasted = isBroadcasted;
    this.lastPinnedMessageId = lastPinnedMessageId;
    this.isRegistered = isRegistered;
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

  @Override
  public String toString() {
    return
        """
            Назва чату: %s
            ID-чату: %d
            Назва університету: %s
            Назва кафедри: %s
            Група (Назва): %s
            Група (ID): %d
            Курс: %d
            Роль: %s
            """.formatted(
            username,
            id,
            group.getFaculty().getInstitute().getInstituteName(),
            group.getFaculty().getFacultyName(),
            group.getName(),
            group.getGroupId(),
            group.getCourse(),
            userRole);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return id == user.id;
  }

  public static User getDefaultUser(Update update, long chatId) {
    User user = new User();
    user.setUsername(getCorrectName(update));
    user.setId(chatId);
    user.setLastSentMessageId(update.getMessage().getMessageId());
    user.setUserState(UserState.AWAITING_GREETINGS);
    Group group = new Group();
    Institute institute = new Institute();
    Faculty faculty = new Faculty();
    faculty.setInstitute(institute);
    group.setFaculty(faculty);
    user.setGroup(group);
    return user;
  }

  private static String getCorrectName(Update update) {
    String userName = null;
    if (update.hasMessage()) {
      if (update.getMessage().isUserMessage()) {
        userName = "@" + update.getMessage().getFrom().getUserName();
      } else if (update.getMessage().isGroupMessage() || update.getMessage()
          .isSuperGroupMessage()) {
        userName = update.getMessage().getChat().getTitle();
      }
    }
    return userName == null ? "N/A" : userName;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

}

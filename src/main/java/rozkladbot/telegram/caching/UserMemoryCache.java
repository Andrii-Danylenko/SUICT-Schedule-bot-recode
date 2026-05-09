package rozkladbot.telegram.caching;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import rozkladbot.entities.User;

@Component("simpleUserCache")
public class UserMemoryCache extends SimpleMemoryCache<Long, User> {
  @Override
  public User get(Long key) {
    User user = super.get(key);
    user.setLastInteractionDate(LocalDateTime.now());
    return user;
  }

  @Override
  public void put(Long key, User value) {
    value.setLastInteractionDate(LocalDateTime.now());
    super.put(key, value);
  }
}

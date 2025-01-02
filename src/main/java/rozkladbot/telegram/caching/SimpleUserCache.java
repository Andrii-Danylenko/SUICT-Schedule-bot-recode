package rozkladbot.telegram.caching;

import org.springframework.stereotype.Component;
import rozkladbot.entities.User;

@Component("simpleUserCache")
public class SimpleUserCache extends SimpleCache<Long, User> {
    public boolean existsByUser(User user) {
        return localCache.containsKey(user.getId());
    }
}

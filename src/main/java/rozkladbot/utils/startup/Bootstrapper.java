package rozkladbot.utils.startup;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rozkladbot.constants.LoggingConstants;
import rozkladbot.entities.User;
import rozkladbot.enums.UserState;
import rozkladbot.services.UserService;
import rozkladbot.telegram.caching.SimpleUserCache;

import java.util.List;

@Component
public class Bootstrapper {
    private static final Logger logger = LoggerFactory.getLogger(Bootstrapper.class);
    private final SimpleUserCache userCache;
    private final UserService userService;

    @Autowired
    public Bootstrapper(SimpleUserCache userCache, UserService userService) {
        this.userCache = userCache;
        this.userService = userService;
    }

    @PostConstruct
    public void loadUsers() {
        logger.info(LoggingConstants.USERS_MIGRATION_STARTED);
        List<User> userList = userService.getAll();
        for (User user : userList) {
            user.setUserState(UserState.MAIN_MENU);
            userCache.put(user.getId(), user);
        }
        logger.info(LoggingConstants.USERS_MIGRATION_FINISHED, userCache.size());
    }
}

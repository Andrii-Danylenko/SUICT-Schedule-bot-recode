package rozkladbot.telegram.startup;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rozkladbot.constants.LoggingConstants;
import rozkladbot.entities.User;
import rozkladbot.enums.UserState;
import rozkladbot.services.UserService;
import rozkladbot.telegram.caching.UserCache;

import java.util.List;

@Component
public class Bootstrapper {
    private static final Logger logger = LoggerFactory.getLogger(Bootstrapper.class);
    private final UserCache userCache;
    private final UserService userService;

    @Autowired
    public Bootstrapper(
            UserCache userCache,
            UserService userService) {
        this.userCache = userCache;
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        logger.info(LoggingConstants.APPLICATION_INIT_JOB_STARTED);
        loadUsers();
        logger.info(LoggingConstants.APPLICATION_INIT_JOB_FINISHED);
    }

    private void loadUsers() {
        logger.info(LoggingConstants.USERS_MIGRATION_STARTED);
        List<User> userList = userService.getAll();
        for (User user : userList) {
            user.setUserState(UserState.IDLE);
            user.setRegistered(true);
            userCache.put(user.getId(), user);
        }
        logger.info(LoggingConstants.USERS_MIGRATION_FINISHED, userCache.size());
    }
}

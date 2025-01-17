package rozkladbot.telegram.utils.synchronization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import rozkladbot.constants.AppConstants;
import rozkladbot.constants.LoggingConstants;
import rozkladbot.entities.User;
import rozkladbot.services.UserService;
import rozkladbot.telegram.caching.UserCache;

import java.util.Set;

@Component("userSynchronizer")
public class UserSynchronizer {
    private static final Logger logger = LoggerFactory.getLogger(UserSynchronizer.class);
    private final UserCache userCache;
    private final UserService userService;

    @Autowired
    public UserSynchronizer(UserCache userCache, UserService userService) {
        this.userCache = userCache;
        this.userService = userService;
    }

    @Async
    @Scheduled(cron = AppConstants.USER_SYNC_CRON, zone = AppConstants.APPLICATION_TIME_ZONE)
    public void saveLocalUsers() {
        logger.info(LoggingConstants.USERS_SYNC_STARTED);
        Set<User> userSet = userCache.getAllValues();
        for (User user : userSet) {
            userService.save(user);
        }
        logger.info(LoggingConstants.USERS_SYNC_FINISHED);
    }
}

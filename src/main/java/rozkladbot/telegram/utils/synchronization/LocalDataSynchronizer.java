package rozkladbot.telegram.utils.synchronization;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rozkladbot.entities.User;
import rozkladbot.services.UserService;
import rozkladbot.telegram.caching.UserMemoryCache;
import rozkladbot.telegram.utils.schedule.ScheduleCacheFileWriter;

@Component("localDataSynchronizer")
public class LocalDataSynchronizer {
    private static final Logger logger = LoggerFactory.getLogger(LocalDataSynchronizer.class);
    private final UserService userService;
    private final UserMemoryCache localUserCache;
    private final ScheduleCacheFileWriter scheduleCacheFileWriter;

    @Autowired
    public LocalDataSynchronizer(
            UserService userService,
            UserMemoryCache localUserCache,
            ScheduleCacheFileWriter scheduleCacheFileWriter) {
        this.userService = userService;
        this.localUserCache = localUserCache;
        this.scheduleCacheFileWriter = scheduleCacheFileWriter;
    }

    public synchronized void synchronize(String message) {
        String[] synchronizationKeys = getSyncKeys(message);
        logger.info("Synchronizing local data");
        for (String key : synchronizationKeys) {
            switch (key) {
                case "-u" -> synchronizeUsers();
                case "-s" -> synchronizeSchedules();
                case "-all" -> {
                    synchronizeUsers();
                    synchronizeSchedules();
                }
            }
        }
    }

    private synchronized void synchronizeUsers() {
        for (User user : localUserCache.getAllValues()) {
            userService.save(user);
        }
    }

    private synchronized void synchronizeSchedules() {
        scheduleCacheFileWriter.dumpSchedule(true);
    }

    private String[] getSyncKeys(String message) {
        return message.split(" ");
    }
}

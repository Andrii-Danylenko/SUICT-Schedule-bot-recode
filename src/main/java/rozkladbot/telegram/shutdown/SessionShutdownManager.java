package rozkladbot.telegram.shutdown;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import rozkladbot.constants.LoggingConstants;
import rozkladbot.telegram.utils.synchronization.LocalDataSynchronizer;

@Component("sessionShutdownManager")
@Scope("singleton")
public class SessionShutdownManager {
    private static final Logger logger = LoggerFactory.getLogger(SessionShutdownManager.class);
    private final LocalDataSynchronizer localDataSynchronizer;
    private final ApplicationContext applicationContext;

    @Autowired
    public SessionShutdownManager(
            LocalDataSynchronizer localDataSynchronizer,
            ApplicationContext applicationContext
    ) {
        this.localDataSynchronizer = localDataSynchronizer;
        this.applicationContext = applicationContext;
    }

    @PreDestroy
    private void synchronize() {
        logger.info(LoggingConstants.PREDESTROY_USER_SYNC_STARTED);
        localDataSynchronizer.synchronize("-u");
        logger.info(LoggingConstants.PREDESTROY_USER_SYNC_FINISHED);
        logger.info(LoggingConstants.PREDESTROY_SCHEDULE_SYNC_STARTED);
        localDataSynchronizer.synchronize("-s");
        logger.info(LoggingConstants.PREDESTROY_SCHEDULE_SYNC_FINISHED);
    }

    public void shutdown() {
        logger.info(LoggingConstants.APPLICATION_CONTEXT_TERMINATION_STARTED);
        SpringApplication.exit(applicationContext);
        logger.info(LoggingConstants.APPLICATION_CONTEXT_TERMINATION_FINISHED);
        System.exit(0);
    }
}

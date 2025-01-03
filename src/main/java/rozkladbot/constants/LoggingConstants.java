package rozkladbot.constants;

public class LoggingConstants {
    public static final String LOCAL_FILE_READING_FAILED_MESSAGE = "Error while parsing local-saved data file. Caused by: {}";
    public static final String EXECUTOR_EXCEPTION_MESSAGE = "Executor executed unsuccessfully! Caused by: {}";
    public static final String STARTED_READING_LOCAL_FILE_MESSAGE = "Started reading local saved data file by path: {}";
    public static final String USERS_MIGRATION_STARTED = "Loading users...";
    public static final String USERS_MIGRATION_FINISHED = "Successfully loaded {} users.";
    private LoggingConstants() {}
}

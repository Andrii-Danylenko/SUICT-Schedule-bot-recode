package rozkladbot.constants;

public class LoggingConstants {
    public static final String LOCAL_FILE_READING_FAILED_MESSAGE = "Error while parsing local-saved data file. Caused by: {}";
    public static final String EXECUTOR_EXCEPTION_MESSAGE = "Executor executed unsuccessfully! Caused by: {}";
    public static final String STARTED_READING_LOCAL_FILE_MESSAGE = "Started reading local saved data file by path: {}";
    public static final String USERS_MIGRATION_STARTED = "Loading users...";
    public static final String USERS_MIGRATION_FINISHED = "Successfully loaded {} users.";
    public static final String BEGIN_BROADCAST_MESSAGE = "Beginning schedule broadcasting...";
    public static final String END_BROADCAST_MESSAGE = "Successfully finished schedule broadcasting.";
    public static final String TRYING_TO_UNPIN_MESSAGE = "Attempting to unpin user's (id: {}) message...";
    public static final String MESSAGE_UNPINNED_SUCCESSFULLY = "Attempt to unpin user's (id: {}) message finished successfully.";
    public static final String TRYING_TO_PIN_MESSAGE = "Attempting to pin user's (id: {}) message...";
    public static final String MESSAGE_PINNED_SUCCESSFULLY = "Attempt to pin user's (id: {}) message finished successfully.";
    public static final String SCHEDULE_DUMPING_BEGAN = "Attempting to create local copy of schedule tables...";
    public static final String SCHEDULE_DUMPING_BEGAN_FOR_GROUP =
            "Attempting to create local copy of schedule for group. It's group id: {} and group name: {}.";
    public static final String SCHEDULE_DUMPING_FAILED = "Failed to dump schedule for group with id: {}.";
    public static final String SCHEDULE_DUMPED_SUCCESSFULLY = "File {} successfully written";
    public static final String SCHEDULE_DUMPING_SKIPPING_FILE = "File {} has already been dumped. Skipping...";
    public static final String SCHEDULE_CREATE_IF_NOT_EXISTS = "File {} does not exist. Writing began...";
    public static final String PREDESTROY_USER_SYNC_STARTED = "Started attempt to synchronize local users with DB.";
    public static final String PREDESTROY_USER_SYNC_FINISHED = "Finished attempt to synchronize local users with DB.";
    public static final String PREDESTROY_SCHEDULE_SYNC_STARTED = "Started attempt to write schedules to local file.";
    public static final String PREDESTROY_SCHEDULE_SYNC_FINISHED = "Finished attempt to write schedules to local file.";
    public static final String SYNCHRONIZATION_STARTED = "Beginning synchronization with following keys: {}.";
    public static final String SYNCHRONIZATION_FINISHED = "Finished synchronization with following keys: {}.";
    public static final String APPLICATION_CONTEXT_TERMINATION_STARTED = "Application termination started.";
    public static final String APPLICATION_CONTEXT_TERMINATION_FINISHED = "Application termination finished.";
    public static final String USERS_LIST_FILE_FOUND = "File with users found. Trying to migrate...";
    public static final String USERS_LIST_MIGRATION_FINISHED = "Finished migrating users to DB from local file.";
    public static final String USERS_SYNC_STARTED = "Started attempt to synchronize local users with DB.";
    public static final String USERS_SYNC_FINISHED = "Finished attempt to synchronize local users with DB.";
    private LoggingConstants() {}
}

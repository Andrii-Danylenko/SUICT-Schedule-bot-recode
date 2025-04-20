package rozkladbot.constants;

public class AppConstants {
    public static final String GROUP_SCHEDULES_FOLDER_NAME = "groupsSchedules";
    public static final String CURRENT_WEEK_LOCAL_SCHEDULE_PATH = GROUP_SCHEDULES_FOLDER_NAME + "/%s(%d)_thisWeek.json";
    public static final String NEXT_WEEK_LOCAL_SCHEDULE_PATH = GROUP_SCHEDULES_FOLDER_NAME + "/%s(%d)_nextWeek.json";
    public static final String GROUPS_LINKS_FOLDER_NAME = "groupsPairLinks";
    public static final String HOORAY_FREE_DAY = "Пар немає %s! Відпочиваємо%s!%n";
    public static final String DEVELOPER_TG_LINK = "https://t.me/optionalOfNullable";
    public static final String APPLICATION_TIME_ZONE = "Europe/Kyiv";
    public static final String BROADCASTING_CRON = "0 0 19 * * *";
    public static final String USER_SYNC_CRON = "0 0 4 * * *";
    public static final String DATE_FORMAT = "dd.MM.yyyy";
    public static final String DAY_FORMAT = "EEEE";
    public static final String APP_LOCALE_LANGUAGE = "uk";
    public static final String APP_LOCALE_COUNTRY = "UA";
    public static final String SCHEDULE_DUMP_CRON = "0 0 3 * * *";
    public static final String THIS_WEEK_SCHEDULE_FILE_NAME = "%s(%d)_thisWeek.json";
    public static final String NEXT_WEEK_SCHEDULE_FILE_NAME = "%s(%d)_nextWeek.json";
    public static final String MONDAY = "Понеділок";
    public static final String JSON_TREE_SCHEDULE_OBJECT_NAME = "schedule";
    public static final String JSON_TREE_PAIR_LINKS_OBJECT_NAME = "pairLinks";
    public static final String JSON_TREE_USER_OBJECT_NAME = "users";
    public static final String JSON_TREE_GROUPS_OBJECT_NAME = "groups";
    public static final String BOT_MESSAGE_PARSE_MODE = "html";
    public static final String CUSTOM_SCHEDULE_INPUT_FORMAT_REGEX = "\\sз\\s|\\sпо\\s";
    public static final String MIGRATION_STARTED = "Beginning migration...";
    public static final String MIGRATION_FINISHED = "Finished migration.";
    public static final String MIGRATION_FAILED = "Migration failed. Error message: {}";
    public static final String HTTP_METHOD_GET = "GET";
    public static final String APPLICATION_JSON = "application/json";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String QUERY_PARAMETER_DELIMITER = "&";
    public static final String QUERY_PARAMETER_KEY_SEPARATOR = "=";
    public static final String FACULTY = "faculty";
    public static final String COURSE = "course";
    public static final String INSTITUTION = "institution";
    public static final String GROUP = "group";
    public static final String GROUP_NAME = GROUP + "Name";
    public static final String QUERY_START_DATE =  "dateFrom";
    public static final String QUERY_END_DATE =  "dateTo";
    private AppConstants() {
    }
}

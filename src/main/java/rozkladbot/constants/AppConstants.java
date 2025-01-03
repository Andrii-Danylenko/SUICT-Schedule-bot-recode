package rozkladbot.constants;

public class AppConstants {
    public static final String CURRENT_WEEK_LOCAL_SCHEDULE_PATH = "groupsSchedules/%s(%d)_thisWeek.json";
    public static final String NEXT_WEEK_LOCAL_SCHEDULE_PATH = "groupsSchedules/%s(%d)_nextWeek.json";
    public static final String HOORAY_FREE_DAY = "Пар немає %s! Відпочиваємо%s!%n";
    public static final String DEVELOPER_TG_LINK = "https://t.me/optionalOfNullable";
    public static final String APPLICATION_TIME_ZONE = "Europe/Kyiv";
    public static final String BROADCASTING_CRON = "0 0 19 * * *";
    public static final String DATE_FORMAT = "dd.MM.yyyy";
    public static final String DAY_FORMAT = "EEEE";
    public static final String APP_LOCALE_LANGUAGE = "uk";
    public static final String APP_LOCALE_COUNTRY = "UA";
    public static final String SCHEDULE_DUMP_CRON = "0 0 3 * * *";
    public static final String THIS_WEEK_SCHEDULE_FILE_NAME = "%s(%d)_thisWeek.json";
    public static final String NEXT_WEEK_SCHEDULE_FILE_NAME = "%s(%d)_nextWeek.json";
    public static final String GROUP_SCHEDULES_FOLDER_NAME = "groupsSchedules";
    public static final String MONDAY = "Понеділок";
    public static final String JSON_TREE_OBJECT_NAME = "schedule";
    private AppConstants() {}
}

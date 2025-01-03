package rozkladbot.constants;

public class BotMessageConstants {
    public static final String USER_IS_UNREGISTERED =
            """
                    Схоже, що ви не зареєстровані.
                    """;
    public static final String INSTITUTE_SELECTION =
            """
                    Виберіть інститут.
                    Інститути, які наразі підтримуються:
                    """;
    public static final String FACULTY_SELECTION =
            """
                    Виберіть факультет.
                    Факультети, які наразі підтримуються:
                    """;
    public static final String COURSE_SELECTION =
            """
                    Виберіть курс.
                    Курси, які наразі підтримуються:
                    """;
    public static final String GROUP_SELECTION =
            """
                    Виберіть групу.
                    Групи, які наразі підтримуються:
                    """;
    public static final String GREETING_MESSAGE =
            """
                    Привіт! Цей бот створений, щоб зручно проглядати розклад ДУІКТ.
                    """;

    public static final String AVAILABLE_USER_COMMANDS =
            """
                    Ось команди, які ти можеш використати:
                    
                    /day - розклад на сьогодні.
                    /nextDay - розклад на завтра.
                    /week - розклад на цей тиждень.
                    /nextWeek - розклад на наступний тиждень.
                    /custom - розклад за своїми параметрами.
                    /settings - змінити налаштування.""";
    public static final String DOES_NOT_CONTAIN_INSTITUTE = "Немає твого інститута?";
    public static final String DOES_NOT_CONTAIN_FACULTY = "Немає твого факультета?";
    public static final String DOES_NOT_CONTAIN_COURSE = "Немає твого курса?";
    public static final String DOES_NOT_CONTAIN_GROUP = "Немає твоєї групи?";
    public static final String CONFIRM_REGISTRATION_DATA = "Твоя група: %s?";
    public static final String REGISTRATION_FAILED = "\u200E(ﾉಥ益ಥ）ﾉ\uFEFF ┻━┻";
    public static final String REGISTRATION_SUCCESSFUL = """
            Ти успішно зареєструвався!
            Тепер можна користуватися ботом.
            """;
    public static final String SCHEDULE_FETCHING_FAILED = """
            Виникла помилка під час отримання розкладу.
            Спробуйте пізніше. 🥺
            """;
    public static final String GET_SCHEDULE_ATTEMPT = "Намагаюся отримати розклад...";
    public static final String TODAY_SCHEDULE = "\uD83E\uDD13 Розклад на сьогодні:\n\n";
    public static final String TOMORROW_SCHEDULE = "\uD83E\uDD13 Розклад на завтра:\n\n";
    public static final String WEEKLY_SCHEDULE = "\uD83E\uDD13 Розклад на цей тиждень:\n\n";
    public static final String NEXT_WEEK_SCHEDULE = "\uD83E\uDD13 Розклад на наступний тиждень:\n\n";
    public static final String BROADCASTING_FAILED = "Не вдалося отримати розклад під час широкомовної розсилки :(";
    public static final String SETTINGS_MENU = """
            Група: %s
            Номер групи: %d
            Курс: %s
            Щоденні повідомлення: %s
            """;
    public static final String IS_BROADCASTED = "Увімкнені";
    public static final String IS_NOT_BROADCASTED = "Вимкнені";
    private BotMessageConstants() {
    }
}

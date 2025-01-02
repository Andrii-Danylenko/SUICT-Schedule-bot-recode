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
    private BotMessageConstants() {
    }
}

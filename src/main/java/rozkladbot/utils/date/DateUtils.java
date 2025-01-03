package rozkladbot.utils.date;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtils {
    private static final ZoneId zoneId = ZoneId.of("Europe/Kyiv");
    public static final DateTimeFormatter JSON_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static final DateTimeFormatter FULL_DAY_NAME = DateTimeFormatter.ofPattern("EEEE").withLocale(Locale.of("uk", "ua"));

    public static LocalDate getToday() {
        return LocalDate.now(zoneId);
    }

    public static String getTodayDateString() {
        return getToday().format(JSON_DATE_FORMATTER);
    }

    public static String getTodayPlusDaysAsString(int days) {
        return getToday().plusDays(days).format(JSON_DATE_FORMATTER);
    }

    public static LocalDate getStartOfWeek(String date) {
        LocalDate inputDate = LocalDate.parse(date, JSON_DATE_FORMATTER);
        DayOfWeek firstDayOfWeek = DayOfWeek.MONDAY;
        int daysUntilStartOfWeek = inputDate.getDayOfWeek().getValue() - firstDayOfWeek.getValue();
        return inputDate.minusDays(daysUntilStartOfWeek);
    }

    public static String getDateAsString(LocalDate date) {
        return date.format(JSON_DATE_FORMATTER);
    }

    public static String getFullDayName(LocalDate date) {
        return date.format(FULL_DAY_NAME);
    }

    public static String getMonthName(LocalDate date) {
        return switch (date.getMonthValue()) {
            case 1 -> "січня";
            case 2 -> "лютого";
            case 3 -> "березня";
            case 4 -> "квітня";
            case 5 -> "травня";
            case 6 -> "червня";
            case 7 -> "липня";
            case 8 -> "серпня";
            case 9 -> "вересня";
            case 10 -> "жовтня";
            case 11 -> "листопада";
            case 12 -> "грудня";
            default -> "undefined";
        };
    }
}

package rozkladbot.utils.date;

import rozkladbot.constants.AppConstants;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static rozkladbot.constants.AppConstants.*;

public class DateUtils {
    private static final ZoneId zoneId = ZoneId.of(AppConstants.APPLICATION_TIME_ZONE);
    public static final DateTimeFormatter JSON_DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    public static final DateTimeFormatter FULL_DAY_NAME = DateTimeFormatter.ofPattern(DAY_FORMAT).withLocale(Locale.of(APP_LOCALE_LANGUAGE, APP_LOCALE_COUNTRY));

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

    public static LocalDate parseFromString(String date) {
        return LocalDate.parse(date, JSON_DATE_FORMATTER);
    }

    public static String getDateAsString(LocalDate date) {
        return date.format(JSON_DATE_FORMATTER);
    }

    public static String getFullDayName(LocalDate date) {
        return date.atStartOfDay(zoneId).format(FULL_DAY_NAME);
    }

    public static LocalDate instantToLocalDate(Instant instant) {
        return LocalDate.ofInstant(instant, zoneId);
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

    public static String convertLatinDayNameToUkrainian(String dayName) {
        switch (dayName.toLowerCase()) {
            case "monday" -> {
                return "понеділок";
            }
            case "tuesday" -> {
                return "вівторок";
            }
            case "wednesday" -> {
                return "середа";
            }
            case "thursday" -> {
                return "четвер";
            }
            case "friday" -> {
                return "пʼятниця";
            }
            case "saturday" -> {
                return "субота";
            }
            case "sunday" -> {
                return "неділя";
            }
            default -> {
                return "undefined";
            }
        }
    }
}

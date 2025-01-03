package rozkladbot.entities;

import rozkladbot.constants.AppConstants;
import rozkladbot.constants.EmojiList;
import rozkladbot.utils.date.DateUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Day {
    private String dayOfWeek;
    private LocalDate day;
    private List<Lesson> lessons;

    public Day(String dayOfWeek, LocalDate day, List<Lesson> lessons) {
        this.dayOfWeek = dayOfWeek;
        this.day = day;
        this.lessons = lessons;
    }

    public Day() {
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Day day1 = (Day) o;
        return Objects.equals(dayOfWeek, day1.dayOfWeek) && Objects.equals(day, day1.day) && Objects.equals(lessons, day1.lessons);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOfWeek, day, lessons);
    }

    @Override
    public String toString() {
        return "Day{" +
               "dayOfWeek='" + dayOfWeek + '\'' +
               ", day=" + day +
               ", lessons=" + lessons +
               '}';
    }

    public boolean isStub() {
        return lessons == null;
    }

    public String toStringIfMany() {
        boolean hasPairs = false;
        StringBuilder builder = new StringBuilder();
        builder.append("%s  <b>%s, %s %s %s</b>".formatted(
                EmojiList.CALENDAR, dayOfWeek,
                day.getDayOfMonth(),
                DateUtils.getMonthName(day),
                Day.getAmountOfPairs(lessons.size()))).append("\n");
        for (Lesson clazz : lessons) {
            hasPairs = true;
            builder.append("""
                    %s  <i>%s</i> - <b>%s</b> [%s]
                    """.formatted(
                    EmojiList.getPairEmoji(Integer.parseInt(clazz.getPairNumber())),
                    Lesson.getPairTime(clazz.getPairNumber()),
                    clazz.getLessonFullName(),
                    clazz.getType()));
        }
        System.out.println(hasPairs);
        return hasPairs ? builder.toString() : builder.append(
                AppConstants.HOORAY_FREE_DAY.formatted(EmojiList.HAPPY, EmojiList.BEER)
        ).toString();
    }

    public String toStringIfOne() {
        boolean hasPairs = false;
        StringBuilder builder = new StringBuilder();
        builder.append("%s <b>%s, %s %s %s</b>%n".formatted(
                EmojiList.CALENDAR,
                dayOfWeek,
                day.getDayOfMonth(),
                DateUtils.getMonthName(day),
                Day.getAmountOfPairs(lessons.size()))).append("\n");
        for (Lesson clazz : lessons) {
            hasPairs = true;
            builder.append("""
                    %s пара | <b>%s</b>
                    %s <b>%s</b> [%s]
                    %s %s
                    %s каб. %s
                    
                    """.formatted(
                    EmojiList.getPairEmoji(Integer.parseInt(clazz.getPairNumber())),
                    Lesson.getPairTime(clazz.getPairNumber()),
                    EmojiList.SUBJECT,
                    clazz.getLessonFullName(),
                    clazz.getType(),
                    EmojiList.LECTOR,
                    clazz.getLectorFullName(),
                    EmojiList.ROOM,
                    clazz.getCabinet()));
        }
        System.out.println(hasPairs);
        return hasPairs ? builder.toString() : builder.append(
                        AppConstants.HOORAY_FREE_DAY.formatted(EmojiList.HAPPY, EmojiList.BEER))
                .toString();
    }

    public static String getAmountOfPairs(int amountOfDays) {
        return switch (amountOfDays) {
            case 1 -> "(1 пара)";
            case 2 -> "(2 пари)";
            case 3 -> "(3 пари)";
            case 4 -> "(4 пари)";
            case 5 -> "(5 пар)";
            case 6 -> "(6 пар)";
            default -> "(пар немає)";
        };
    }
}

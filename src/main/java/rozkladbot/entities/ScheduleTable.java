package rozkladbot.entities;

import rozkladbot.constants.AppConstants;
import rozkladbot.constants.EmojiList;
import rozkladbot.utils.date.DateUtils;

import java.util.List;

public class ScheduleTable {
    private List<Day> days;

    public ScheduleTable() {
    }

    public ScheduleTable(List<Day> days) {
        this.days = days;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (days.size() > 1) {
            days.forEach(day -> builder.append(day.toStringIfMany()).append("\n"));
        } else if (days.size() == 1) {
            days.forEach(day -> builder.append(day.toStringIfOne()).append("\n"));
        } else {
            builder.append("%s  <b>%s, %s %s %s%n%n</b>".formatted(
                            EmojiList.CALENDAR, DateUtils.getFullDayName(DateUtils.getToday()),
                            DateUtils.getToday().getDayOfMonth(),
                            DateUtils.getMonthName(DateUtils.getToday()),
                            Day.getAmountOfPairs(0)))
                    .append(AppConstants.HOORAY_FREE_DAY.formatted(EmojiList.HAPPY, EmojiList.BEER)
                    );
        }
        return builder.toString();
    }

    public List<Day> getDays() {
        return days;
    }
}

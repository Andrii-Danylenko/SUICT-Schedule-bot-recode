package rozkladbot.entities;

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
        } else {
            days.forEach(day -> builder.append(day.toStringIfOne()).append("\n"));
        }
        return builder.toString();
    }

    public List<Day> getDays() {
        return days;
    }
}

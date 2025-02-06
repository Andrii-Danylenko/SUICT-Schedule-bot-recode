package rozkladbot.entities;

import java.util.ArrayDeque;
import java.util.Deque;

public class ScheduleTable {
    private Deque<Day> days;

    public ScheduleTable() {
        days = new ArrayDeque<>();
    }

    public ScheduleTable(Deque<Day> days) {
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

    public Deque<Day> getDays() {
        return days;
    }
    public void setDays(Deque<Day> days) {
        this.days = days;
    }
}

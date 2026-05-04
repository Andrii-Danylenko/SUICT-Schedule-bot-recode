package rozkladbot.entities;

import java.util.ArrayDeque;
import java.util.Deque;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ScheduleTable {
    private Deque<Day> days;

    public ScheduleTable() {
        days = new ArrayDeque<>();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (days.size() > 1) {
            days.forEach(day -> builder.append(day.toStringFormatted(true)).append("\n"));
        } else {
            days.forEach(day -> builder.append(day.toStringFormatted(false)).append("\n"));
        }
        return builder.toString();
    }

}

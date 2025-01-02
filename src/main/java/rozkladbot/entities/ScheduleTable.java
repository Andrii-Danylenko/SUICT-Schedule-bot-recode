package rozkladbot.entities;

import java.util.List;

public class ScheduleTable {
    private List<Day> days;

    public ScheduleTable() {
    }

    public ScheduleTable(List<Day> days) {
        this.days = days;
    }
}

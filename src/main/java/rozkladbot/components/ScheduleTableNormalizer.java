package rozkladbot.components;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import rozkladbot.entities.Day;
import rozkladbot.entities.Lesson;
import rozkladbot.entities.ScheduleTable;
import rozkladbot.utils.date.DateUtils;

@Component
public class ScheduleTableNormalizer {

  public ScheduleTable splitBigTableIntoSmall(ScheduleTable scheduleTable) {
    ScheduleTable scheduleToReturn = new ScheduleTable();
    int scheduleTableSize = scheduleTable.getDays().size();
    for (int partition = 1; scheduleTableSize > 0; partition++) {
      scheduleToReturn.getDays().add(scheduleTable.getDays().pollFirst());
      scheduleTableSize = scheduleTable.getDays().size();
      if (partition % 8 == 0) {
        return scheduleToReturn;
      }
    }
    return scheduleToReturn;
  }

  public Deque<Day> splitByDays(Deque<Lesson> lessons, LocalDate startDate, LocalDate endDate) {
    TreeMap<LocalDate, List<Lesson>> lessonsByDay = lessons
        .stream()
        .filter(
            lesson -> !lesson.getDate().isBefore(startDate) && !lesson.getDate().isAfter(endDate))
        .collect(Collectors.groupingBy(Lesson::getDate, TreeMap::new, Collectors.toList()));
    ensureNoBreaks(lessonsByDay, startDate, endDate);
    Deque<Day> days = new ArrayDeque<>();
    for (Map.Entry<LocalDate, List<Lesson>> entry : lessonsByDay.entrySet()) {
      Day day = new Day();
      day.setDay(entry.getKey());
      day.setLessons(entry.getValue());
      day.setDayOfWeek(DateUtils.getFullDayName(entry.getKey()));
      days.add(day);
    }
    for (Day day : days) {
      day.getLessons().sort(Comparator.comparing(Lesson::getPairNumber));
    }
    return days;
  }

  private void ensureNoBreaks(TreeMap<LocalDate, List<Lesson>> lessonsByDay, LocalDate startDate,
      LocalDate endDate) {
    for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
      lessonsByDay.putIfAbsent(date, Collections.emptyList());
    }
  }

}

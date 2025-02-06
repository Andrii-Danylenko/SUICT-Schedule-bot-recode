package rozkladbot.controllers.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rozkladbot.entities.Day;
import rozkladbot.entities.Lesson;
import rozkladbot.entities.ScheduleTable;
import rozkladbot.enums.OfflineReadingMode;
import rozkladbot.services.ScheduleService;
import rozkladbot.utils.date.DateUtils;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/schedule")
public class RestScheduleController {
    private final ScheduleService scheduleService;

    public RestScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/get")
    public ResponseEntity<ScheduleTable> getSchedule(
            @RequestParam long instituteId,
            @RequestParam long faculty,
            @RequestParam long course,
            @RequestParam long group,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) String lessonType,
            @RequestParam(required = false) String dayName
    ) {
        try {
            ScheduleTable scheduleTable = scheduleService.getSchedule(
                    instituteId,
                    faculty,
                    course,
                    group,
                    DateUtils.parseFromString(startDate), DateUtils.parseFromString(endDate),
                    OfflineReadingMode.NONE);
            // Filter by day name in ukrainian
            if (dayName != null) {
                String convertedDayName = DateUtils.convertLatinDayNameToUkrainian(dayName);
                Deque<Day> filteredDays = scheduleTable
                        .getDays()
                        .stream()
                        .filter(d -> convertedDayName.equalsIgnoreCase(d.getDayOfWeek()))
                        .collect(LinkedList::new, LinkedList::add, LinkedList::addAll);
                scheduleTable.setDays(filteredDays);
            }
            // Filter by lesson type e.g. practice, lecture, lab
            if (lessonType != null) {
                Deque<Day> filteredDays = scheduleTable.getDays()
                        .stream()
                        .map(d -> {
                            List<Lesson> filteredLessons = d.getLessons()
                                    .stream()
                                    .filter(l -> lessonType.equalsIgnoreCase(l.getType()))
                                    .toList();
                            return new Day(d.getDayOfWeek(), d.getDay(), filteredLessons);
                        })
                        .filter(d -> !d.getLessons().isEmpty())
                        .collect(Collectors.toCollection(LinkedList::new));

                scheduleTable.setDays(filteredDays);
            }

            return ResponseEntity.ok(scheduleTable);
        } catch (Exception exception) {
            // TODO: implement ControllerAdvice for controller exceptions
            exception.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

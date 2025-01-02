package rozkladbot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import rozkladbot.entities.Day;
import rozkladbot.entities.User;
import rozkladbot.services.ScheduleService;

import java.util.List;
import java.util.concurrent.ExecutionException;

@SpringBootTest
public class ScheduleServiceImplTest {
    @Autowired
    private ScheduleService scheduleService;

    @Test
    void testGetTodaySchedule() throws ExecutionException, InterruptedException {
        User user = ModelUtils.getUser();
        List<Day> lessonList = scheduleService.getTodayLessons(user);
        Assertions.assertFalse(lessonList.isEmpty());
    }

    @Test
    void testGetWeeklySchedule() throws ExecutionException, InterruptedException {
        User user = ModelUtils.getUser();
        List<Day> lessonList = scheduleService.getWeeklyLessons(user);
        lessonList.forEach(lesson -> {
            System.out.println(lesson.toStringIfOne());
        });
        Assertions.assertFalse(lessonList.isEmpty());
    }
}

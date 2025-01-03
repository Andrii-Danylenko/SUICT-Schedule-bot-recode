package rozkladbot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import rozkladbot.entities.ScheduleTable;
import rozkladbot.entities.User;
import rozkladbot.services.ScheduleService;

import java.util.concurrent.ExecutionException;

@SpringBootTest
public class ScheduleServiceImplTest {
    @Autowired
    private ScheduleService scheduleService;

    @Test
    void testGetTodaySchedule() throws ExecutionException, InterruptedException {
        User user = ModelUtils.getUser();
        ScheduleTable scheduleTable = scheduleService.getTodayLessons(user);
        Assertions.assertFalse(scheduleTable.getDays().isEmpty());
    }

    @Test
    void testGetWeeklySchedule() throws ExecutionException, InterruptedException {
        User user = ModelUtils.getUser();
        ScheduleTable scheduleTable = scheduleService.getWeeklyLessons(user);
        Assertions.assertFalse(scheduleTable.getDays().isEmpty());
    }
}

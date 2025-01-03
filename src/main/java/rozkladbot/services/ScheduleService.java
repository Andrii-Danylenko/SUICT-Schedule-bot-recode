package rozkladbot.services;

import rozkladbot.entities.ScheduleTable;
import rozkladbot.entities.User;

import java.util.concurrent.ExecutionException;

public interface ScheduleService {
    ScheduleTable getTodayLessons(User user) throws ExecutionException, InterruptedException;

    ScheduleTable getTomorrowLessons(User user) throws ExecutionException, InterruptedException;

    ScheduleTable getWeeklyLessons(User user) throws ExecutionException, InterruptedException;

    ScheduleTable getNextWeekLessons(User user) throws ExecutionException, InterruptedException;
}

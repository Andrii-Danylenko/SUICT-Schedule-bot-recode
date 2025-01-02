package rozkladbot.services;

import rozkladbot.entities.Day;
import rozkladbot.entities.User;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface ScheduleService {
    List<Day> getTodayLessons(User user) throws ExecutionException, InterruptedException;

    List<Day> getTomorrowLessons(User user) throws ExecutionException, InterruptedException;

    List<Day> getWeeklyLessons(User user) throws ExecutionException, InterruptedException;

    List<Day> getNextWeekLessons(User user) throws ExecutionException, InterruptedException;
}

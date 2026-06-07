package rozkladbot.services;

import org.telegram.telegrambots.meta.api.objects.Update;
import rozkladbot.entities.ScheduleTable;
import rozkladbot.entities.User;
import rozkladbot.enums.CachePeriod;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.concurrent.ExecutionException;
import rozkladbot.enums.ScheduleServiceType;

public interface ScheduleService {

  ScheduleTable getTodayLessons(User user) throws ExecutionException, InterruptedException;

  ScheduleTable getTomorrowLessons(User user) throws ExecutionException, InterruptedException;

  ScheduleTable getWeeklyLessons(User user) throws ExecutionException, InterruptedException;

  ScheduleTable getNextWeekLessons(User user) throws ExecutionException, InterruptedException;


  ScheduleTable getScheduleWithCustomParameters(User user, Update update)
      throws IOException, URISyntaxException, InterruptedException;

  ScheduleTable getSchedule(
      long institute,
      long faculty,
      long course,
      long groupOfficialId,
      long groupId,
      LocalDate queryDateStart,
      LocalDate queryDateEnd,
      CachePeriod mode) throws ExecutionException, InterruptedException;

  boolean supports(ScheduleServiceType serviceType);
}


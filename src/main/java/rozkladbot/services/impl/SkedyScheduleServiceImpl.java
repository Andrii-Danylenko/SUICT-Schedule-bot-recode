package rozkladbot.services.impl;

import java.time.DayOfWeek;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import rozkladbot.components.ScheduleTableNormalizer;
import rozkladbot.constants.ApiEndpoints;
import rozkladbot.constants.AppConstants;
import rozkladbot.entities.*;
import rozkladbot.enums.CachePeriod;
import rozkladbot.enums.ScheduleServiceType;
import rozkladbot.enums.ScheduleType;
import rozkladbot.exceptions.CustomScheduleFetchException;
import rozkladbot.exceptions.RequestCreationFailedException;
import rozkladbot.services.GroupService;
import rozkladbot.services.PairLinkService;
import rozkladbot.services.ScheduleCacheService;
import rozkladbot.services.ScheduleService;
import rozkladbot.telegram.utils.files.reader.LocalFileReader;
import rozkladbot.telegram.utils.parser.MessageParser;
import rozkladbot.utils.date.DateUtils;
import rozkladbot.json.deserializers.LessonDeserializer;
import rozkladbot.services.web.urlbuilder.QueryBuilder;
import rozkladbot.services.web.requestservice.WebRequestService;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static rozkladbot.constants.AppConstants.CURRENT_WEEK_LOCAL_SCHEDULE_PATH;
import static rozkladbot.constants.AppConstants.GROUP;
import static rozkladbot.constants.AppConstants.GROUP_ID;
import static rozkladbot.constants.AppConstants.GROUP_NAME;
import static rozkladbot.constants.AppConstants.NEXT_WEEK_LOCAL_SCHEDULE_PATH;
import static rozkladbot.constants.ErrorConstants.REQUEST_CREATION_FAILED;
import static rozkladbot.constants.LoggingConstants.EXECUTOR_EXCEPTION_MESSAGE;
import static rozkladbot.enums.CachePeriod.NEXT_WEEK;
import static rozkladbot.enums.CachePeriod.THIS_WEEK;

@Service("skedyScheduleServiceImpl")
@RequiredArgsConstructor
public class SkedyScheduleServiceImpl implements ScheduleService {

  private static final Logger logger = LoggerFactory.getLogger(SkedyScheduleServiceImpl.class);
  private final ScheduleTableNormalizer scheduleTableNormalizer;
  private final WebRequestService webRequestService;
  private final QueryBuilder queryBuilder;
  private final LessonDeserializer lessonDeserializer;
  private final LocalFileReader localFileReader;
  private final MessageParser messageParser;
  private final PairLinkService pairLinkService;
  private final GroupService groupService;
  private final ScheduleCacheService scheduleCacheService;
  private final ExecutorService virtualThreadExecutor;

  @Override
  public ScheduleTable getTodayLessons(User user) throws ExecutionException, InterruptedException {
    LocalDate queryDateStart = DateUtils.getToday();
    return getSchedule(
        user.getGroup().getFaculty().getInstitute().getId(),
        user.getGroup().getFaculty().getFacultyId(),
        user.getGroup().getCourse(),
        user.getGroup().getGroupId(),
        user.getGroup().getId(),
        queryDateStart,
        queryDateStart,
        THIS_WEEK);
  }

  @Override
  public ScheduleTable getTomorrowLessons(User user)
      throws ExecutionException, InterruptedException {
    LocalDate queryDateStart = DateUtils.getToday().plusDays(1);
    return getSchedule(
        user.getGroup().getFaculty().getInstitute().getId(),
        user.getGroup().getFaculty().getFacultyId(),
        user.getGroup().getCourse(),
        user.getGroup().getGroupId(),
        user.getGroup().getId(),
        queryDateStart,
        queryDateStart,
        Objects.equals(queryDateStart.getDayOfWeek(), DayOfWeek.MONDAY) ? NEXT_WEEK : THIS_WEEK);
  }

  @Override
  public ScheduleTable getWeeklyLessons(User user) throws ExecutionException, InterruptedException {
    LocalDate queryDateStart = DateUtils.getStartOfWeek(DateUtils.getTodayDateString());
    LocalDate queryDateEnd = queryDateStart.plusDays(6);
    return getSchedule(
        user.getGroup().getFaculty().getInstitute().getId(),
        user.getGroup().getFaculty().getFacultyId(),
        user.getGroup().getCourse(),
        user.getGroup().getGroupId(),
        user.getGroup().getId(),
        queryDateStart,
        queryDateEnd,
        THIS_WEEK);
  }

  @Override
  public ScheduleTable getNextWeekLessons(User user)
      throws ExecutionException, InterruptedException {
    LocalDate queryDateStart = DateUtils.getStartOfWeek(DateUtils.getTodayDateString()).plusDays(7);
    LocalDate queryDateEnd = queryDateStart.plusDays(6);
    return getSchedule(
        user.getGroup().getFaculty().getInstitute().getId(),
        user.getGroup().getFaculty().getFacultyId(),
        user.getGroup().getCourse(),
        user.getGroup().getGroupId(),
        user.getGroup().getId(),
        queryDateStart,
        queryDateEnd,
        CachePeriod.NEXT_WEEK);
  }

  @Override
  public ScheduleTable getScheduleWithCustomParameters(User user, Update update)
      throws InterruptedException {
    String[] parameters = messageParser.getParametersFromMessage(update.getMessage().getText());
    try {
      String groupAsString = parameters[0];
      LocalDate queryDateStart = DateUtils.parseFromString(parameters[1]);
      LocalDate queryDateEnd = DateUtils.parseFromString(parameters[2]);
      Group group = groupService.getByName(groupAsString);
      return getSchedule(
          group.getFaculty().getInstitute().getId(),
          group.getFaculty().getFacultyId(),
          group.getCourse(),
          group.getGroupId(),
          user.getGroup().getId(),
          queryDateStart,
          queryDateEnd,
          CachePeriod.NONE
      );
    } catch (RuntimeException | ExecutionException e) {
      throw new CustomScheduleFetchException();
    }
  }

  private Deque<Lesson> getSchedule(Map<String, String> params, CachePeriod mode)
      throws ExecutionException, InterruptedException {
    return CompletableFuture.supplyAsync(() -> {
      ScheduleType type = ScheduleType.fromCachePeriod(mode);
      Optional<ScheduleCache> cache = scheduleCacheService.findByGroupIdAndScheduleType(
          Long.parseLong(params.get(AppConstants.GROUP_ID)),
          type);
      if (scheduleCacheService.isCacheValid(cache)) {
        return cache.get().getContent();
      }
      try {
        String content = webRequestService.makeRequest(params, ApiEndpoints.API_SCHEDULE);
        scheduleCacheService.updateContent(
            Long.parseLong(params.get(GROUP_ID)),
            type,
            content
        );
        return content;
      } catch (IOException | URISyntaxException | InterruptedException e) {
        logger.error(REQUEST_CREATION_FAILED);
        throw new RequestCreationFailedException(REQUEST_CREATION_FAILED);
      }
    }, virtualThreadExecutor).exceptionally((ex) -> {
      if (ex != null) {
        logger.error(EXECUTOR_EXCEPTION_MESSAGE, ex.getMessage());
      }
      String result;
      if (mode.equals(THIS_WEEK)) {
        result = localFileReader.readLocalFile(
            CURRENT_WEEK_LOCAL_SCHEDULE_PATH.formatted(
                params.get(GROUP_NAME),
                Long.parseLong(params.get(GROUP))));
      } else if (mode.equals(CachePeriod.NEXT_WEEK)) {
        result = localFileReader.readLocalFile(
            NEXT_WEEK_LOCAL_SCHEDULE_PATH.formatted(
                params.get(GROUP_NAME),
                Long.parseLong(params.get(GROUP))));
      } else {
        throw new CustomScheduleFetchException();
      }
      return result;
    }).thenApply(lessonDeserializer::deserialize).get();
  }

  public ScheduleTable getSchedule(
      long institute,
      long faculty,
      long course,
      long groupOfficialId,
      long groupId,
      LocalDate queryDateStart,
      LocalDate queryDateEnd,
      CachePeriod mode) throws ExecutionException, InterruptedException {
    HashMap<String, String> params = queryBuilder.buildQueryParams(
        groupOfficialId,
        groupId,
        course,
        groupService.findByGroupIdAndFacultyId(groupOfficialId, faculty).getName(),
        faculty,
        institute,
        queryDateStart,
        queryDateEnd
    );
    Deque<Lesson> lessons = getSchedule(params, mode);
    appendPairLinks(groupOfficialId, lessons);
    return new ScheduleTable(
        scheduleTableNormalizer.splitByDays(lessons, queryDateStart, queryDateEnd));
  }

  private void appendPairLinks(long groupId, Deque<Lesson> lessons) {
    lessons.forEach(lesson -> {
          PairLink pairlink = pairLinkService.getByGroupIdAndLessonNameAndLessonType(
              groupId,
              lesson.getLessonFullName(),
              lesson.getType()
          );
          if (pairlink != null) {
            lesson.setPairLink(pairlink.getLink());
          }
        }
    );
  }

  @Override
  public boolean supports(ScheduleServiceType serviceType) {
    return ScheduleServiceType.SKEDY == serviceType;
  }
}

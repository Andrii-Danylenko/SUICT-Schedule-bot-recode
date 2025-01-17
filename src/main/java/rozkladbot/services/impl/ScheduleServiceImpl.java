package rozkladbot.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rozkladbot.constants.AppConstants;
import rozkladbot.constants.LoggingConstants;
import rozkladbot.entities.Day;
import rozkladbot.entities.Lesson;
import rozkladbot.entities.ScheduleTable;
import rozkladbot.entities.User;
import rozkladbot.enums.UserState;
import rozkladbot.exceptions.RequestCreationFailedException;
import rozkladbot.services.ScheduleService;
import rozkladbot.utils.date.DateUtils;
import rozkladbot.utils.deserializers.LessonDeserializer;
import rozkladbot.telegram.utils.files.reader.LocalFileReader;
import rozkladbot.utils.web.requester.ParamsBuilder;
import rozkladbot.utils.web.requester.RequesterImpl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static rozkladbot.constants.ErrorConstants.REQUEST_CREATION_FAILED;

@Service("scheduleServiceImpl")
public class ScheduleServiceImpl implements ScheduleService {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleServiceImpl.class);
    private final RequesterImpl requester;
    private final ParamsBuilder paramsBuilder;
    private final LessonDeserializer lessonDeserializer;
    private final LocalFileReader localFileReader;

    @Autowired
    public ScheduleServiceImpl(LocalFileReader localFileReader, RequesterImpl requester, ParamsBuilder paramsBuilder, LessonDeserializer lessonDeserializer) {
        this.requester = requester;
        this.paramsBuilder = paramsBuilder;
        this.lessonDeserializer = lessonDeserializer;
        this.localFileReader = localFileReader;
    }

    @Override
    public ScheduleTable getTodayLessons(User user) throws ExecutionException, InterruptedException {
        LocalDate queryDateStart = DateUtils.getToday();
        List<Lesson> lessons = getSchedule(buildScheduleParams(user, queryDateStart, queryDateStart), user);
        return new ScheduleTable(splitByDays(lessons, queryDateStart, queryDateStart));
    }

    @Override
    public ScheduleTable getTomorrowLessons(User user) throws ExecutionException, InterruptedException {
        LocalDate queryDateStart = DateUtils.getToday().plusDays(1);
        List<Lesson> lessons = getSchedule(buildScheduleParams(user, queryDateStart, queryDateStart), user);
        return new ScheduleTable(splitByDays(lessons, queryDateStart, queryDateStart));
    }

    @Override
    public ScheduleTable getWeeklyLessons(User user) throws ExecutionException, InterruptedException {
        LocalDate queryDateStart = DateUtils.getStartOfWeek(DateUtils.getTodayDateString());
        LocalDate queryDateEnd = queryDateStart.plusDays(6);
        List<Lesson> lessons = getSchedule(buildScheduleParams(user, queryDateStart, queryDateEnd), user);
        return new ScheduleTable(splitByDays(lessons, queryDateStart, queryDateEnd));
    }

    @Override
    public ScheduleTable getNextWeekLessons(User user) throws ExecutionException, InterruptedException {
        LocalDate queryDateStart = DateUtils.getStartOfWeek(DateUtils.getTodayDateString()).plusDays(7);
        LocalDate queryDateEnd = queryDateStart.plusDays(6);
        List<Lesson> lessons = getSchedule(buildScheduleParams(user, queryDateStart, queryDateEnd), user);
        return new ScheduleTable(splitByDays(lessons, queryDateStart, queryDateEnd));
    }

    private Map<String, String> buildScheduleParams(User user, LocalDate startDate, LocalDate endDate) {
        HashMap<String, String> params = paramsBuilder.build(user);
        params.put("dateFrom", DateUtils.getDateAsString(startDate));
        params.put("dateTo", DateUtils.getDateAsString(endDate));
        return params;
    }

    public String getRawSchedule(User user, LocalDate startDate, LocalDate endDate) throws IOException, URISyntaxException, InterruptedException {
        HashMap<String, String> params = paramsBuilder.build(user);
        params.put("dateFrom", DateUtils.getDateAsString(startDate));
        params.put("dateTo", DateUtils.getDateAsString(endDate));
        return requester.makeRequest(params);
    }

    private List<Day> splitByDays(List<Lesson> lessons, LocalDate startDate, LocalDate endDate) {
        TreeMap<LocalDate, List<Lesson>> lessonsByDay = lessons.stream()
                .collect(Collectors.groupingBy(Lesson::getDate, TreeMap::new, Collectors.toList()));
        if (lessonsByDay.isEmpty()) {
            ensureNoBreaks(lessonsByDay, startDate, endDate);
        } else {
            ensureNoBreaks(lessonsByDay, lessonsByDay.firstKey(), lessonsByDay.lastKey());
        }
        List<Day> days = new ArrayList<>();
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

    private void ensureNoBreaks(TreeMap<LocalDate, List<Lesson>> lessonsByDay, LocalDate startDate, LocalDate endDate) {
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            lessonsByDay.putIfAbsent(date, Collections.emptyList());
        }
    }

    private List<Lesson> getSchedule(Map<String, String> params, User user) throws ExecutionException, InterruptedException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return requester.makeRequest(params);
            } catch (IOException | URISyntaxException | InterruptedException e) {
                logger.error(REQUEST_CREATION_FAILED);
                user.setUserState(UserState.AWAITING_THIS_WEEK_SCHEDULE);
                throw new RequestCreationFailedException(REQUEST_CREATION_FAILED);
            }
        }, Executors.newSingleThreadExecutor()).exceptionally((ex) -> {
            if (ex != null) logger.error(LoggingConstants.EXECUTOR_EXCEPTION_MESSAGE, ex.getMessage());
            String result;
            if (user.getUserState() == UserState.AWAITING_THIS_WEEK_SCHEDULE) {
                result = localFileReader.readLocalFile(
                        AppConstants.CURRENT_WEEK_LOCAL_SCHEDULE_PATH.formatted(
                                user.getGroup().getName(),
                                user.getGroup().getId()));
            } else if (user.getUserState() == UserState.AWAITING_NEXT_WEEK_SCHEDULE) {
                result = localFileReader.readLocalFile(
                        AppConstants.NEXT_WEEK_LOCAL_SCHEDULE_PATH.formatted(
                                user.getGroup().getName(),
                                user.getGroup().getId()));
            } else {
                result = "null";
            }
            user.setUserState(UserState.IDLE);
            return result;
        }).thenApply(lessonDeserializer::deserialize).get();
    }
}

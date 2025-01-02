package rozkladbot.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rozkladbot.constants.AppConstants;
import rozkladbot.constants.LoggingConstants;
import rozkladbot.entities.Day;
import rozkladbot.entities.Lesson;
import rozkladbot.entities.User;
import rozkladbot.enums.UserState;
import rozkladbot.exceptions.RequestCreationFailedException;
import rozkladbot.services.ScheduleService;
import rozkladbot.utils.date.DateUtils;
import rozkladbot.utils.deserializers.LessonDeserializer;
import rozkladbot.utils.local.file.reader.LocalFileReader;
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

    public List<Day> getTodayLessons(User user) throws ExecutionException, InterruptedException {
        HashMap<String, String> params = paramsBuilder.build(user);
        params.put("dateFrom", DateUtils.getTodayDateString());
        params.put("dateTo", DateUtils.getTodayDateString());
        List<Lesson> lessons = getSchedule(params, user);
        return splitByDays(lessons);
    }

    public List<Day> getTomorrowLessons(User user) throws ExecutionException, InterruptedException {
        HashMap<String, String> params = paramsBuilder.build(user);
        params.put("dateFrom", DateUtils.getTodayPlusDaysAsString(1));
        params.put("dateTo", DateUtils.getTodayPlusDaysAsString(1));
        List<Lesson> lessons = getSchedule(params, user);
        return splitByDays(lessons);
    }

    @Override
    public List<Day> getWeeklyLessons(User user) throws ExecutionException, InterruptedException {
        LocalDate startOfWeek = DateUtils.getStartOfWeek(DateUtils.getTodayDateString());
        HashMap<String, String> params = paramsBuilder.build(user);
        params.put("dateFrom", DateUtils.getDateAsString(startOfWeek));
        params.put("dateTo", DateUtils.getDateAsString(startOfWeek.plusDays(6)));
        List<Lesson> lessons = getSchedule(params, user);
        return splitByDays(lessons);
    }

    @Override
    public List<Day> getNextWeekLessons(User user) throws ExecutionException, InterruptedException {
        LocalDate startOfWeek = DateUtils.getStartOfWeek(DateUtils.getTodayDateString());
        HashMap<String, String> params = paramsBuilder.build(user);
        params.put("dateFrom", DateUtils.getDateAsString(startOfWeek.plusDays(7)));
        params.put("dateTo", DateUtils.getDateAsString(startOfWeek.plusDays(13)));
        List<Lesson> lessons = getSchedule(params, user);
        return splitByDays(lessons);
    }

    private List<Day> splitByDays(List<Lesson> lessons) {
        TreeMap<LocalDate, List<Lesson>> lessonsByDay = lessons.stream()
                .collect(Collectors.groupingBy(Lesson::getDate, TreeMap::new, Collectors.toList()));
        if (lessonsByDay.size() > 1) {
            ensureNoBreaks(lessonsByDay);
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

    private void ensureNoBreaks(TreeMap<LocalDate, List<Lesson>> lessonsByDay) {
        if (lessonsByDay.isEmpty()) {
            return;
        }
        LocalDate startDate = lessonsByDay.firstKey();
        LocalDate endDate = lessonsByDay.lastKey();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            lessonsByDay.putIfAbsent(date, Collections.emptyList());
        }
    }

    private List<Lesson> getSchedule(HashMap<String, String> params, User user) throws ExecutionException, InterruptedException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return requester.makeRequest(params);
            } catch (IOException | URISyntaxException e) {
                logger.error(REQUEST_CREATION_FAILED);
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
                        AppConstants.CURRENT_WEEK_LOCAL_SCHEDULE_PATH.formatted(
                                user.getGroup().getName(),
                                user.getGroup().getId()));
            } else {
                result = "null";
            }
            return result;
        }).thenApply(lessonDeserializer::deserialize).get();
    }
}

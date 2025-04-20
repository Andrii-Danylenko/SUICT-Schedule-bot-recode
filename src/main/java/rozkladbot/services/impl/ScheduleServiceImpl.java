package rozkladbot.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import rozkladbot.constants.ApiEndpoints;
import rozkladbot.entities.*;
import rozkladbot.enums.OfflineReadingMode;
import rozkladbot.exceptions.CustomScheduleFetchException;
import rozkladbot.exceptions.RequestCreationFailedException;
import rozkladbot.services.GroupService;
import rozkladbot.services.PairLinkService;
import rozkladbot.services.ScheduleService;
import rozkladbot.telegram.utils.files.reader.LocalFileReader;
import rozkladbot.telegram.utils.parser.MessageParser;
import rozkladbot.utils.date.DateUtils;
import rozkladbot.utils.deserializers.LessonDeserializer;
import rozkladbot.utils.web.requester.ParamsBuilder;
import rozkladbot.utils.web.requester.Requester;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static rozkladbot.constants.AppConstants.CURRENT_WEEK_LOCAL_SCHEDULE_PATH;
import static rozkladbot.constants.AppConstants.GROUP;
import static rozkladbot.constants.AppConstants.GROUP_NAME;
import static rozkladbot.constants.AppConstants.NEXT_WEEK_LOCAL_SCHEDULE_PATH;
import static rozkladbot.constants.ErrorConstants.REQUEST_CREATION_FAILED;
import static rozkladbot.constants.LoggingConstants.EXECUTOR_EXCEPTION_MESSAGE;
import static rozkladbot.enums.OfflineReadingMode.THIS_WEEK;

@Service("scheduleServiceImpl")
public class ScheduleServiceImpl implements ScheduleService {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleServiceImpl.class);
    private final Requester requester;
    private final ParamsBuilder paramsBuilder;
    private final LessonDeserializer lessonDeserializer;
    private final LocalFileReader localFileReader;
    private final MessageParser messageParser;
    private final PairLinkService pairLinkService;
    private final GroupService groupService;

    @Autowired
    public ScheduleServiceImpl(
            LocalFileReader localFileReader,
            Requester requester,
            ParamsBuilder paramsBuilder,
            LessonDeserializer lessonDeserializer,
            MessageParser messageParser,
            PairLinkService pairLinkService,
            GroupService groupService) {
        this.requester = requester;
        this.paramsBuilder = paramsBuilder;
        this.lessonDeserializer = lessonDeserializer;
        this.localFileReader = localFileReader;
        this.messageParser = messageParser;
        this.pairLinkService = pairLinkService;
        this.groupService = groupService;
    }

    @Override
    public ScheduleTable getTodayLessons(User user) throws ExecutionException, InterruptedException {
        LocalDate queryDateStart = DateUtils.getToday();
        return getSchedule(
                user.getGroup().getFaculty().getInstitute().getId(),
                user.getGroup().getFaculty().getFacultyId(),
                user.getGroup().getCourse(),
                user.getGroup().getGroupId(),
                queryDateStart,
                queryDateStart,
                THIS_WEEK);
    }

    @Override
    public ScheduleTable getTomorrowLessons(User user) throws ExecutionException, InterruptedException {
        LocalDate queryDateStart = DateUtils.getToday().plusDays(1);
        return getSchedule(
                user.getGroup().getFaculty().getInstitute().getId(),
                user.getGroup().getFaculty().getFacultyId(),
                user.getGroup().getCourse(),
                user.getGroup().getGroupId(),
                queryDateStart,
                queryDateStart,
                THIS_WEEK);
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
                queryDateStart,
                queryDateEnd,
                THIS_WEEK);
    }

    @Override
    public ScheduleTable getNextWeekLessons(User user) throws ExecutionException, InterruptedException {
        LocalDate queryDateStart = DateUtils.getStartOfWeek(DateUtils.getTodayDateString()).plusDays(7);
        LocalDate queryDateEnd = queryDateStart.plusDays(6);
        return getSchedule(
                user.getGroup().getFaculty().getInstitute().getId(),
                user.getGroup().getFaculty().getFacultyId(),
                user.getGroup().getCourse(),
                user.getGroup().getGroupId(),
                queryDateStart,
                queryDateEnd,
                OfflineReadingMode.NEXT_WEEK);
    }

    @Override
    public ScheduleTable getScheduleWithCustomParameters(User user, Update update) throws InterruptedException {
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
                    queryDateStart,
                    queryDateEnd,
                    OfflineReadingMode.NONE
            );
        } catch (RuntimeException | ExecutionException e) {
            throw new CustomScheduleFetchException();
        }
    }

    private Deque<Day> splitByDays(Deque<Lesson> lessons, LocalDate startDate, LocalDate endDate) {
        TreeMap<LocalDate, List<Lesson>> lessonsByDay = lessons
                .stream()
                .filter(lesson -> !lesson.getDate().isBefore(startDate) && !lesson.getDate().isAfter(endDate))
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

    private void ensureNoBreaks(TreeMap<LocalDate, List<Lesson>> lessonsByDay, LocalDate startDate, LocalDate endDate) {
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            lessonsByDay.putIfAbsent(date, Collections.emptyList());
        }
    }

    private Deque<Lesson> getSchedule(Map<String, String> params, OfflineReadingMode mode) throws ExecutionException, InterruptedException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return requester.makeRequest(params, ApiEndpoints.API_SCHEDULE);
            } catch (IOException | URISyntaxException | InterruptedException e) {
                logger.error(REQUEST_CREATION_FAILED);
                throw new RequestCreationFailedException(REQUEST_CREATION_FAILED);
            }
        }, Executors.newSingleThreadExecutor()).exceptionally((ex) -> {
            if (ex != null) logger.error(EXECUTOR_EXCEPTION_MESSAGE, ex.getMessage());
            String result;
            if (mode.equals(THIS_WEEK)) {
                result = localFileReader.readLocalFile(
                        CURRENT_WEEK_LOCAL_SCHEDULE_PATH.formatted(
                                params.get(GROUP_NAME),
                                Long.parseLong(params.get(GROUP))));
            } else if (mode.equals(OfflineReadingMode.NEXT_WEEK)) {
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

    public ScheduleTable splitBigTableIntoSmall(ScheduleTable scheduleTable) {
        if (scheduleTable.getDays().size() <= 7) return scheduleTable;
        ScheduleTable scheduleToReturn = new ScheduleTable();
        int scheduleTableSize = scheduleTable.getDays().size();
        for (int partition = 1; scheduleTableSize > 0; partition++) {
            scheduleToReturn.getDays().add(scheduleTable.getDays().pollFirst());
            scheduleTableSize = scheduleTable.getDays().size();
            // 7 is week length + 1 is offset
            if (partition % 8 == 0) {
                return scheduleToReturn;
            }
        }
        return scheduleToReturn;
    }

    public ScheduleTable getSchedule(
            long institute,
            long faculty,
            long course,
            long group,
            LocalDate queryDateStart,
            LocalDate queryDateEnd,
            OfflineReadingMode mode) throws ExecutionException, InterruptedException {
        HashMap<String, String> params = paramsBuilder.createParams(
                group,
                course,
                groupService.getById(group).getName(),
                faculty,
                institute,
                queryDateStart,
                queryDateEnd
        );
        Deque<Lesson> lessons = getSchedule(params, mode);
        appendPairLinks(group, lessons);
        return new ScheduleTable(splitByDays(lessons, queryDateStart, queryDateEnd));
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
}

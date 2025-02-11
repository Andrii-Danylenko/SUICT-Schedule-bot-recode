package rozkladbot.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import rozkladbot.constants.AppConstants;
import rozkladbot.constants.LoggingConstants;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static rozkladbot.constants.ErrorConstants.REQUEST_CREATION_FAILED;

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
                user.getGroup().getFaculty().getId(),
                user.getGroup().getCourse(),
                user.getGroup().getId(),
                queryDateStart,
                queryDateStart,
                OfflineReadingMode.THIS_WEEK);
    }

    @Override
    public ScheduleTable getTomorrowLessons(User user) throws ExecutionException, InterruptedException {
        LocalDate queryDateStart = DateUtils.getToday().plusDays(1);
        return getSchedule(
                user.getGroup().getFaculty().getInstitute().getId(),
                user.getGroup().getFaculty().getId(),
                user.getGroup().getCourse(),
                user.getGroup().getId(),
                queryDateStart,
                queryDateStart,
                OfflineReadingMode.THIS_WEEK);
    }

    @Override
    public ScheduleTable getWeeklyLessons(User user) throws ExecutionException, InterruptedException {
        LocalDate queryDateStart = DateUtils.getStartOfWeek(DateUtils.getTodayDateString());
        LocalDate queryDateEnd = queryDateStart.plusDays(6);
        return getSchedule(
                user.getGroup().getFaculty().getInstitute().getId(),
                user.getGroup().getFaculty().getId(),
                user.getGroup().getCourse(),
                user.getGroup().getId(),
                queryDateStart,
                queryDateEnd,
                OfflineReadingMode.THIS_WEEK);
    }

    @Override
    public ScheduleTable getNextWeekLessons(User user) throws ExecutionException, InterruptedException {
        LocalDate queryDateStart = DateUtils.getStartOfWeek(DateUtils.getTodayDateString()).plusDays(7);
        LocalDate queryDateEnd = queryDateStart.plusDays(6);
        return getSchedule(
                user.getGroup().getFaculty().getInstitute().getId(),
                user.getGroup().getFaculty().getId(),
                user.getGroup().getCourse(),
                user.getGroup().getId(),
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
                    group.getFaculty().getId(),
                    group.getCourse(),
                    group.getId(),
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
        System.out.println(lessonsByDay);
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
                return requester.makeRequest(params);
            } catch (IOException | URISyntaxException | InterruptedException e) {
                logger.error(REQUEST_CREATION_FAILED);
                throw new RequestCreationFailedException(REQUEST_CREATION_FAILED);
            }
        }, Executors.newSingleThreadExecutor()).exceptionally((ex) -> {
            if (ex != null) logger.error(LoggingConstants.EXECUTOR_EXCEPTION_MESSAGE, ex.getMessage());
            String result;
            if (mode.equals(OfflineReadingMode.THIS_WEEK)) {
                result = localFileReader.readLocalFile(
                        AppConstants.CURRENT_WEEK_LOCAL_SCHEDULE_PATH.formatted(
                                params.get("groupName"),
                                Long.parseLong(params.get("group"))));
            } else if (mode.equals(OfflineReadingMode.NEXT_WEEK)) {
                result = localFileReader.readLocalFile(
                        AppConstants.NEXT_WEEK_LOCAL_SCHEDULE_PATH.formatted(
                                params.get("groupName"),
                                Long.parseLong(params.get("group"))));
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
        HashMap<String, String> params = paramsBuilder.buildFromGroupId(group, queryDateStart, queryDateEnd);
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
                    System.out.println(pairlink);
                    if (pairlink != null) {
                        lesson.setPairLink(pairlink.getLink());
                    }
                }
        );
    }
}

package rozkladbot.telegram.utils.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import rozkladbot.constants.AppConstants;
import rozkladbot.entities.User;
import rozkladbot.services.ScheduleService;
import rozkladbot.services.UserService;
import rozkladbot.telegram.utils.files.writer.LocalFileWriter;
import rozkladbot.utils.date.DateUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static rozkladbot.constants.LoggingConstants.*;

@Component("scheduleDumper")
public class ScheduleDumper {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleDumper.class);
    private final UserService userService;
    private final ScheduleService scheduleService;
    private final LocalFileWriter localFileWriter;

    public ScheduleDumper(
            UserService userService,
            ScheduleService scheduleService,
            LocalFileWriter localFileWriter) {
        this.userService = userService;
        this.scheduleService = scheduleService;
        this.localFileWriter = localFileWriter;
    }

    @Async
    @Scheduled(cron = AppConstants.SCHEDULE_DUMP_CRON, zone = AppConstants.APPLICATION_TIME_ZONE)
    public void dumpSchedule() {
        dumpSchedule(false);
    }

    @Async
    public void dumpSchedule(boolean isForced) {
        Set<Long> alreadyDumpedGroups = new HashSet<>();
        logger.info(SCHEDULE_DUMPING_BEGAN);
        userService.getAll().forEach(user -> {
            long groupNumber = user.getGroup().getId();
            if (groupNumber == 0 || alreadyDumpedGroups.contains(groupNumber)) return;
            try {
                logger.info(SCHEDULE_DUMPING_BEGAN_FOR_USER, user.getId(), user.getGroup().getId(), user.getGroup().getName());
                Path directoryPath = Paths.get(AppConstants.GROUP_SCHEDULES_FOLDER_NAME);
                String fileName = AppConstants.THIS_WEEK_SCHEDULE_FILE_NAME.formatted(user.getGroup().getName(), user.getGroup().getId());
                prepareForWriting(directoryPath, fileName, user,
                        DateUtils.getStartOfWeek(DateUtils.getTodayDateString()),
                        DateUtils.getStartOfWeek(DateUtils.getTodayDateString()).plusDays(7),
                        isForced);
                fileName = AppConstants.NEXT_WEEK_SCHEDULE_FILE_NAME.formatted(user.getGroup().getName(), user.getGroup().getId());
                prepareForWriting(directoryPath, fileName, user,
                        DateUtils.getStartOfWeek(DateUtils.getTodayDateString()).plusDays(7),
                        DateUtils.getStartOfWeek(DateUtils.getTodayDateString()).plusDays(14),
                        isForced);
                alreadyDumpedGroups.add(groupNumber);
            } catch (IOException | URISyntaxException | InterruptedException e) {
                logger.error(SCHEDULE_DUMPING_FAILED, user.getId(), e);
            }
        });
    }

    private void prepareForWriting(Path directoryPath, String fileName, User user, LocalDate dateFrom, LocalDate dateTo, boolean isForced) throws IOException, URISyntaxException, InterruptedException {
        if (localFileWriter.checkIfAlreadyWritten(directoryPath, fileName, isForced)) {
            String response = scheduleService.getRawSchedule(user, dateFrom, dateTo);
            localFileWriter.writeFile(directoryPath, fileName, response);
            logger.info(SCHEDULE_DUMPED_SUCCESSFULLY, fileName);
        } else {
            logger.info(SCHEDULE_DUMPING_SKIPPING_FILE, fileName);
        }
    }
}

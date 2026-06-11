package rozkladbot.telegram.utils.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import rozkladbot.constants.ApiEndpoints;
import rozkladbot.constants.AppConstants;
import rozkladbot.entities.Group;
import rozkladbot.services.GroupService;
import rozkladbot.telegram.utils.files.writer.LocalFileWriter;
import rozkladbot.utils.date.DateUtils;
import rozkladbot.services.web.urlbuilder.QueryBuilder;
import rozkladbot.services.web.requestservice.WebRequestService;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static rozkladbot.constants.LoggingConstants.SCHEDULE_DUMPED_SUCCESSFULLY;
import static rozkladbot.constants.LoggingConstants.SCHEDULE_DUMPING_BEGAN;
import static rozkladbot.constants.LoggingConstants.SCHEDULE_DUMPING_BEGAN_FOR_GROUP;
import static rozkladbot.constants.LoggingConstants.SCHEDULE_DUMPING_FAILED;
import static rozkladbot.constants.LoggingConstants.SCHEDULE_DUMPING_SKIPPING_FILE;

@Component("scheduleCacheFileWriter")
public class ScheduleCacheFileWriter {

  private static final Logger logger = LoggerFactory.getLogger(ScheduleCacheFileWriter.class);
  private final GroupService groupService;
  private final WebRequestService webRequestService;
  private final LocalFileWriter localFileWriter;
  private final QueryBuilder queryBuilder;

  public ScheduleCacheFileWriter(
      GroupService groupService,
      WebRequestService webRequestService,
      LocalFileWriter localFileWriter,
      QueryBuilder queryBuilder
  ) {
    this.groupService = groupService;
    this.webRequestService = webRequestService;
    this.localFileWriter = localFileWriter;
    this.queryBuilder = queryBuilder;
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
    groupService.getAllGroupsThatAssignedToUsers().forEach(group -> {
      long groupNumber = group.getGroupId();
      if (groupNumber == 0 || alreadyDumpedGroups.contains(groupNumber)) {
        return;
      }
      try {
        logger.info(SCHEDULE_DUMPING_BEGAN_FOR_GROUP, group.getGroupId(), group.getName());
        Path directoryPath = Paths.get(AppConstants.GROUP_SCHEDULES_FOLDER_NAME);
        String fileName = AppConstants.SCHEDULE_FILE_NAME.formatted(group.getName(),
            group.getGroupId());
        prepareForWriting(directoryPath, fileName, group,
            DateUtils.getStartOfWeek(DateUtils.getTodayDateString()),
            DateUtils.getStartOfWeek(DateUtils.getTodayDateString()).plusDays(7),
            isForced);
        alreadyDumpedGroups.add(groupNumber);
      } catch (IOException | URISyntaxException | InterruptedException e) {
        logger.error(SCHEDULE_DUMPING_FAILED, group, e);
      }
    });
  }

  private void prepareForWriting(Path directoryPath, String fileName, Group group,
      LocalDate dateFrom, LocalDate dateTo, boolean isForced)
      throws IOException, URISyntaxException, InterruptedException {
    if (localFileWriter.checkIfAlreadyWritten(directoryPath, fileName, isForced)) {
      Map<String, String> params = queryBuilder.buildQueryParams(
          group.getGroupId(),
          group.getId(),
          group.getCourse(),
          group.getName(),
          group.getFaculty().getFacultyId(),
          group.getFaculty().getInstitute().getId(),
          dateFrom,
          dateTo
      );
      String response = webRequestService.makeRequest(params, ApiEndpoints.API_SCHEDULE);
      localFileWriter.writeFile(directoryPath, fileName, response);
      logger.info(SCHEDULE_DUMPED_SUCCESSFULLY, fileName);
    } else {
      logger.info(SCHEDULE_DUMPING_SKIPPING_FILE, fileName);
    }
  }
}

package rozkladbot.telegram.startup.dataupdater;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import rozkladbot.constants.ApiEndpoints;
import rozkladbot.entities.Faculty;
import rozkladbot.entities.Group;
import rozkladbot.entities.json.response.CourseJsonResponse;
import rozkladbot.entities.json.response.GroupJsonResponse;
import rozkladbot.json.deserializers.JsonDeserializer;
import rozkladbot.services.FacultyService;
import rozkladbot.services.GroupService;
import rozkladbot.services.web.requestservice.WebRequestService;
import rozkladbot.services.web.urlbuilder.QueryBuilder;

@Service
@Order(4)
@Slf4j
@ConditionalOnProperty(name = "bootstrapper.update.university-data", havingValue = "true")
public class GroupDataUpdateService extends GenericDataUpdateService {

  private final FacultyService facultyService;
  private final JsonDeserializer<CourseJsonResponse> courseJsonResponseDeserializer;
  private final GroupService groupService;
  private final JsonDeserializer<GroupJsonResponse> groupJsonResponseDeserializer;

  public GroupDataUpdateService(WebRequestService webRequestService,
      QueryBuilder queryBuilder, FacultyService facultyService,
      JsonDeserializer<CourseJsonResponse> courseJsonResponseDeserializer,
      GroupService groupService,
      JsonDeserializer<GroupJsonResponse> groupJsonResponseDeserializer) {
    super(webRequestService, queryBuilder);
    this.facultyService = facultyService;
    this.courseJsonResponseDeserializer = courseJsonResponseDeserializer;
    this.groupService = groupService;
    this.groupJsonResponseDeserializer = groupJsonResponseDeserializer;
  }


  @Override
  public void updateData() {
    try {
      List<Faculty> faculties = facultyService.getAll();
      for (Faculty faculty : faculties) {
        List<CourseJsonResponse> courseJsonResponses = new LinkedList<>(
            courseJsonResponseDeserializer.deserialize(
                webRequestService.makeRequest(
                    queryBuilder.buildFromValues(),
                    ApiEndpoints.API_COURSES.formatted(faculty.getInstitute().getId(),
                        faculty.getFacultyId())
                )));
        for (CourseJsonResponse courseJsonResponse : courseJsonResponses) {
          System.out.printf("updating groups for course %d of university %d%n",
              courseJsonResponse.getId(), faculty.getInstitute().getId());
          updateGroups(courseJsonResponse, faculty);
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  private void updateGroups(CourseJsonResponse course, Faculty faculty) {
    try {
      List<Group> groups = groupJsonResponseDeserializer.deserialize(webRequestService.makeRequest(
          queryBuilder.buildFromValues(),
          ApiEndpoints.API_GROUPS.formatted(faculty.getInstitute().getId(), faculty.getFacultyId(),
              course.getId())
      )).stream().map(groupJsonResponse -> {
        Group group = Group.toGroupFromResponse(groupJsonResponse);
        group.setFaculty(faculty);
        group.setCourse(course.getId());
        return group;
      }).collect(Collectors.toCollection(LinkedList::new));
      faculty.setGroups(groups);
      for (Group group : groups) {
        groupService.save(group);
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }
}

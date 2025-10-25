package rozkladbot.telegram.startup;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import rozkladbot.constants.ApiEndpoints;
import rozkladbot.constants.LoggingConstants;
import rozkladbot.entities.Faculty;
import rozkladbot.entities.Group;
import rozkladbot.entities.Institute;
import rozkladbot.entities.User;
import rozkladbot.entities.json.response.CourseJsonResponse;
import rozkladbot.enums.UserState;
import rozkladbot.services.FacultyService;
import rozkladbot.services.GroupService;
import rozkladbot.services.InstituteService;
import rozkladbot.services.UserService;
import rozkladbot.telegram.caching.UserCache;
import rozkladbot.utils.deserializers.CourseJsonResponseDeserializer;
import rozkladbot.utils.deserializers.FacultyJsonResponseDeserializer;
import rozkladbot.utils.deserializers.GroupJsonResponseDeserializer;
import rozkladbot.utils.deserializers.InstituteJsonResponseDeserializer;
import rozkladbot.utils.web.requester.ParamsBuilder;
import rozkladbot.utils.web.requester.Requester;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Bootstrapper {
    private static final Logger logger = LoggerFactory.getLogger(Bootstrapper.class);
    private final Requester requester;
    private final ParamsBuilder paramsBuilder;
    private final UserCache userCache;
    private final UserService userService;
    private final InstituteService instituteService;
    private final GroupService groupService;
    private final FacultyService facultyService;
    private final InstituteJsonResponseDeserializer instituteJsonResponseDeserializer;
    private final FacultyJsonResponseDeserializer facultyJsonResponseDeserializer;
    private final GroupJsonResponseDeserializer groupJsonResponseDeserializer;
    private final CourseJsonResponseDeserializer courseJsonResponseDeserializer;
    @Value("${bootstrapper.update.university-data}")
    private boolean DOES_NEED_TO_UPDATE_UNIVERSITY_DATA;

    @Autowired
    public Bootstrapper(
            UserCache userCache,
            UserService userService,
            GroupService groupService,
            InstituteJsonResponseDeserializer instituteJsonResponseDeserializer,
            Requester requester,
            ParamsBuilder paramsBuilder,
            FacultyJsonResponseDeserializer facultyJsonResponseDeserializer,
            GroupJsonResponseDeserializer groupJsonResponseDeserializer,
            CourseJsonResponseDeserializer courseJsonResponseDeserializer,
            InstituteService instituteService,
            FacultyService facultyService) {
        this.userCache = userCache;
        this.userService = userService;
        this.groupService = groupService;
        this.facultyService = facultyService;
        this.instituteJsonResponseDeserializer = instituteJsonResponseDeserializer;
        this.requester = requester;
        this.paramsBuilder = paramsBuilder;
        this.facultyJsonResponseDeserializer = facultyJsonResponseDeserializer;
        this.groupJsonResponseDeserializer = groupJsonResponseDeserializer;
        this.courseJsonResponseDeserializer = courseJsonResponseDeserializer;
        this.instituteService = instituteService;
    }

    @PostConstruct
    public void init() {
        logger.info(LoggingConstants.APPLICATION_INIT_JOB_STARTED);
        loadUsers();
        if (DOES_NEED_TO_UPDATE_UNIVERSITY_DATA) {
            updateInstitutes();
            updateFaculties();
            updateCourses();
        }
        logger.info(LoggingConstants.APPLICATION_INIT_JOB_FINISHED);
    }

    private void loadUsers() {
        logger.info(LoggingConstants.USERS_MIGRATION_STARTED);
        List<User> userList = userService.getAll();
        for (User user : userList) {
            user.setUserState(UserState.IDLE);
            user.setRegistered(true);
            userCache.put(user.getId(), user);
        }
        logger.info(LoggingConstants.USERS_MIGRATION_FINISHED, userCache.size());
    }

    private void updateInstitutes() {
        try {
            List<Institute> institutes = instituteJsonResponseDeserializer.deserialize(
                    requester.makeRequest(
                            paramsBuilder.buildFromValues(),
                            ApiEndpoints.API_INSTITUTIONS
                    )
            ).stream().map(Institute::toInstituteFromJsonResponse).collect(Collectors.toCollection(LinkedList::new));
            instituteService.saveAll(institutes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateFaculties() {
        try {
            List<Institute> institutes = instituteService.getAll();
            for (Institute inst : institutes) {
                List<Faculty> faculties = facultyJsonResponseDeserializer.deserialize(
                        requester.makeRequest(
                                paramsBuilder.buildFromValues(),
                                ApiEndpoints.API_FACULTIES.formatted(inst.getId())
                        )
                ).stream().map(facultyJsonResponse -> {
                            Faculty faculty = Faculty.toFacultyFromJsonResponse(facultyJsonResponse);
                            faculty.setInstitute(inst);
                            return faculty;
                        }
                ).collect(Collectors.toCollection(ArrayList::new));
                facultyService.saveAll(faculties);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCourses() {
        try {
            List<Faculty> faculties = facultyService.getAll();
            for (Faculty faculty : faculties) {
                List<CourseJsonResponse> courseJsonResponses = new LinkedList<>(courseJsonResponseDeserializer.deserialize(requester.makeRequest(
                        paramsBuilder.buildFromValues(),
                        ApiEndpoints.API_COURSES.formatted(faculty.getInstitute().getId(), faculty.getFacultyId())
                )));
                for (CourseJsonResponse courseJsonResponse : courseJsonResponses) {
                    System.out.printf("updating groups for course %d of university %d%n", courseJsonResponse.getId(), faculty.getInstitute().getId());
                    updateGroups(courseJsonResponse, faculty);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateGroups(CourseJsonResponse course, Faculty faculty) {
        try {
            List<Group> groups = groupJsonResponseDeserializer.deserialize(requester.makeRequest(
                    paramsBuilder.buildFromValues(),
                    ApiEndpoints.API_GROUPS.formatted(faculty.getInstitute().getId(), faculty.getFacultyId(), course.getId())
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
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

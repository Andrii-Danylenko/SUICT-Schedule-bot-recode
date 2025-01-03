package rozkladbot.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rozkladbot.entities.Faculty;
import rozkladbot.entities.Group;
import rozkladbot.services.FacultyService;
import rozkladbot.services.GroupService;

import java.util.List;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private final FacultyService facultyService;
    private final GroupService groupService;

    public ScheduleController(FacultyService facultyService, GroupService groupService) {
        this.facultyService = facultyService;
        this.groupService = groupService;
    }

    @GetMapping("/faculties")
    public List<Faculty> getFaculties(@RequestParam long instituteId) {
        return facultyService.findByInstituteId(instituteId);
    }

    @GetMapping("/courses")
    public List<Long> getCourses(@RequestParam long facultyId) {
        return groupService.getCoursesByFacultyId(facultyId);
    }

    @GetMapping("/groups")
    public List<Group> getGroups(@RequestParam long course) {
        return groupService.findByCourse(course);
    }
}

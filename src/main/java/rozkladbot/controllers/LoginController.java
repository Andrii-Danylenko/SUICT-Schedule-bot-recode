package rozkladbot.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import rozkladbot.entities.Faculty;
import rozkladbot.entities.Group;
import rozkladbot.entities.Institute;
import rozkladbot.entities.ScheduleForm;
import rozkladbot.services.FacultyService;
import rozkladbot.services.GroupService;
import rozkladbot.services.InstituteService;

import java.util.List;

@Controller
@RequestMapping("/login")
public class LoginController {
    private final GroupService groupService;
    private final InstituteService instituteService;
    private final FacultyService facultyService;

    public LoginController(
            GroupService groupService,
            InstituteService instituteService,
            FacultyService facultyService) {
        this.groupService = groupService;
        this.instituteService = instituteService;
        this.facultyService = facultyService;
    }

    @GetMapping
    public String showScheduleForm(Model model) {
        List<Institute> institutes = instituteService.getAll();
        List<Group> groupList = groupService.getAll();
        List<String> courseList = groupService.getGroupCourses();
        List<Faculty> facultyList = facultyService.getAll();
        model.addAttribute("scheduleForm", new ScheduleForm());
        model.addAttribute("groups", groupList);
        model.addAttribute("institutes", institutes);
        model.addAttribute("courses", courseList);
        model.addAttribute("faculties", facultyList);
        return "schedule-form";
    }
}

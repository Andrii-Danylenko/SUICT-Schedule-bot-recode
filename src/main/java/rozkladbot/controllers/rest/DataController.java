package rozkladbot.controllers.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rozkladbot.entities.dto.FacultyDTO;
import rozkladbot.entities.dto.GroupDTO;
import rozkladbot.entities.dto.InstituteDTO;
import rozkladbot.services.FacultyService;
import rozkladbot.services.GroupService;
import rozkladbot.services.InstituteService;

import java.util.List;

@RestController
@RequestMapping("/get")
public class DataController {
    private final FacultyService facultyService;
    private final GroupService groupService;
    private final InstituteService instituteService;

    public DataController(
            FacultyService facultyService,
            GroupService groupService,
            InstituteService instituteService) {
        this.facultyService = facultyService;
        this.groupService = groupService;
        this.instituteService = instituteService;
    }

    @GetMapping("/faculties")
    @ResponseBody
    public ResponseEntity<List<FacultyDTO>> getFacultiesByInstitute(@RequestParam Long instituteId) {
        List<FacultyDTO> facultyDTOS = facultyService.findByInstituteId(instituteId).stream().map(FacultyDTO::new).toList();
        return ResponseEntity.ok(facultyDTOS);
    }

    @GetMapping("/courses")
    public ResponseEntity<List<Long>> getCourses(@RequestParam Long facultyId, @RequestParam Long instituteId) {
        List<Long> courses = groupService.findAllCoursesByFacultyIdAndInstituteId(facultyId, instituteId);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/groups")
    public ResponseEntity<List<GroupDTO>> getGroups(@RequestParam Long facultyId, @RequestParam Long course, @RequestParam Long instituteId) {
        List<GroupDTO> groupDTOs = groupService.findByIdAndCourseAndInstituteId(facultyId, course, instituteId).stream().map(GroupDTO::new).toList();
        return ResponseEntity.ok(groupDTOs);
    }

    @GetMapping("/institutes/all")
    @ResponseBody
    public ResponseEntity<List<InstituteDTO>> getFacultiesByInstitute() {
        List<InstituteDTO> instituteDTOS = instituteService.getAll().stream().map(InstituteDTO::new).toList();
        return ResponseEntity.ok(instituteDTOS);
    }
}

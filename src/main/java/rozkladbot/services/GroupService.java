package rozkladbot.services;

import rozkladbot.entities.Group;

import java.util.List;
import java.util.Set;

public interface GroupService extends BasicCrudService<Group> {
    Group getByName(String name);

    List<String> getGroupCourses();

    List<Group> findByFacultyId(long facultyId);

    List<Group> findByFacultyAndCourse(String facultyName, long course);

    List<Group> findByCourse(long course);

    List<Long> findAllCoursesByFacultyIdAndInstituteId(long facultyId, long instituteId);

    List<Group> findByIdAndCourseAndInstituteId(long facultyId, long course, long instituteId);

    Set<Group> getAllGroupsThatAssignedToUsers();
}

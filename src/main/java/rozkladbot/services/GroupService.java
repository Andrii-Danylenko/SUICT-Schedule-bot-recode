package rozkladbot.services;

import rozkladbot.entities.Group;

import java.util.List;

public interface GroupService extends BasicCrudService<Group> {
    Group getByName(String name);

    List<String> getGroupCourses();

    List<Group> findByFacultyId(long facultyId);

    List<Group> findByFacultyAndCourse(String facultyName, long course);

    List<Group> findByCourse(long course);

    List<Long> getCoursesByFacultyId(long facultyId);

    List<Group> findByFacultyIdAndCourse(long facultyId, long course);
}

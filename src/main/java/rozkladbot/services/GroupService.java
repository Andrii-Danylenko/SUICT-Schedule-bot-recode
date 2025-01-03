package rozkladbot.services;

import rozkladbot.entities.Group;

import java.util.List;

public interface GroupService {
    List<Group> getAll();

    Group getById(long id);

    Group getByName(String name);

    Group save(Group group);

    List<String> getGroupCourses();
    List<Group> findByFacultyId(long facultyId);
    List<Group> findByFacultyAndCourse(String facultyName, long course);
}

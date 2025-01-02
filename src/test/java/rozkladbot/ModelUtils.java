package rozkladbot;

import rozkladbot.entities.Faculty;
import rozkladbot.entities.Group;
import rozkladbot.entities.Institute;
import rozkladbot.entities.User;

public class ModelUtils {
    private ModelUtils() {
    }

    public static User getUser() {
        User user = new User();
        user.setGroup(getGroup());
        user.setId(1);
        user.setUsername("Andrii Danylenko");
        return user;
    }

    public static Group getGroup() {
        Group group = new Group();
        group.setId(1);
        group.setCourse(2);
        group.setName("Group 1");
        group.setFaculty(getFaculty());
        return group;
    }

    public static Faculty getFaculty() {
        Faculty faculty = new Faculty();
        faculty.setId(1);
        faculty.setFacultyName("Faculty 1");
        faculty.setInstitute(getInstitute());
        return faculty;
    }

    public static Institute getInstitute() {
        Institute institute = new Institute();
        institute.setId(1);
        institute.setInstituteName("Institute 1");
        return institute;
    }
}

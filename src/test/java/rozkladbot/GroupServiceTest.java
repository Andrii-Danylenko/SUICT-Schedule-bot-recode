package rozkladbot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import rozkladbot.entities.Group;
import rozkladbot.services.GroupService;

import java.util.List;

@SpringBootTest
public class GroupServiceTest {
    @Autowired
    private GroupService groupService;
    @Test
    @Transactional
    void testGetAllFaculties() {
        List<Group> groupList = groupService.getAll();
        Assertions.assertFalse(groupList.isEmpty());
    }
    @Test
    @Transactional
    void testGetFacultyByNameAndCourse() {
        List<Group> groups = groupService.findByFacultyAndCourse("Факультет IT", 1);
        Assertions.assertFalse(groups.isEmpty());
    }
}

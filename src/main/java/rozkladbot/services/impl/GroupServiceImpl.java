package rozkladbot.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rozkladbot.constants.ErrorConstants;
import rozkladbot.entities.Group;
import rozkladbot.exceptions.NoSuchEntityFoundException;
import rozkladbot.repos.GroupRepo;
import rozkladbot.services.GroupService;

import java.util.List;

@Service("groupServiceImpl")
public class GroupServiceImpl implements GroupService {

    private final GroupRepo groupRepo;

    @Autowired
    public GroupServiceImpl(GroupRepo groupRepo) {
        this.groupRepo = groupRepo;
    }

    @Override
    public List<Group> getAll() {
        return groupRepo.findAll();
    }

    @Override
    public Group getById(long id) {
        return groupRepo.findById(id).orElseThrow(() -> new NoSuchEntityFoundException(ErrorConstants.ENTITY_NOT_FOUND_EXCEPTION));
    }

    @Override
    public Group getByName(String name) {
        return groupRepo.findByName(name).orElseThrow(() -> new NoSuchEntityFoundException(ErrorConstants.ENTITY_NOT_FOUND_EXCEPTION));
    }

    @Override
    public Group save(Group group) {
        return groupRepo.save(group);
    }

    @Override
    public List<String> getGroupCourses() {
        return groupRepo.getGroupCourses();
    }

    @Override
    public List<Group> findByFacultyId(long facultyId) {
        return groupRepo.findByFacultyId(facultyId);
    }

    @Override
    public List<Group> findByFacultyAndCourse(String facultyName, long course) {
        return groupRepo.findByFacultyAndCourse(facultyName, course);
    }
}

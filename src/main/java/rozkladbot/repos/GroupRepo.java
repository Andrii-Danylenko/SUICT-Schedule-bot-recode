package rozkladbot.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rozkladbot.entities.Group;

import java.util.List;
import java.util.Optional;

public interface GroupRepo extends JpaRepository<Group, Long> {
    Optional<Group> findByName(String name);

    @Query(value = "SELECT DISTINCT g.course FROM Group g")
    List<String> getGroupCourses();

    @Query(value = "SELECT DISTINCT g FROM Group g WHERE g.faculty.id = ?1")
    List<Group> findByFacultyId(long facultyId);
    @Query(value = "SELECT g FROM Group g WHERE g.faculty.facultyName = ?1 AND g.course = ?2")
    List<Group> findByFacultyAndCourse(String facultyName, long course);
    List<Group> findByCourse(long course);
}

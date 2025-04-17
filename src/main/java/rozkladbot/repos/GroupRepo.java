package rozkladbot.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rozkladbot.entities.Group;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface GroupRepo extends JpaRepository<Group, Long> {
    Optional<Group> findByName(String name);

    @Query(value = "SELECT DISTINCT g.course FROM Group g")
    List<String> getGroupCourses();

    @Query(value = "SELECT DISTINCT g FROM Group g WHERE g.faculty.facultyId = ?1")
    List<Group> findByFacultyId(long facultyId);

    @Query(value = "SELECT g FROM Group g WHERE g.faculty.facultyId = ?1 and g.course = ?2")
    List<Group> findByFacultyIdAndCourse(long facultyId);

    @Query(value = "SELECT g FROM Group g WHERE g.faculty.facultyName = ?1 AND g.course = ?2")
    List<Group> findByFacultyAndCourse(String facultyName, long course);

    @Query(value = "SELECT * FROM groups WHERE course = ?1", nativeQuery = true)
    List<Group> findByCourse(long course);

    @Query(value = "SELECT g FROM Group g WHERE " +
                   "g.faculty.facultyId = ?1 AND g.course = ?2 AND g.faculty.institute.id = ?3")
    List<Group> findByIdAndCourseAndInstituteId(long facultyId, long course, long instituteId);

    @Query(value = "SELECT DISTINCT g.course FROM Group g WHERE g.faculty.facultyId = :facultyId AND g.faculty.institute.id = :instituteId ORDER BY g.course")
    List<Long> findAllCourseIdsByFacultyIdAndInstituteId(long facultyId, long instituteId);

    Optional<Group> getByGroupId(long groupId);

    @Query("SELECT g FROM Group g JOIN FETCH User u on u.group = g")
    Set<Group> getAllGroupsThatAssignedToUsers();
}

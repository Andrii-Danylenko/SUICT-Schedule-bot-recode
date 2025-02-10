package rozkladbot.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rozkladbot.entities.Faculty;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyRepo extends JpaRepository<Faculty, Long> {
    Optional<Faculty> findByFacultyName(String facultyName);
    @Query(value = "SELECT f FROM Faculty f WHERE f.institute.instituteName = ?1")
    List<Faculty> getFacultyByInstituteName(String instituteName);
    List<Faculty> findByInstituteId(long instituteId);
}

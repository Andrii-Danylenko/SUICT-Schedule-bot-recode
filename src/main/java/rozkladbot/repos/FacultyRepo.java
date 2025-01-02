package rozkladbot.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rozkladbot.entities.Faculty;

import java.util.Optional;

public interface FacultyRepo extends JpaRepository<Faculty, Long> {
    Optional<Faculty> findByFacultyName(String facultyName);
}

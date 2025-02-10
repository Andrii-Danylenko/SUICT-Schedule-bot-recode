package rozkladbot.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rozkladbot.entities.Institute;

import java.util.Optional;

@Repository
public interface InstituteRepo extends JpaRepository<Institute, Long> {
    @Query("SELECT i FROM Institute i WHERE i.instituteName = ?1")
    Optional<Institute> findByName(String name);

    Optional<Institute> findById(long id);

}

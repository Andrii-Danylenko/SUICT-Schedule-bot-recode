package rozkladbot.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rozkladbot.entities.User;

import java.util.Optional;
import java.util.Set;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsById(long id);

    @Query(nativeQuery = true, value = "SELECT id FROM users")
    Set<Long> getAllIds();
}

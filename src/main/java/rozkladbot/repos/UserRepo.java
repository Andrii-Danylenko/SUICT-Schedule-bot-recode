package rozkladbot.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rozkladbot.entities.User;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsById(long id);
}

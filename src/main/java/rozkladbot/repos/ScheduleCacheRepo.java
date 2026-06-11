package rozkladbot.repos;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rozkladbot.entities.ScheduleCache;

@Repository
public interface ScheduleCacheRepo extends JpaRepository<ScheduleCache, Long> {

  Optional<ScheduleCache> findByGroupId(long groupId);
}

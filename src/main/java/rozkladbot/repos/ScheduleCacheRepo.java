package rozkladbot.repos;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rozkladbot.entities.ScheduleCache;
import rozkladbot.enums.ScheduleType;

@Repository
public interface ScheduleCacheRepo extends JpaRepository<ScheduleCache, Long> {

  @Modifying
  @Query(value = "UPDATE ScheduleCache cache set cache.content = ?1, cache.scheduleType = ?2, cache.updateTime = ?4 WHERE cache.groupId = ?3")
  ScheduleCache updateContent(String content, ScheduleType scheduleType, long groupId);

  ScheduleCache findByGroupId(Long groupId);

  Optional<ScheduleCache> findByGroupIdAndScheduleType(long groupId, ScheduleType scheduleType);
}

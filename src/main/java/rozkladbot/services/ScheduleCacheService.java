package rozkladbot.services;

import java.util.Optional;
import rozkladbot.entities.ScheduleCache;

public interface ScheduleCacheService extends BasicCrudService<ScheduleCache> {

  ScheduleCache updateContent(long groupId, String content);

  ScheduleCache getByGroupId(long groupId);

  Optional<ScheduleCache> findByGroupIdAndScheduleType(long groupId);

  boolean isCacheValid(Optional<ScheduleCache> scheduleCache);
}

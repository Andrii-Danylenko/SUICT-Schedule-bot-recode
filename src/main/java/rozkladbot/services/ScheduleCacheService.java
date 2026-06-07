package rozkladbot.services;

import java.util.Optional;
import rozkladbot.entities.ScheduleCache;
import rozkladbot.enums.ScheduleType;

public interface ScheduleCacheService extends BasicCrudService<ScheduleCache> {

  ScheduleCache updateContent(long groupId, ScheduleType type, String content);

  ScheduleCache getByGroupId(long groupId);

  Optional<ScheduleCache> findByGroupIdAndScheduleType(long groupId, ScheduleType scheduleType);

  boolean isCacheValid(Optional<ScheduleCache> scheduleCache);
}

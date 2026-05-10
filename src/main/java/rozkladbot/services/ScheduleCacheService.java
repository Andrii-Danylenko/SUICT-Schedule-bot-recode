package rozkladbot.services;

import rozkladbot.entities.ScheduleCache;
import rozkladbot.enums.ScheduleType;

public interface ScheduleCacheService extends BasicCrudService<ScheduleCache> {
  ScheduleCache updateContent(long groupId, ScheduleType type, String content);
  ScheduleCache getByGroupId(long groupId);
  ScheduleCache findByGroupIdAndScheduleType(long groupId, ScheduleType scheduleType);
}

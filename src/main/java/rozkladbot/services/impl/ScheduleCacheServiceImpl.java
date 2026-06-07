package rozkladbot.services.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import rozkladbot.entities.ScheduleCache;
import rozkladbot.enums.ScheduleType;
import rozkladbot.repos.ScheduleCacheRepo;
import rozkladbot.services.ScheduleCacheService;

@Service
@RequiredArgsConstructor
public class ScheduleCacheServiceImpl implements ScheduleCacheService {

  private final ScheduleCacheRepo scheduleCacheRepo;
  private final ObjectProvider<ScheduleCacheServiceImpl> scheduleCacheServiceProvider;

  @Override
  public List<ScheduleCache> getAll() {
    return scheduleCacheRepo.findAll();
  }

  @Override
  public ScheduleCache getById(long id) {
    return scheduleCacheRepo.findById(id).orElseThrow(
        () -> new EntityNotFoundException("ScheduleCache with id " + id + " not found"));
  }

  @Override
  @Transactional
  @CachePut(value = "schedules", key = "#result.groupId + '_' + #result.scheduleType.name()")
  public ScheduleCache save(ScheduleCache value) {
    return scheduleCacheRepo.save(value);
  }

  @Override
  @Transactional
  public void saveAll(Collection<ScheduleCache> value) {
    scheduleCacheRepo.saveAll(value);
  }

  @Transactional
  @Override
  public ScheduleCache updateContent(long groupId, ScheduleType type, String content) {
    if (ScheduleType.CUSTOM == type) {
      return null;
    }
    Optional<ScheduleCache> existing = findByGroupIdAndScheduleType(groupId, type);
    if (existing.isPresent()) {
      ScheduleCache existingSchedule = existing.get();
      existingSchedule.setContent(content);
      existingSchedule.setScheduleType(type);
      existingSchedule.setUpdateTime(LocalDateTime.now());
      return existingSchedule;
    }
    return scheduleCacheServiceProvider.getObject()
        .save(new ScheduleCache(groupId, type, content));
  }

  @Override
  public ScheduleCache getByGroupId(long groupId) {
    return scheduleCacheRepo.findByGroupId(groupId);
  }

  @Override
  @Cacheable(value = "schedules", key = "#groupId + '_' + #scheduleType.name()", unless = "#result == null")
  public Optional<ScheduleCache> findByGroupIdAndScheduleType(long groupId,
      ScheduleType scheduleType) {
    return scheduleCacheRepo.findByGroupIdAndScheduleType(groupId, scheduleType)
        .stream()
        .findFirst();
  }

  @Override
  public boolean isCacheValid(Optional<ScheduleCache> scheduleCache) {
    LocalDate scheduleCacheInvalidationTimeLimit = LocalDate.now().minusDays(1);
    return scheduleCache.isPresent() && StringUtils.isNotBlank(scheduleCache.get().getContent()) &&
        scheduleCache.get().getUpdateTime().isAfter(
            scheduleCacheInvalidationTimeLimit.atStartOfDay());
  }
}

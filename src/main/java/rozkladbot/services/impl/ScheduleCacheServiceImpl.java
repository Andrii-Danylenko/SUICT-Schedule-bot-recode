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
  @CachePut(value = "schedules", key = "#result.groupId")
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
  public ScheduleCache updateContent(long groupId, String content) {
    Optional<ScheduleCache> existing = findByGroupIdAndScheduleType(groupId);
    if (existing.isPresent()) {
      ScheduleCache existingSchedule = existing.get();
      existingSchedule.setContent(content);
      existingSchedule.setUpdateTime(LocalDateTime.now());
      return existingSchedule;
    }
    return scheduleCacheServiceProvider.getObject()
        .save(new ScheduleCache(groupId, content));
  }

  @Override
  public ScheduleCache getByGroupId(long groupId) {
    return scheduleCacheRepo.findByGroupId(groupId).orElse(null);
  }

  @Override
  @Cacheable(value = "schedules", key = "#groupId", unless = "#result == null")
  public Optional<ScheduleCache> findByGroupIdAndScheduleType(long groupId) {
    return scheduleCacheRepo.findByGroupId(groupId)
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

package rozkladbot.services.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
    ScheduleCache existing = findByGroupIdAndScheduleType(groupId, type);
    if (existing != null) {
      existing.setContent(content);
      return scheduleCacheServiceProvider.getIfAvailable().save(existing);
    }
    return scheduleCacheServiceProvider.getIfAvailable().save(new ScheduleCache(groupId, type, content));
  }

  @Override
  public ScheduleCache getByGroupId(long groupId) {
    return scheduleCacheRepo.findByGroupId(groupId);
  }

  @Override
  @Cacheable(value = "schedules", key = "#groupId + '_' + #scheduleType.name()", unless = "#result == null")
  public ScheduleCache findByGroupIdAndScheduleType(long groupId,
      ScheduleType scheduleType) {
    return scheduleCacheRepo.findByGroupIdAndScheduleType(groupId, scheduleType);
  }
}

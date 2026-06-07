package rozkladbot.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import rozkladbot.enums.ScheduleType;

@Entity
@Table(name = "schedules_cache")
@Data
@NoArgsConstructor
public class ScheduleCache {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "group_id")
  private long groupId;

  @Enumerated(EnumType.STRING)
  @Column(name = "schedule_type")
  private ScheduleType scheduleType;

  @Column(name = "content", columnDefinition = "TEXT")
  private String content;

  @Column(name = "update_time")
  @UpdateTimestamp
  private LocalDateTime updateTime;

  public ScheduleCache(long groupId, ScheduleType scheduleType, String content) {
    this.groupId = groupId;
    this.scheduleType = scheduleType;
    this.content = content;
  }
}

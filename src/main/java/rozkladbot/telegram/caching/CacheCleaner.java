package rozkladbot.telegram.caching;

import static rozkladbot.constants.BotMessageConstants.LAST_INTERACTION_TIME_WAS_TOO_LONG_AGO;

import java.time.LocalDateTime;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import rozkladbot.constants.AppConstants;
import rozkladbot.entities.User;
import rozkladbot.telegram.utils.message.MessageSender;

@Component
@RequiredArgsConstructor
public class CacheCleaner {

  private final UserMemoryCache userMemoryCache;
  private final ThreadMemoryCache threadMemoryCache;
  private final MessageSender messageSender;
  private final Predicate<User> userPredicate = user -> user.getLastInteractionDate().isBefore(
      LocalDateTime.now().minusDays(4));

  @Async
  @Scheduled(cron = AppConstants.USER_CACHE_CLEANING_CRON, zone = AppConstants.APPLICATION_TIME_ZONE)
  public void cleanUserCache() {
    for (User user : userMemoryCache.getAllValues()) {
      if (userPredicate.test(user)) {
        messageSender.sendMessage(user, LAST_INTERACTION_TIME_WAS_TOO_LONG_AGO, null, false, null);
        userMemoryCache.remove(user.getId());
        threadMemoryCache.remove(user.getId());
      }
    }
  }
}

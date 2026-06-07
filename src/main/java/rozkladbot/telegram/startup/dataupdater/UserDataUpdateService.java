package rozkladbot.telegram.startup.dataupdater;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import rozkladbot.constants.LoggingConstants;
import rozkladbot.entities.User;
import rozkladbot.enums.UserState;
import rozkladbot.services.UserService;
import rozkladbot.services.web.requestservice.WebRequestService;
import rozkladbot.services.web.urlbuilder.QueryBuilder;
import rozkladbot.telegram.caching.SimpleMemoryCache;

@Service
@Order(1)
@Slf4j
public class UserDataUpdateService extends GenericDataUpdateService {

  private final SimpleMemoryCache<Long, User> userCache;
  private final UserService userService;

  public UserDataUpdateService(WebRequestService webRequestService,
      QueryBuilder queryBuilder,
      UserService userService,
      SimpleMemoryCache<Long, User> userCache) {
    super(webRequestService, queryBuilder);
    this.userCache = userCache;
    this.userService = userService;
  }

  @Override
  public void updateData() {
    log.info(LoggingConstants.USERS_MIGRATION_STARTED);
    List<User> userList = userService.getAll();
    for (User user : userList) {
      user.setUserState(UserState.IDLE);
      user.setRegistered(true);
      userCache.put(user.getId(), user);
    }
    log.info(LoggingConstants.USERS_MIGRATION_FINISHED, userCache.size());
  }
}

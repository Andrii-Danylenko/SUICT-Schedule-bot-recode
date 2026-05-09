package rozkladbot.telegram.router;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import rozkladbot.entities.HandlerContext;
import rozkladbot.entities.User;
import rozkladbot.telegram.caching.UserCache;
import rozkladbot.telegram.factories.HandlerFactory;
import rozkladbot.telegram.handlers.*;
import rozkladbot.telegram.handlers.commandresolver.CommandsResolver;
import rozkladbot.telegram.utils.message.MessageUtils;

import static rozkladbot.enums.UserState.*;

@Component("routerImpl")
public class RouterImpl implements Router {

  private final UserCache userCache;
  private final CommandsResolver commandsResolver;
  private final HandlerFactory handlerFactory;

  @Autowired
  public RouterImpl(
      CommandsResolver commandsResolver,
      UserCache userCache,
      HandlerFactory handlerFactory
  ) {
    this.userCache = userCache;
    this.commandsResolver = commandsResolver;
    this.handlerFactory = handlerFactory;
  }

  @Override
  public void route(Update update, long chatId) {
    User user = userCache.get(chatId);
    if (user == null) {
      user = User.getDefaultUser(update, chatId);
      userCache.put(chatId, user);
    }
    boolean override = !update.hasMessage();
    user.setLastSentMessageId(MessageUtils.getCorrectMessageIdWithOffset(update));
    if (user.isRegistered()) {
      commandsResolver.resolveCommand(update, user);
    }
    HandlerContext handlerContext = new HandlerContext(user, update, override);
    if (user.getUserState().equals(AWAITING_SETTINGS)) {
      handlerFactory.getStrategy("settingshandler").handleRequest(handlerContext);
    }
    if (user.getUserState().equals(AWAITING_GREETINGS)) {
      handlerFactory.getStrategy("newusershandler").handleRequest(handlerContext);
      return;
    }
    if (registerUserStates.contains(user.getUserState())) {
      handlerFactory.getStrategy("registrationhandler").handleRequest(handlerContext);
    }
    if (scheduleUserStates.contains(user.getUserState())) {
      handlerFactory.getStrategy("schedulefetchhandler").handleRequest(handlerContext);
    }
    if (user.getUserState().equals(MAIN_MENU)) {
      handlerFactory.getStrategy("mainmenuhandler").handleRequest(handlerContext);
    }
    if (adminUserStates.contains(user.getUserState())) {
      handlerFactory.getStrategy("admincommandshandler").handleRequest(handlerContext);
    }
  }
}

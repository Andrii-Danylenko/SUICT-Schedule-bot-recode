package rozkladbot.telegram.router;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import rozkladbot.entities.User;
import rozkladbot.telegram.caching.SimpleUserCache;
import rozkladbot.telegram.handlers.*;
import rozkladbot.telegram.utils.message.MessageUtils;

@Component("routerImpl")
public class RouterImpl implements Router {
    private final RegistrationHandler registrationHandler;
    private final NewUsersHandler newUsersHandler;
    private final SimpleUserCache userCache;
    private final MainMenuHandler mainMenuHandler;
    private final CommandsHandler commandsHandler;

    @Autowired

    public RouterImpl(
            CommandsHandler commandsHandler,
            RegistrationHandler registrationHandler,
            SimpleUserCache userCache,
            NewUsersHandler newUsersHandler,
            MainMenuHandler mainMenuHandler) {
        this.registrationHandler = registrationHandler;
        this.userCache = userCache;
        this.newUsersHandler = newUsersHandler;
        this.mainMenuHandler = mainMenuHandler;
        this.commandsHandler = commandsHandler;
    }

    @Override
    public void route(Update update, long chatId) {
        User user = userCache.get(chatId);
        if (user == null) {
            user = registrationHandler.formUser(update, chatId);
            userCache.put(chatId, user);
        }
        boolean override = !update.hasMessage();
        user.setLastSentMessageId(MessageUtils.getCorrectMessageIdWithOffset(update));
        switch (user.getUserState()) {
            case AWAITING_GREETINGS:
                newUsersHandler.sendGreetings(user);
                break;
            case UNREGISTERED,
                 AWAITING_INSTITUTE,
                 AWAITING_FACULTY,
                 AWAITING_COURSE,
                 AWAITING_GROUP,
                 AWAITING_REGISTRATION_DATA_CONFIRMATION:
                registrationHandler.registerUser(update, user);
                break;
            case MAIN_MENU:
                mainMenuHandler.sendMenu(update, user, override);
                break;
            default:
                commandsHandler.resolveCommand(update, user);
        }
    }


    public boolean isUserActive(long chatId) {
        return userCache.existsByKey(chatId);
    }
}

package rozkladbot.telegram.router;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import rozkladbot.entities.User;
import rozkladbot.telegram.caching.SimpleUserCache;
import rozkladbot.telegram.handlers.*;
import rozkladbot.telegram.utils.message.MessageUtils;

import static rozkladbot.enums.UserState.*;

@Component("routerImpl")
public class RouterImpl implements Router {
    private final RegistrationHandler registrationHandler;
    private final NewUsersHandler newUsersHandler;
    private final SimpleUserCache userCache;
    private final MainMenuHandler mainMenuHandler;
    private final CommandsResolver commandsResolver;
    private final SettingsHandler settingHandler;
    private final ScheduleFetchHandler scheduleFetchHandler;
    private final AdminCommandsHandler adminCommandsHandler;

    @Autowired
    public RouterImpl(
            CommandsResolver commandsResolver,
            RegistrationHandler registrationHandler,
            SimpleUserCache userCache,
            NewUsersHandler newUsersHandler,
            MainMenuHandler mainMenuHandler,
            SettingsHandler settingHandler,
            ScheduleFetchHandler scheduleFetchHandler,
            AdminCommandsHandler adminCommandsHandler) {
        this.registrationHandler = registrationHandler;
        this.userCache = userCache;
        this.newUsersHandler = newUsersHandler;
        this.mainMenuHandler = mainMenuHandler;
        this.commandsResolver = commandsResolver;
        this.settingHandler = settingHandler;
        this.scheduleFetchHandler = scheduleFetchHandler;
        this.adminCommandsHandler = adminCommandsHandler;
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
        if (user.isRegistered()) {
            commandsResolver.resolveCommand(update, user);
        }
        if (user.getUserState().equals(AWAITING_SETTINGS)) {
            settingHandler.sendSettingsMenu(update, user, override);
        }
        if (user.getUserState().equals(AWAITING_GREETINGS)) {
            newUsersHandler.sendGreetings(user, update);
            return;
        }
        if (user.getUserState().equals(UNREGISTERED) ||
            user.getUserState().equals(AWAITING_INSTITUTE) ||
            user.getUserState().equals(AWAITING_FACULTY) ||
            user.getUserState().equals(AWAITING_COURSE) ||
            user.getUserState().equals(AWAITING_GROUP) ||
            user.getUserState().equals(AWAITING_REGISTRATION_DATA_CONFIRMATION)) {
            registrationHandler.registerUser(update, user);
        }
        if (user.getUserState().equals(AWAITING_THIS_WEEK_SCHEDULE) ||
            user.getUserState().equals(AWAITING_TODAY_SCHEDULE) ||
            user.getUserState().equals(AWAITING_TOMORROW_SCHEDULE) ||
            user.getUserState().equals(AWAITING_NEXT_WEEK_SCHEDULE)) {
            scheduleFetchHandler.resolveStates(update, user);
        }
        if (user.getUserState().equals(MAIN_MENU)) {
            mainMenuHandler.sendMenu(update, user, override);
        }
        if (user.getUserState().equals(AWAITING_USERS_LIST) ||
            user.getUserState().equals(AWAITING_APPLICATION_TERMINATION) ||
            user.getUserState().equals(AWAITING_DATA_SYNC) ||
            user.getUserState().equals(AWAITING_MESSAGE_SENDING)) {
            adminCommandsHandler.resolveStates(update, user);
        }
    }


    public boolean isUserActive(long chatId) {
        return userCache.existsByKey(chatId);
    }
}

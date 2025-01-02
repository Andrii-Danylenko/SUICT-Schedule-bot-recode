package rozkladbot.telegram.router;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import rozkladbot.entities.User;
import rozkladbot.telegram.caching.SimpleUserCache;
import rozkladbot.telegram.handlers.NewUsersHandler;
import rozkladbot.telegram.handlers.RegistrationHandler;

@Component("routerImpl")
public class RouterImpl implements Router {
    private final RegistrationHandler registrationHandler;
    private final NewUsersHandler newUsersHandler;
    private final SimpleUserCache userCache;

    @Autowired
    public RouterImpl(RegistrationHandler registrationHandler, SimpleUserCache userCache, NewUsersHandler newUsersHandler) {
        this.registrationHandler = registrationHandler;
        this.userCache = userCache;
        this.newUsersHandler = newUsersHandler;
    }

    @Override
    public void route(Update update, long chatId) {
        User user = userCache.get(chatId);
        if (user == null) {
            user = registrationHandler.formUser(update, chatId);
            userCache.put(chatId, user);
        }
        if (update.hasMessage()) {
            user.setLastSentMessageId(update.getMessage().getMessageId());
        }
        switch (user.getUserState()) {
            case AWAITING_GREETINGS:
                newUsersHandler.sendGreetings(user);
                break;
            case UNREGISTERED, AWAITING_INSTITUTE, AWAITING_FACULTY, AWAITING_COURSE, AWAITING_GROUP:
                registrationHandler.registerUser(update, user);
                break;
        }
    }


    public boolean isUserActive(long chatId) {
        return userCache.existsByKey(chatId);
    }
}

package rozkladbot.telegram.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import rozkladbot.entities.User;
import rozkladbot.enums.UserRole;
import rozkladbot.enums.UserState;

@Component("commandsResolverImpl")
public class CommandsResolverImpl implements CommandsResolver {

    @Override
    public void resolveCommand(Update update, User user) {
        if (update.hasCallbackQuery()) {
            resolveCallbackQueries(update.getCallbackQuery(), user);
        } else if (update.hasMessage()) {
            resolveTextMessages(update.getMessage().getText(), user);
        }
    }

    private void resolveTextMessages(String text, User user) {
        switch (text.toLowerCase()) {
            case "/day",
                 "/day@rozkad_bot" -> {
                user.setUserState(UserState.AWAITING_TODAY_SCHEDULE);
            }
            case "/nextday",
                 "/nextday@rozkad_bot" -> {
                user.setUserState(UserState.AWAITING_TOMORROW_SCHEDULE);
            }
            case "/week",
                 "/week@rozkad_bot" -> {
                user.setUserState(UserState.AWAITING_THIS_WEEK_SCHEDULE);
            }
            case "/nextweek",
                 "/nextweek@rozkad_bot" -> {
                user.setUserState(UserState.AWAITING_NEXT_WEEK_SCHEDULE);
            }
            case "/settings", "/settings@rozkad_bot" -> {
                user.setUserState(UserState.AWAITING_SETTINGS);
            }
            case "/menu", "/menu@rozkad_bot" -> {
                user.setUserState(UserState.MAIN_MENU);
            }
            case "/custom", "/custom@rozkad_bot" -> {
                user.setUserState(UserState.AWAITING_CUSTOM_SCHEDULE_QUERY);
            }
        }
        if (user.getUserRole().equals(UserRole.ADMIN)) {
            if ("/viewUsers".equalsIgnoreCase(text)) {
                user.setUserState(UserState.AWAITING_USERS_LIST);
            } else if (text.toLowerCase().startsWith("/synchronize")) {
                user.setUserState(UserState.AWAITING_DATA_SYNC);
            } else if (text.toLowerCase().startsWith("/sendmessage")) {
                user.setUserState(UserState.AWAITING_MESSAGE_SENDING);
            } else if ("/terminateSession".equalsIgnoreCase(text)) {
                user.setUserState(UserState.AWAITING_APPLICATION_TERMINATION);
            }
        }
    }

    private void resolveCallbackQueries(CallbackQuery callbackQuery, User user) {
        resolveTextMessages(callbackQuery.getData(), user);
    }

}

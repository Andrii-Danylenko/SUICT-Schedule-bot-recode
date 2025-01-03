package rozkladbot.telegram.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import rozkladbot.constants.BotMessageConstants;
import rozkladbot.entities.ScheduleTable;
import rozkladbot.entities.User;
import rozkladbot.enums.UserState;
import rozkladbot.services.ScheduleService;
import rozkladbot.telegram.factories.KeyBoardFactory;
import rozkladbot.telegram.utils.message.MessageSender;

import java.util.concurrent.ExecutionException;

@Component("commandsHandlerImpl")
public class CommandsHandlerImpl implements CommandsHandler {
    private final ScheduleService scheduleService;
    private final MessageSender messageSender;
    private final SettingsHandler settingsHandler;

    @Autowired
    public CommandsHandlerImpl(
            ScheduleService scheduleService,
            MessageSender messageSender,
            SettingsHandler settingsHandler) {
        this.scheduleService = scheduleService;
        this.messageSender = messageSender;
        this.settingsHandler = settingsHandler;
    }

    @Override
    public void resolveCommand(Update update, User user) {
        if (update.hasCallbackQuery()) {
            resolveCallbackQueries(update.getCallbackQuery(), user, update);
        } else if (update.hasMessage()) {
            resolveTextMessages(update.getMessage().getText(), user, false, update);
        }
    }

    private void resolveTextMessages(String text, User user, boolean isCallback, Update update) {
        switch (text.toLowerCase()) {
            case "/day",
                 "/day@rozkad_bot" -> user.setUserState(UserState.AWAITING_TODAY_SCHEDULE);
            case "/nextday",
                 "/nextday@rozkad_bot" -> user.setUserState(UserState.AWAITING_TOMORROW_SCHEDULE);
            case "/week",
                 "/week@rozkad_bot" -> user.setUserState(UserState.AWAITING_THIS_WEEK_SCHEDULE);
            case "/nextweek",
                 "/nextweek@rozkad_bot" -> user.setUserState(UserState.AWAITING_NEXT_WEEK_SCHEDULE);
            case "/settings", "/settings@rozkad_bot" -> user.setUserState(UserState.AWAITING_SETTINGS);
        }
        resolveStates(user, isCallback, update);
    }

    private void resolveCallbackQueries(CallbackQuery callbackQuery, User user, Update update) {
        resolveTextMessages(callbackQuery.getData(), user, true, update);
    }

    private void resolveStates(User user, boolean isCallback, Update update) {
        try {
            String scheduleName;
            ScheduleTable scheduleTable;
            switch (user.getUserState()) {
                case AWAITING_TOMORROW_SCHEDULE -> {
                    messageSender.sendMessage(
                            user,
                            BotMessageConstants.GET_SCHEDULE_ATTEMPT,
                            null,
                            isCallback);
                    scheduleName = BotMessageConstants.TOMORROW_SCHEDULE;
                    scheduleTable = scheduleService.getTomorrowLessons(user);
                }
                case AWAITING_THIS_WEEK_SCHEDULE -> {
                    messageSender.sendMessage(
                            user,
                            BotMessageConstants.GET_SCHEDULE_ATTEMPT,
                            null,
                            isCallback);
                    scheduleName = BotMessageConstants.WEEKLY_SCHEDULE;
                    scheduleTable = scheduleService.getWeeklyLessons(user);
                }
                case AWAITING_NEXT_WEEK_SCHEDULE -> {
                    messageSender.sendMessage(
                            user,
                            BotMessageConstants.GET_SCHEDULE_ATTEMPT,
                            null,
                            isCallback);
                    scheduleName = BotMessageConstants.NEXT_WEEK_SCHEDULE;
                    scheduleTable = scheduleService.getNextWeekLessons(user);
                }
                case AWAITING_SETTINGS -> {
                    settingsHandler.sendSettingsMenu(update, user, isCallback);
                    return;
                }
                default -> {
                    messageSender.sendMessage(
                            user,
                            BotMessageConstants.GET_SCHEDULE_ATTEMPT,
                            null,
                            isCallback);
                    scheduleName = BotMessageConstants.TODAY_SCHEDULE;
                    scheduleTable = scheduleService.getTodayLessons(user);
                }
            }
            messageSender.sendMessage(
                    user,
                    scheduleName + scheduleTable,
                    KeyBoardFactory.getBackButton(),
                    true);
        } catch (ExecutionException | InterruptedException e) {
            messageSender.sendMessage(
                    user,
                    BotMessageConstants.SCHEDULE_FETCHING_FAILED,
                    KeyBoardFactory.getBackButton(),
                    true);
        }
        user.setUserState(UserState.MAIN_MENU);
    }
}

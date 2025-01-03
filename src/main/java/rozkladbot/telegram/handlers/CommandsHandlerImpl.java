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

    @Autowired
    public CommandsHandlerImpl(ScheduleService scheduleService, MessageSender messageSender) {
        this.scheduleService = scheduleService;
        this.messageSender = messageSender;
    }

    @Override
    public void resolveCommand(Update update, User user) {
        if (update.hasCallbackQuery()) {
            resolveCallbackQueries(update.getCallbackQuery(), user);
        } else if (update.hasMessage()) {
            resolveTextMessages(update.getMessage().getText(), user, false);
        }
    }

    private void resolveTextMessages(String text, User user, boolean isCallback) {
        switch (text.toLowerCase()) {
            case "/day",
                 "/day@rozkad_bot" -> user.setUserState(UserState.AWAITING_TODAY_SCHEDULE);
            case "/nextday",
                 "/nextday@rozkad_bot" -> user.setUserState(UserState.AWAITING_TOMORROW_SCHEDULE);
            case "/week",
                 "/week@rozkad_bot" -> user.setUserState(UserState.AWAITING_THIS_WEEK_SCHEDULE);
            case "/nextweek",
                 "/nextweek@rozkad_bot" -> user.setUserState(UserState.AWAITING_NEXT_WEEK_SCHEDULE);
        }
        resolveStates(user, isCallback);
    }

    private void resolveCallbackQueries(CallbackQuery callbackQuery, User user) {
        resolveTextMessages(callbackQuery.getData(), user, true);
    }

    private void resolveStates(User user, boolean isCallback) {
        try {
            String scheduleName;
            ScheduleTable scheduleTable;
            messageSender.sendMessage(
                    user,
                    BotMessageConstants.GET_SCHEDULE_ATTEMPT,
                    null,
                    isCallback);
            switch (user.getUserState()) {
                case AWAITING_TOMORROW_SCHEDULE -> {
                    scheduleName = BotMessageConstants.TOMORROW_SCHEDULE;
                    scheduleTable = scheduleService.getTomorrowLessons(user);
                }
                case AWAITING_THIS_WEEK_SCHEDULE -> {
                    scheduleName = BotMessageConstants.WEEKLY_SCHEDULE;
                    scheduleTable = scheduleService.getWeeklyLessons(user);
                }
                case AWAITING_NEXT_WEEK_SCHEDULE -> {
                    scheduleName = BotMessageConstants.NEXT_WEEK_SCHEDULE;
                    scheduleTable = scheduleService.getNextWeekLessons(user);
                }
                default -> {
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

package rozkladbot.telegram.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import rozkladbot.constants.BotButtons;
import rozkladbot.constants.BotMessageConstants;
import rozkladbot.entities.DelayedCommand;
import rozkladbot.entities.ScheduleTable;
import rozkladbot.entities.User;
import rozkladbot.enums.Command;
import rozkladbot.enums.UserState;
import rozkladbot.services.ScheduleService;
import rozkladbot.telegram.factories.KeyBoardFactory;
import rozkladbot.telegram.utils.delayed.executor.DelayedCommandCache;
import rozkladbot.telegram.utils.message.MessageSender;

import java.util.concurrent.ExecutionException;

@Component
public class ScheduleFetchHandler {
    private final ScheduleService scheduleService;
    private final MessageSender messageSender;
    private final DelayedCommandCache delayedCommandCache;

    public ScheduleFetchHandler(
            ScheduleService scheduleService,
            MessageSender messageSender,
            DelayedCommandCache delayedCommandCache) {
        this.scheduleService = scheduleService;
        this.messageSender = messageSender;
        this.delayedCommandCache = delayedCommandCache;
    }

    public void resolveStates(Update update, User user, boolean override) {
        DelayedCommand delayedCommand = null;
        try {
            ScheduleTable scheduleTable;
            String messageToSend = BotMessageConstants.GET_SCHEDULE_ATTEMPT;
            messageSender.sendMessage(
                    user,
                    messageToSend,
                    null,
                    override,
                    update);
            switch (user.getUserState()) {
                case AWAITING_TODAY_SCHEDULE -> {
                    delayedCommand = new DelayedCommand(Command.GET_TODAY_SCHEDULE);
                    scheduleTable = getThisDaySchedule(user);
                    messageToSend = BotMessageConstants.TODAY_SCHEDULE;
                }
                case AWAITING_TOMORROW_SCHEDULE -> {
                    delayedCommand = new DelayedCommand(Command.GET_TOMORROW_SCHEDULE);
                    scheduleTable = getTomorrowSchedule(user);
                    messageToSend = BotMessageConstants.TOMORROW_SCHEDULE;
                }
                case AWAITING_THIS_WEEK_SCHEDULE -> {
                    delayedCommand = new DelayedCommand(Command.GET_THIS_WEEK_SCHEDULE);
                    scheduleTable = getThisWeekSchedule(user);
                    messageToSend = BotMessageConstants.WEEKLY_SCHEDULE;
                }
                default -> {
                    delayedCommand = new DelayedCommand(Command.GET_NEXT_WEEK_SCHEDULE);
                    scheduleTable = getNextWeekSchedule(user);
                    messageToSend = BotMessageConstants.NEXT_WEEK_SCHEDULE;
                }
            }
            messageSender.sendMessage(
                    user,
                    messageToSend + scheduleTable.toString(),
                    KeyBoardFactory.getCustomButton(BotButtons.BACK_TO_MENU, BotButtons.BACK_TO_MENU_DATA),
                    true,
                    update);
        } catch (ExecutionException | InterruptedException e) {
            if (delayedCommand != null) {
                // Just in case Telegram API throws an exception into me
                delayedCommandCache.add(user.getId(), delayedCommand);
            }
            messageSender.sendMessage(
                    user,
                    BotMessageConstants.SCHEDULE_FETCHING_FAILED,
                    KeyBoardFactory.getCustomButton(BotButtons.BACK_TO_MENU, BotButtons.BACK_TO_MENU_DATA),
                    true,
                    update);
        }
        user.setUserState(UserState.IDLE);
    }

    private ScheduleTable getThisDaySchedule(User user) throws ExecutionException, InterruptedException {
        return scheduleService.getTodayLessons(user);
    }

    private ScheduleTable getTomorrowSchedule(User user) throws ExecutionException, InterruptedException {
        return scheduleService.getTomorrowLessons(user);
    }

    private ScheduleTable getThisWeekSchedule(User user) throws ExecutionException, InterruptedException {
        return scheduleService.getWeeklyLessons(user);
    }

    private ScheduleTable getNextWeekSchedule(User user) throws ExecutionException, InterruptedException {
        return scheduleService.getNextWeekLessons(user);
    }
}

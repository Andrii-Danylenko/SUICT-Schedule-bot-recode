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
import rozkladbot.exceptions.CustomScheduleFetchException;
import rozkladbot.services.ScheduleService;
import rozkladbot.telegram.factories.KeyBoardFactory;
import rozkladbot.telegram.utils.delayed.executor.DelayedCommandCache;
import rozkladbot.telegram.utils.message.MessageSender;
import rozkladbot.utils.date.DateUtils;

import java.io.IOException;
import java.net.URISyntaxException;
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
            boolean isCustom = false;
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
                    scheduleTable = scheduleService.getTodayLessons(user);
                    messageToSend = BotMessageConstants.TODAY_SCHEDULE + scheduleTable.toString();
                    user.setUserState(UserState.IDLE);
                }
                case AWAITING_TOMORROW_SCHEDULE -> {
                    delayedCommand = new DelayedCommand(Command.GET_TOMORROW_SCHEDULE);
                    scheduleTable = scheduleService.getTomorrowLessons(user);
                    messageToSend = BotMessageConstants.TOMORROW_SCHEDULE + scheduleTable.toString();
                    user.setUserState(UserState.IDLE);
                }
                case AWAITING_THIS_WEEK_SCHEDULE -> {
                    delayedCommand = new DelayedCommand(Command.GET_THIS_WEEK_SCHEDULE);
                    scheduleTable = scheduleService.getWeeklyLessons(user);
                    messageToSend = BotMessageConstants.WEEKLY_SCHEDULE + scheduleTable.toString();
                    user.setUserState(UserState.IDLE);
                }
                case UserState.AWAITING_CUSTOM_SCHEDULE_QUERY -> {
                    messageToSend = BotMessageConstants.CUSTOM_SCHEDULE_QUERY_EXAMPLE
                            .formatted(
                                    user.getGroup().getName(),
                                    DateUtils.getTodayDateString(),
                                    DateUtils.getDateAsString(DateUtils.getToday().plusDays(7)));
                    user.setUserState(UserState.AWAITING_CUSTOM_SCHEDULE);
                }
                case AWAITING_CUSTOM_SCHEDULE -> {
                    scheduleTable = scheduleService.getScheduleWithCustomParameters(user, update);
                    int scheduleTableSize = scheduleTable.getDays().size();
                    int i = 0;
                    do {
                        messageSender.sendMessage(
                                user,
                                scheduleService.splitBigTableIntoSmall(scheduleTable).toString(),
                                null,
                                i == 0,
                                update);
                        i++;
                    } while (scheduleTable.getDays().size() > 7);
                    if (!scheduleTable.getDays().isEmpty() && scheduleTableSize > 7) {
                        messageSender.sendMessage(
                                user,
                                scheduleService.splitBigTableIntoSmall(scheduleTable).toString(),
                                null,
                                false,
                                update);
                    }
                    messageSender.sendMessage(
                            user,
                            BotMessageConstants.BACK_TO_MENU,
                            KeyBoardFactory.getCustomButton(BotButtons.BACK_TO_MENU, BotButtons.BACK_TO_MENU_DATA),
                            false,
                            update);
                    isCustom = true;
                    user.setUserState(UserState.IDLE);
                }
                case AWAITING_NEXT_WEEK_SCHEDULE -> {
                    delayedCommand = new DelayedCommand(Command.GET_NEXT_WEEK_SCHEDULE);
                    scheduleTable = scheduleService.getNextWeekLessons(user);
                    messageToSend = BotMessageConstants.NEXT_WEEK_SCHEDULE + scheduleTable.toString();
                    user.setUserState(UserState.IDLE);
                }
                default -> {
                    messageToSend = BotMessageConstants.UNRESOLVED_USER_STATE;
                    user.setUserState(UserState.IDLE);
                }
            }
            if (!isCustom) {
                messageSender.sendMessage(
                        user,
                        messageToSend,
                        KeyBoardFactory.getCustomButton(BotButtons.BACK_TO_MENU, BotButtons.BACK_TO_MENU_DATA),
                        true,
                        update);
            }

        } catch (ExecutionException | InterruptedException | URISyntaxException | CustomScheduleFetchException |
                 IOException e) {
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
            user.setUserState(UserState.IDLE);
        }
    }
}

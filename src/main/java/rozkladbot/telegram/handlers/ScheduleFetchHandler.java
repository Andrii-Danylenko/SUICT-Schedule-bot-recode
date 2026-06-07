package rozkladbot.telegram.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import rozkladbot.components.ScheduleTableNormalizer;
import rozkladbot.constants.BotButtons;
import rozkladbot.constants.BotMessageConstants;
import rozkladbot.entities.HandlerContext;
import rozkladbot.entities.ScheduleTable;
import rozkladbot.entities.User;
import rozkladbot.enums.UserState;
import rozkladbot.exceptions.CustomScheduleFetchException;
import rozkladbot.services.ScheduleService;
import rozkladbot.telegram.factories.KeyBoardFactory;
import rozkladbot.telegram.utils.message.MessageSender;
import rozkladbot.utils.date.DateUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

@Component
public class ScheduleFetchHandler implements HandlerStrategy {

  private final ScheduleService scheduleService;
  private final MessageSender messageSender;
  private final ScheduleTableNormalizer scheduleTableNormalizer;

  public ScheduleFetchHandler(
      ScheduleService scheduleService,
      MessageSender messageSender,
      ScheduleTableNormalizer scheduleTableNormalizer) {
    this.scheduleService = scheduleService;
    this.messageSender = messageSender;
    this.scheduleTableNormalizer = scheduleTableNormalizer;
  }

  public void handleRequest(HandlerContext ctx) {
    User user = ctx.getUser();
    Update update = ctx.getUpdate();
    try {
      boolean isCustom = false;
      ScheduleTable scheduleTable;
      String messageToSend = BotMessageConstants.GET_SCHEDULE_ATTEMPT;
      messageSender.sendMessage(
          user,
          messageToSend,
          null,
          ctx.isOverrideMessage(),
          update);
      switch (user.getUserState()) {
        case AWAITING_TODAY_SCHEDULE -> {
          scheduleTable = scheduleService.getTodayLessons(user);
          messageToSend = BotMessageConstants.TODAY_SCHEDULE + scheduleTable.toString();
          user.setUserState(UserState.IDLE);
        }
        case AWAITING_TOMORROW_SCHEDULE -> {
          scheduleTable = scheduleService.getTomorrowLessons(user);
          messageToSend = BotMessageConstants.TOMORROW_SCHEDULE + scheduleTable.toString();
          user.setUserState(UserState.IDLE);
        }
        case AWAITING_THIS_WEEK_SCHEDULE -> {
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
          int i = 0;
          do {
            messageSender.sendMessage(
                user,
                scheduleTableNormalizer.splitBigTableIntoSmall(scheduleTable).toString(),
                null,
                i == 0,
                update);
            i++;
          } while (!scheduleTable.getDays().isEmpty());
          messageToSend = BotMessageConstants.BACK_TO_MENU;
          isCustom = true;
          user.setUserState(UserState.IDLE);
        }
        case AWAITING_NEXT_WEEK_SCHEDULE -> {
          scheduleTable = scheduleService.getNextWeekLessons(user);
          messageToSend = BotMessageConstants.NEXT_WEEK_SCHEDULE + scheduleTable.toString();
          user.setUserState(UserState.IDLE);
        }
        default -> {
          messageToSend = BotMessageConstants.UNRESOLVED_USER_STATE;
          user.setUserState(UserState.IDLE);
        }
      }
      messageSender.sendMessage(
          user,
          messageToSend,
          KeyBoardFactory.getCustomButton(BotButtons.BACK_TO_MENU, BotButtons.BACK_TO_MENU_DATA),
          !isCustom,
          update);

    } catch (ExecutionException | InterruptedException | URISyntaxException |
             CustomScheduleFetchException |
             IOException e) {
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

package rozkladbot.telegram.utils.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import rozkladbot.constants.AppConstants;
import rozkladbot.constants.BotMessageConstants;
import rozkladbot.constants.ErrorConstants;
import rozkladbot.constants.LoggingConstants;
import rozkladbot.entities.User;
import rozkladbot.services.ScheduleService;
import rozkladbot.services.UserService;
import rozkladbot.telegram.caching.UserCache;
import rozkladbot.telegram.utils.message.MessageSender;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Component("scheduleBroadcaster")
public class ScheduleBroadcaster {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleBroadcaster.class);
    private final MessageSender messageSender;
    private final UserCache userCache;
    private final ScheduleService scheduleService;
    private final UserService userService;

    @Autowired
    public ScheduleBroadcaster(
            MessageSender messageSender,
            UserCache userCache,
            ScheduleService scheduleService,
            UserService userService) {
        this.messageSender = messageSender;
        this.userCache = userCache;
        this.scheduleService = scheduleService;
        this.userService = userService;
    }

    @Scheduled(cron = "0 0 19 * * *", zone = AppConstants.APPLICATION_TIME_ZONE)
    public void broadcastAndPinTomorrowSchedule() {
        logger.info(LoggingConstants.BEGIN_BROADCAST_MESSAGE);
        try {
            Set<User> users = userCache.getAll();
            CompletableFuture<?>[] futures = users.stream()
                    .filter(User::isBroadcasted)
                    .map(user -> CompletableFuture.runAsync(() -> processUser(user.getId(), user)))
                    .toArray(CompletableFuture[]::new);
            CompletableFuture.allOf(futures).join();
        } finally {
            logger.info(LoggingConstants.END_BROADCAST_MESSAGE);
        }
    }

    private void processUser(Long chatId, User user) {
        if (user.getLastPinnedMessageId() != 0) {
            try {
                logger.info(LoggingConstants.TRYING_TO_UNPIN_MESSAGE, chatId);
                messageSender.unpinMessage(user);
                logger.info(LoggingConstants.MESSAGE_UNPINNED_SUCCESSFULLY, chatId);
            } catch (TelegramApiException e) {
                logger.error(ErrorConstants.MESSAGE_CANNOT_BE_UNPINNED, e);
            }
        }
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.enableHtml(true);
        sendMessage.setParseMode(AppConstants.BOT_MESSAGE_PARSE_MODE);
        try {
            sendMessage.setText(BotMessageConstants.TOMORROW_SCHEDULE + scheduleService.getTomorrowLessons(user));
            int messageToPin = messageSender.executeSilentSender(sendMessage);
            logger.info(LoggingConstants.TRYING_TO_PIN_MESSAGE, chatId);
            messageSender.pinMessage(user, messageToPin);
            user.setLastPinnedMessageId(messageToPin);
            userService.save(user);
            logger.info(LoggingConstants.MESSAGE_PINNED_SUCCESSFULLY, chatId);
        } catch (Exception exception) {
            logger.error(ErrorConstants.MESSAGE_CANNOT_BE_PINNED);
            sendMessage.setText(BotMessageConstants.BROADCASTING_FAILED);
            messageSender.executeSilentSender(sendMessage);
        }
    }
}

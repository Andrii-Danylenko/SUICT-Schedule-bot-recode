package rozkladbot.telegram.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component("messageUtils")
public class MessageUtils {
    public static int getCorrectMessageIdWithOffset(Update update) {
        int messageId = 0;
        if (update.hasMessage()) {
            messageId = update.getMessage().getMessageId() + 1;
        } else if (update.hasCallbackQuery()) {
            messageId = update.getCallbackQuery().getMessage().getMessageId();
        }
        return messageId;
    }
}

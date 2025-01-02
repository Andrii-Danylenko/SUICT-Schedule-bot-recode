package rozkladbot.telegram.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface ResponseHandler {
    void replyToStart(Update update, long chatId);
}

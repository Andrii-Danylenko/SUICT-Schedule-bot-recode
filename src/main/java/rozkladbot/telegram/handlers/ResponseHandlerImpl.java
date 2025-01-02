package rozkladbot.telegram.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component("responseHandlerImpl")
public class ResponseHandlerImpl implements ResponseHandler {

    @Override
    public void replyToStart(Update update, long chatId) {

    }
}

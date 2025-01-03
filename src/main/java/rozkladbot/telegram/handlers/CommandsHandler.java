package rozkladbot.telegram.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;
import rozkladbot.entities.User;

public interface CommandsHandler {
    void resolveCommand(Update update, User user);
}

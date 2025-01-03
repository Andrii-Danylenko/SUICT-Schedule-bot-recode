package rozkladbot.telegram.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import rozkladbot.constants.BotMessageConstants;
import rozkladbot.entities.User;
import rozkladbot.enums.UserState;
import rozkladbot.telegram.factories.KeyBoardFactory;
import rozkladbot.telegram.utils.message.MessageSender;

@Component("mainMenuHandler")
public class MainMenuHandler {
    private final MessageSender messageSender;
    @Autowired
    public MainMenuHandler(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void sendMenu(Update update, User user) {
        messageSender.sendMessage(
                user,
                BotMessageConstants.AVAILABLE_USER_COMMANDS,
                KeyBoardFactory.getCommandsList(),
                true);
        user.setUserState(UserState.IDLE);
    }
}

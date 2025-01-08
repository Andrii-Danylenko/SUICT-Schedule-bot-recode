package rozkladbot.telegram.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import rozkladbot.constants.BotMessageConstants;
import rozkladbot.entities.User;
import rozkladbot.enums.UserState;
import rozkladbot.telegram.factories.KeyBoardFactory;
import rozkladbot.telegram.utils.message.MessageSender;

@Component
public class NewUsersHandler {
    private final MessageSender messageSender;

    @Autowired
    public NewUsersHandler(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void sendGreetings(User user, Update update) {
        messageSender.sendMessage(
                user,
                BotMessageConstants.GREETING_MESSAGE + BotMessageConstants.AVAILABLE_USER_COMMANDS,
                KeyBoardFactory.getCommandsList(),
                false, update);
        user.setUserState(UserState.AWAITING_INSTITUTE);
    }
}

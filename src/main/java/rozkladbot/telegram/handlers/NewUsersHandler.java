package rozkladbot.telegram.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rozkladbot.constants.BotMessageConstants;
import rozkladbot.entities.HandlerContext;
import rozkladbot.enums.UserState;
import rozkladbot.telegram.factories.KeyBoardFactory;
import rozkladbot.telegram.utils.message.MessageSender;

@Component
public class NewUsersHandler implements HandlerStrategy {
    private final MessageSender messageSender;

    @Autowired
    public NewUsersHandler(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void handleRequest(HandlerContext ctx) {
        messageSender.sendMessage(
            ctx.getUser(),
                BotMessageConstants.GREETING_MESSAGE + BotMessageConstants.AVAILABLE_USER_COMMANDS,
                KeyBoardFactory.getCommandsList(),
                false, ctx.getUpdate());
        ctx.getUser().setUserState(UserState.AWAITING_INSTITUTE);
    }
}

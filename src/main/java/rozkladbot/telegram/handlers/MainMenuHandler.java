package rozkladbot.telegram.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rozkladbot.constants.BotMessageConstants;
import rozkladbot.entities.HandlerContext;
import rozkladbot.enums.UserRole;
import rozkladbot.enums.UserState;
import rozkladbot.telegram.factories.KeyBoardFactory;
import rozkladbot.telegram.utils.message.MessageSender;
import rozkladbot.telegram.utils.message.MessageUtils;

@Component("mainMenuHandler")
public class MainMenuHandler implements HandlerStrategy {
    private final MessageSender messageSender;

    @Autowired
    public MainMenuHandler(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void handleRequest(HandlerContext ctx) {
        messageSender.sendMessage(
                ctx.getUser(),
                (ctx.getUser().getUserRole().equals(UserRole.ADMIN) ?
                        BotMessageConstants.AVAILABLE_USER_COMMANDS + BotMessageConstants.AVAILABLE_ADMIN_COMMANDS :
                        BotMessageConstants.AVAILABLE_USER_COMMANDS),
                KeyBoardFactory.getCommandsList(),
                ctx.isOverrideMessage(), ctx.getUpdate());
        ctx.getUser().setUserState(UserState.IDLE);
        ctx.getUser().setLastSentMessageId(MessageUtils.getCorrectMessageIdWithOffset(ctx.getUpdate()));
    }
}

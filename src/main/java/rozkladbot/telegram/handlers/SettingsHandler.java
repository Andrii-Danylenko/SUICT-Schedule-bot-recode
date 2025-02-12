package rozkladbot.telegram.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import rozkladbot.constants.BotButtons;
import rozkladbot.constants.BotMessageConstants;
import rozkladbot.entities.User;
import rozkladbot.enums.UserState;
import rozkladbot.telegram.factories.KeyBoardFactory;
import rozkladbot.telegram.utils.message.MessageSender;
import rozkladbot.telegram.utils.message.MessageUtils;

@Component("settingHandler")
public class SettingsHandler {
    private final MessageSender messageSender;

    @Autowired
    public SettingsHandler(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void sendSettingsMenu(Update update, User user, boolean override) {
        user.setLastSentMessageId(MessageUtils.getCorrectMessageIdWithOffset(update));
        if (update.hasCallbackQuery()) {
            if (BotButtons.BACK_DATA.equals(update.getCallbackQuery().getData())) {
                user.setUserState(UserState.MAIN_MENU);
                return;
            }
            else if (BotButtons.DISABLED_DATA.equals(update.getCallbackQuery().getData())) {
                user.setBroadcasted(false);
            }
            else if (BotButtons.ENABLED_DATA.equals(update.getCallbackQuery().getData())) {
                user.setBroadcasted(true);
            }
            else if (BotButtons.CHANGE_GROUP_DATA.equals(update.getCallbackQuery().getData())) {
                user.setUserState(UserState.AWAITING_INSTITUTE);
                return;
            }
        }
        messageSender.sendMessage(
                user,
                BotMessageConstants.SETTINGS_MENU.formatted(
                        user.getGroup().getFaculty().getInstitute().getInstituteName(),
                        user.getGroup().getFaculty().getFacultyName(),
                        user.getGroup().getName(),
                        user.getGroup().getGroupId(),
                        user.getGroup().getCourse(),
                        user.isBroadcasted() ? BotMessageConstants.IS_BROADCASTED : BotMessageConstants.IS_NOT_BROADCASTED
                ),
                KeyBoardFactory.getSettings(user.isBroadcasted()), override, update);
        user.setLastSentMessageId(MessageUtils.getCorrectMessageIdWithOffset(update));
    }
}

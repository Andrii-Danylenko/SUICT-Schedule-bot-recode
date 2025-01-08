package rozkladbot.telegram.utils.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.UnpinChatMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import rozkladbot.constants.AppConstants;
import rozkladbot.entities.User;

@Component("messageSender")
public class MessageSender {
    private final AbilityBot abilityBot;
    private final SilentSender sender;

    @Autowired
    public MessageSender(AbilityBot abilityBot, SilentSender sender) {
        this.abilityBot = abilityBot;
        this.sender = sender;
    }

    public void sendMessage(User currentUser, String message, ReplyKeyboard keyboard, boolean overrideMessage, Update update) {
        if (overrideMessage) {
            overrideMessage(currentUser, message, keyboard, update);
        } else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(currentUser.getId());
            sendMessage.setText(message);
            sendMessage.setParseMode(AppConstants.BOT_MESSAGE_PARSE_MODE);
            if (keyboard != null) {
                sendMessage.setReplyMarkup(keyboard);
            }
            sender.execute(sendMessage);
        }
    }

    private void overrideMessage(User currentUser, String message, ReplyKeyboard keyboard, Update update) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setText(message);
        editMessageText.setChatId(currentUser.getId());
        // If update sent via callback query, then we just edit callback message
        if (update.hasCallbackQuery()) {
            editMessageText.setMessageId(currentUser.getLastSentMessageId());
        } else if (update.hasMessage()) {
            // If update sent via text message, then we must skip users message and edit next bot message
            editMessageText.setMessageId(currentUser.getLastSentMessageId() + 1);
        }
        editMessageText.setParseMode(AppConstants.BOT_MESSAGE_PARSE_MODE);
        if (keyboard != null) editMessageText.setReplyMarkup((InlineKeyboardMarkup) keyboard);
        sender.execute(editMessageText);
    }

    public void unpinMessage(User user) throws TelegramApiException {
        UnpinChatMessage unpinMessage = new UnpinChatMessage(
                String.valueOf(user.getId()),
                user.getLastPinnedMessageId());
        this.abilityBot.execute(unpinMessage);
    }

    public void pinMessage(User user, int messageToPin) {
        PinChatMessage pinMessage = new PinChatMessage(String.valueOf(user.getId()), messageToPin);
        sender.execute(pinMessage);
    }

    public int executeSilentSender(SendMessage message) {
        return sender.execute(message).orElseThrow(RuntimeException::new).getMessageId();
    }
}

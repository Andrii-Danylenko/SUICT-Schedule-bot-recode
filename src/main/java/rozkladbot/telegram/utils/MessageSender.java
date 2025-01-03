package rozkladbot.telegram.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
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

    public void sendMessage(User currentUser, String message, ReplyKeyboard keyboard, boolean overrideMessage) {
        if (overrideMessage) {
            overrideMessage(currentUser, message, keyboard);
        } else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(currentUser.getId());
            sendMessage.setText(message);
            sendMessage.setParseMode("html");
            if (keyboard != null) {
                sendMessage.setReplyMarkup(keyboard);
            }
            sender.execute(sendMessage);
        }
    }

    private void overrideMessage(User currentUser, String message, ReplyKeyboard keyboard) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setText(message);
        editMessageText.setChatId(currentUser.getId());
        editMessageText.setMessageId(currentUser.getLastSentMessageId());
        editMessageText.setParseMode("html");
        if (keyboard != null) editMessageText.setReplyMarkup((InlineKeyboardMarkup) keyboard);
        sender.execute(editMessageText);
    }
}

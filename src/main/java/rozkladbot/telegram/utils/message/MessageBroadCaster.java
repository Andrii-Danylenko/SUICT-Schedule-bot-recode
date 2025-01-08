package rozkladbot.telegram.utils.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rozkladbot.services.UserService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("messageBroadcaster")
public class MessageBroadCaster {
    private final MessageSender messageSender;
    private final UserService userService;

    @Autowired
    public MessageBroadCaster(
            MessageSender messageSender,
            UserService userService
    ) {
        this.messageSender = messageSender;
        this.userService = userService;
    }

    public void broadcast(String message) {
        String[] splittedMessage = splitMessageForIds(message);
        String messageToSend = splitMessageForText(message);
        Set<Long> idsToBroadCast;
        if ("-all".equals(splittedMessage[0])) {
            idsToBroadCast = userService.getAllUserIds();
        } else {
            idsToBroadCast = splitByKeys(splittedMessage);
        }
        for (Long id : idsToBroadCast) {
            messageSender.sendMessage(
                    userService.getById(id),
                    messageToSend,
                    null,
                    false,
                    null);
        }
    }

    private String[] splitMessageForIds(String message) {
        // We're skipping 1 because first element is always /sendMessage
        return Arrays.stream(message.split(" ")).skip(1).toArray(String[]::new);
    }

    private String splitMessageForText(String message) {
        String regex = "\"([^\"]+)\"";
        String quotedText = "null";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            quotedText = matcher.group(1);
        }
        return quotedText;
    }

    private Set<Long> splitByKeys(String[] splittedMessage) {
        Set<Long> idsToBroadcast = new HashSet<>();
        for (String s : splittedMessage) {
            try {
                idsToBroadcast.add(Long.parseLong(s));
            } catch (NumberFormatException ignored) {
            }
        }
        return idsToBroadcast;
    }
}

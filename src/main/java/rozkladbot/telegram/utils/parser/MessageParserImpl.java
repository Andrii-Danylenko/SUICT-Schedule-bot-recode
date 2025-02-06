package rozkladbot.telegram.utils.parser;

import org.springframework.stereotype.Component;
import rozkladbot.constants.AppConstants;
import rozkladbot.constants.ErrorConstants;
import rozkladbot.exceptions.MessageSplitException;

@Component("messageParser")
public class MessageParserImpl implements MessageParser {
    public String[] getParametersFromMessage(String message) {
        String[] parameters = message.split(AppConstants.CUSTOM_SCHEDULE_INPUT_FORMAT_REGEX);
        if (parameters.length != 3) throw new MessageSplitException(ErrorConstants.WRONG_AMOUNT_OF_SPLITTED_PARAMETERS.formatted(parameters.length));
        return parameters;
    }
}

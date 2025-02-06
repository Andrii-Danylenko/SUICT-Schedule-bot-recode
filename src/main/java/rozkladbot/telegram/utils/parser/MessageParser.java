package rozkladbot.telegram.utils.parser;

public interface MessageParser {
    String[] getParametersFromMessage(String message);
}

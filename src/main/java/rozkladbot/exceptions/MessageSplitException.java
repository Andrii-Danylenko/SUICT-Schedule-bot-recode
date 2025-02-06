package rozkladbot.exceptions;

public class MessageSplitException extends RuntimeException {

    public MessageSplitException() {
    }

    public MessageSplitException(String message) {
        super(message);
    }

    public MessageSplitException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageSplitException(Throwable cause) {
        super(cause);
    }

    public MessageSplitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

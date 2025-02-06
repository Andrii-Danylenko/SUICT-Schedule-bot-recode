package rozkladbot.exceptions;

public class CustomScheduleFetchException extends RuntimeException {
    public CustomScheduleFetchException() {
    }

    public CustomScheduleFetchException(String message) {
        super(message);
    }

    public CustomScheduleFetchException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomScheduleFetchException(Throwable cause) {
        super(cause);
    }

    public CustomScheduleFetchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

package rozkladbot.exceptions;

public class LocalFileReadingFailedException extends RuntimeException {
    public LocalFileReadingFailedException(String message) {
        super(message);
    }

    public LocalFileReadingFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public LocalFileReadingFailedException(Throwable cause) {
        super(cause);
    }

    public LocalFileReadingFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public LocalFileReadingFailedException() {
    }
}

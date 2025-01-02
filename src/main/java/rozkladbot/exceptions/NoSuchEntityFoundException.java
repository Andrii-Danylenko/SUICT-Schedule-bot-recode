package rozkladbot.exceptions;

public class NoSuchEntityFoundException extends RuntimeException {
    public NoSuchEntityFoundException(String message) {
        super(message);
    }
}

package rozkladbot.exceptions;

public class RequestCreationFailedException extends RuntimeException {
    public RequestCreationFailedException(String message) {
        super(message);
    }
}

package rozkladbot.exceptions;

public class EmptyRequestParametersException extends RuntimeException {
    public EmptyRequestParametersException(String message) {
        super(message);
    }
}

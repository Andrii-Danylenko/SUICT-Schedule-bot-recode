package rozkladbot.exceptions;

public class NoSuchHandlerStrategyFoundException extends RuntimeException {

  public NoSuchHandlerStrategyFoundException() {
    super();
  }

  public NoSuchHandlerStrategyFoundException(String message) {
    super(message);
  }

  public NoSuchHandlerStrategyFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public NoSuchHandlerStrategyFoundException(Throwable cause) {
    super(cause);
  }

  protected NoSuchHandlerStrategyFoundException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}

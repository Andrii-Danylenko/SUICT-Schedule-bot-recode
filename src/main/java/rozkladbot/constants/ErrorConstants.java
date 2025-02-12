package rozkladbot.constants;

public final class ErrorConstants {
    public final static String PARAMS_ARE_EMPTY = "Parameters could not be empty! Please, check your request data try again. ";
    public final static String REQUEST_CREATION_FAILED = "An error occurred while creating the request! Please try again.";
    public final static String JSON_PROCESSING_FAILED = "An error occurred while processing the JSON file! Please try again.";
    public final static String ENTITY_NOT_FOUND_EXCEPTION = "No entity with such ID found!";
    public final static String MESSAGE_CANNOT_BE_UNPINNED = "Message could not be unpinned!";
    public final static String MESSAGE_CANNOT_BE_PINNED = "Message could not be pinned!";
    public final static String WRONG_AMOUNT_OF_SPLIT_PARAMETERS = "Wrong amount of split parameters! Expected 3, got: %d";
    public final static String WRONG_AMOUNT_OF_QUERY_PARAMETERS = "The number of values must be even";
    private ErrorConstants() {}
}

package ca.fortdefense.exception;

public class BadInputException extends RuntimeException {

    public BadInputException() {
    }

    public BadInputException(String message) {
        super(message);
    }
}

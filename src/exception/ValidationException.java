package exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message, int task1, int task2) {
        super(message + task1 + ", " + task2);
    }

    public ValidationException(String message) {
        super(message);
    }
}

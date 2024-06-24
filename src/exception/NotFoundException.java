package exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message, int id) {
        super(message + id);
    }
}

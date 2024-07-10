package task.manager.service.exception;

public class WrongTaskException extends RuntimeException {
    public WrongTaskException() {
    }

    public WrongTaskException(String message) {
        super(message);
    }
}

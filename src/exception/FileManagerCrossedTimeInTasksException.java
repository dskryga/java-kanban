package exception;

public class FileManagerCrossedTimeInTasksException extends RuntimeException {
    public FileManagerCrossedTimeInTasksException(String message) {
        super(message);
    }
}

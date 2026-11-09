package ca.yorku.eecs4413.fritzev.exception;

// Signed in but not allowed, maps to 403.
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}

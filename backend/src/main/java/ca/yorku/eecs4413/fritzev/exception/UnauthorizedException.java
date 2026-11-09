package ca.yorku.eecs4413.fritzev.exception;

// No signed-in user, maps to 401.
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}

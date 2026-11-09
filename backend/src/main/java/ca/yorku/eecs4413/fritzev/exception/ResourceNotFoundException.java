package ca.yorku.eecs4413.fritzev.exception;

// A requested row (vehicle, cart item, booking...) does not exist.
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

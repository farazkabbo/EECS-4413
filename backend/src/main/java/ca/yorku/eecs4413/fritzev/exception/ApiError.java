package ca.yorku.eecs4413.fritzev.exception;

import java.time.Instant;
import java.util.Map;

// The single JSON error shape every endpoint returns.
public record ApiError(Instant timestamp, int status, String error, String message, String path,
                       Map<String, String> fieldErrors) {
}

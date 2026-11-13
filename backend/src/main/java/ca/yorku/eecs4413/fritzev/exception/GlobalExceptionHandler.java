package ca.yorku.eecs4413.fritzev.exception; 

import jakarta.servlet.http.HttpServletRequest; 
import jakarta.validation.ConstraintViolationException; 
import org.slf4j.Logger; 
import org.slf4j.LoggerFactory; 
import org.springframework.dao.DataIntegrityViolationException; 
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity; 
import org.springframework.http.converter.HttpMessageNotReadableException; 
import org.springframework.web.HttpRequestMethodNotSupportedException; 
import org.springframework.web.bind.MethodArgumentNotValidException; 
import org.springframework.web.bind.MissingServletRequestParameterException; 
import org.springframework.web.bind.annotation.ExceptionHandler; 
import org.springframework.web.bind.annotation.RestControllerAdvice; 
import org.springframework.web.method.annotation.HandlerMethodValidationException; 
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException; 
import org.springframework.web.servlet.resource.NoResourceFoundException; 

import java.time.Instant; 
import java.util.LinkedHashMap; 
import java.util.Map; 

// Turns every exception into a clean ApiError JSON response, so bad input never crashes the server or leaks a stack trace (part of M3 Web Controllers). 
@RestControllerAdvice 
public class GlobalExceptionHandler { 

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class); 

    // ---------- our own exceptions ---------- 
    @ExceptionHandler(ResourceNotFoundException.class) 
    ResponseEntity<ApiError> notFound(ResourceNotFoundException e, HttpServletRequest r) { 
        return build(HttpStatus.NOT_FOUND, e.getMessage(), r, null); 
    } 

    @ExceptionHandler({BadRequestException.class, IllegalArgumentException.class}) 
    ResponseEntity<ApiError> badRequest(RuntimeException e, HttpServletRequest r) { 
        return build(HttpStatus.BAD_REQUEST, e.getMessage(), r, null); 
    } 

    @ExceptionHandler(UnauthorizedException.class) 
    ResponseEntity<ApiError> unauthorized(UnauthorizedException e, HttpServletRequest r) { 
        return build(HttpStatus.UNAUTHORIZED, e.getMessage(), r, null); 
    } 

    @ExceptionHandler(ForbiddenException.class) 
    ResponseEntity<ApiError> forbidden(ForbiddenException e, HttpServletRequest r) { 
        return build(HttpStatus.FORBIDDEN, e.getMessage(), r, null); 
    } 

    @ExceptionHandler(ConflictException.class) 
    ResponseEntity<ApiError> conflict(ConflictException e, HttpServletRequest r) { 
        return build(HttpStatus.CONFLICT, e.getMessage(), r, null); 
    } 

    @ExceptionHandler(NotImplementedYetException.class) 
    ResponseEntity<ApiError> notImplemented(NotImplementedYetException e, HttpServletRequest r) { 
        return build(HttpStatus.NOT_IMPLEMENTED, e.getMessage(), r, null); 
    } 

    // ---------- input validation ---------- 

    // A @Valid @RequestBody failed 
    @ExceptionHandler(MethodArgumentNotValidException.class) 
    ResponseEntity<ApiError> invalidBody(MethodArgumentNotValidException e, HttpServletRequest r) { 
        Map<String, String> fields = new LinkedHashMap<>(); 
        e.getBindingResult().getFieldErrors() 
                .forEach(fe -> fields.putIfAbsent(fe.getField(), fe.getDefaultMessage())); 
        return build(HttpStatus.BAD_REQUEST, "Validation failed", r, fields); 
    } 

    @ExceptionHandler(HandlerMethodValidationException.class) 
    ResponseEntity<ApiError> invalidParams(HandlerMethodValidationException e, HttpServletRequest r) { 
        Map<String, String> fields = new LinkedHashMap<>(); 
        e.getParameterValidationResults().forEach(res -> res.getResolvableErrors().forEach(err -> 
                fields.putIfAbsent(res.getMethodParameter().getParameterName(), err.getDefaultMessage()))); 
        return build(HttpStatus.BAD_REQUEST, "Validation failed", r, fields); 
    } 

    @ExceptionHandler(ConstraintViolationException.class) 
    ResponseEntity<ApiError> constraint(ConstraintViolationException e, HttpServletRequest r) { 
        Map<String, String> fields = new LinkedHashMap<>(); 
        e.getConstraintViolations().forEach(v -> fields.putIfAbsent(v.getPropertyPath().toString(), v.getMessage())); 
        return build(HttpStatus.BAD_REQUEST, "Validation failed", r, fields); 
    } 

    // Wrong type in the URL, like ?year=abc or ?shape=BOAT. 
    @ExceptionHandler(MethodArgumentTypeMismatchException.class) 
    ResponseEntity<ApiError> typeMismatch(MethodArgumentTypeMismatchException e, HttpServletRequest r) { 
        return build(HttpStatus.BAD_REQUEST, 
                "Invalid value '" + e.getValue() + "' for parameter '" + e.getName() + "'", r, null); 
    } 

    @ExceptionHandler(MissingServletRequestParameterException.class) 
    ResponseEntity<ApiError> missingParam(MissingServletRequestParameterException e, HttpServletRequest r) { 
        return build(HttpStatus.BAD_REQUEST, e.getMessage(), r, null); 
    } 

    // Malformed or missing JSON body. 
    @ExceptionHandler(HttpMessageNotReadableException.class) 
    ResponseEntity<ApiError> unreadable(HttpMessageNotReadableException e, HttpServletRequest r) { 
        return build(HttpStatus.BAD_REQUEST, "Request body is missing or is not valid JSON", r, null); 
    } 

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class) 
    ResponseEntity<ApiError> method(HttpRequestMethodNotSupportedException e, HttpServletRequest r) { 
        return build(HttpStatus.METHOD_NOT_ALLOWED, e.getMessage(), r, null); 
    } 

    @ExceptionHandler(NoResourceFoundException.class) 
    ResponseEntity<ApiError> noRoute(NoResourceFoundException e, HttpServletRequest r) { 
        return build(HttpStatus.NOT_FOUND, "No endpoint " + r.getMethod() + " " + r.getRequestURI(), r, null); 
    } 

    // A UNIQUE / CHECK / FK constraint in schema.sql rejected the write, maps to 409. 
    @ExceptionHandler(DataIntegrityViolationException.class) 
    ResponseEntity<ApiError> integrity(DataIntegrityViolationException e, HttpServletRequest r) { 
        log.warn("Constraint violation on {}: {}", r.getRequestURI(), e.getMostSpecificCause().getMessage()); 
        return build(HttpStatus.CONFLICT, "The request conflicts with existing data", r, null); 
    } 

    // Last resort 
    @ExceptionHandler(Exception.class) 
    ResponseEntity<ApiError> unexpected(Exception e, HttpServletRequest r) { 
        log.error("Unhandled error on {} {}", r.getMethod(), r.getRequestURI(), e); 
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", r, null); 
    } 

    private ResponseEntity<ApiError> build(HttpStatus s, String msg, HttpServletRequest r, Map<String, String> fields) { 
        return ResponseEntity.status(s) 
                .body(new ApiError(Instant.now(), s.value(), s.getReasonPhrase(), msg, r.getRequestURI(), fields)); 
    } 
} 

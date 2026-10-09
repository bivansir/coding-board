package ru.coding_board.session_service.common.exception;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice 
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ServiceEntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
        ServiceEntityNotFoundException ex, HttpServletRequest request) {
        log.warn("Entity not found: {} at {}", ex.getMessage(), request.getRequestURI());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidSessionStatusException.class)
    public ResponseEntity<ErrorResponse> handleTransition(
        InvalidSessionStatusException ex, HttpServletRequest request) {
        log.warn("Invalid session status: {} at {}",
            ex.getMessage(), request.getRequestURI());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        HandlerMethodValidationException.class,
        MissingServletRequestParameterException.class,
        HttpMessageNotReadableException.class
    })
    public ResponseEntity<ErrorResponse> handleValidation(
        Exception ex, HttpServletRequest request
    ) {
        log.warn("Request validation failed: {} [{}]",
            ex.getMessage(), request.getRequestURI());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
        DataIntegrityViolationException ex, HttpServletRequest request
    ) {
        log.warn("Data integrity violated: {} at {}",
            ex.getMessage(), request.getRequestURI());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(
        Exception ex, HttpServletRequest request
    ) {
        log.error("Unexpected error", ex);

        ErrorResponse body = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
            "An unexpected error occurred", request.getRequestURI(), LocalDateTime.now()
        );
        return ResponseEntity.internalServerError().body(body);
    }

    private ResponseEntity<ErrorResponse> buildResponse(
        HttpStatus status, String message, HttpServletRequest request
    ) {
        ErrorResponse body = new ErrorResponse(
            status.value(), status.getReasonPhrase(),
            message, request.getRequestURI(), LocalDateTime.now());

        return ResponseEntity.status(status).body(body);
    }
}

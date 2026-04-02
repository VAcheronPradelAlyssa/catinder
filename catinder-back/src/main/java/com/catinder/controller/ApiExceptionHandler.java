package com.catinder.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Gestionnaire global des exceptions REST pour uniformiser les réponses d'erreur de l'API.
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
        MethodArgumentNotValidException ex,
        HttpServletRequest request
    ) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        ApiError apiError = new ApiError(
            Instant.now().toString(),
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            request.getRequestURI(),
            fieldErrors
        );
        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraint(
        ConstraintViolationException ex,
        HttpServletRequest request
    ) {
        ApiError apiError = new ApiError(
            Instant.now().toString(),
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            request.getRequestURI(),
            null
        );
        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(
        IllegalArgumentException ex,
        HttpServletRequest request
    ) {
        ApiError apiError = new ApiError(
            Instant.now().toString(),
            HttpStatus.CONFLICT.value(),
            ex.getMessage(),
            request.getRequestURI(),
            null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(
        BadCredentialsException ex,
        HttpServletRequest request
    ) {
        ApiError apiError = new ApiError(
            Instant.now().toString(),
            HttpStatus.UNAUTHORIZED.value(),
            ex.getMessage(),
            request.getRequestURI(),
            null
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }

    @ExceptionHandler({AuthorizationDeniedException.class})
    public ResponseEntity<ApiError> handleForbidden(
        RuntimeException ex,
        HttpServletRequest request
    ) {
        ApiError apiError = new ApiError(
            Instant.now().toString(),
            HttpStatus.FORBIDDEN.value(),
            "Accès refusé",
            request.getRequestURI(),
            null
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
        EntityNotFoundException ex,
        HttpServletRequest request
    ) {
        ApiError apiError = new ApiError(
            Instant.now().toString(),
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            request.getRequestURI(),
            null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(
        Exception ex,
        HttpServletRequest request
    ) {
        ApiError apiError = new ApiError(
            Instant.now().toString(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Erreur interne du serveur",
            request.getRequestURI(),
            null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }

    public record ApiError(
        String timestamp,
        int status,
        String message,
        String path,
        Map<String, String> details
    ) {}
}

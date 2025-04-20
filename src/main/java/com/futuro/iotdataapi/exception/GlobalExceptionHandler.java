package com.futuro.iotdataapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // Validaciones fallidas en DTOs (@Valid)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ExceptionResponse> handleValidationErrors(
      MethodArgumentNotValidException ex) {
    Map<String, String> fieldErrors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));

    ExceptionResponse response =
        new ExceptionResponse(
            new Date(),
            "Validation failed",
            fieldErrors.toString(),
            HttpStatus.BAD_REQUEST.value());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ExceptionResponse> handleUnauthorizedException(Exception ex) {
    ExceptionResponse response =
        new ExceptionResponse(
            new Date(), "Could not execute", ex.getMessage(), HttpStatus.UNAUTHORIZED.value());

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ExceptionResponse> handleNotFoundException(Exception ex) {
    ExceptionResponse response =
        new ExceptionResponse(
            new Date(), "Not found", ex.getMessage(), HttpStatus.NOT_FOUND.value());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  // Errores generales
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionResponse> handleGenericException(Exception ex) {
    // Log básico temporal
    ex.printStackTrace(); // modificar a Logger (SLF4J o similar)

    ExceptionResponse response =
        new ExceptionResponse(
            new Date(),
            "Unexpected server error",
            "An internal error occurred. Please contact support if the problem persists.",
            HttpStatus.INTERNAL_SERVER_ERROR.value());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }
}

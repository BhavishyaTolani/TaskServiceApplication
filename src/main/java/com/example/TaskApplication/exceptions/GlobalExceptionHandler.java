package com.example.TaskApplication.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskServiceException.class)
    public ResponseEntity<ErrorResponse> handleApprovalException(TaskServiceException ex) {
        ErrorResponse errorResponse = ErrorResponse.create(ex, HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse errorResponse = ErrorResponse.create(ex, HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
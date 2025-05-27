package com.gelatoflow.gelatoflow_api.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Map<String, Object>> handleApplicationException(ApplicationException e) {
        ErrorCode errorCode = e.getErrorCode();
        Map<String, Object> responseBody = new HashMap<>();

        responseBody.put("message", e.getParametrisedMessage());
        responseBody.put("httpStatus", errorCode.getHttpStatus());

        return new ResponseEntity<>(responseBody, errorCode.getHttpStatus());
    }
}
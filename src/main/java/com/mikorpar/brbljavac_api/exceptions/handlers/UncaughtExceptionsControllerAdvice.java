package com.mikorpar.brbljavac_api.exceptions.handlers;

import com.mikorpar.brbljavac_api.data.dtos.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.Date;

@ControllerAdvice(annotations = {RestController.class})
public class UncaughtExceptionsControllerAdvice {

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> handleBindingErrors(Exception ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse();

        error.setTimestamp(new Date(System.currentTimeMillis()));
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        error.setMessage(ex.getMessage());
        error.setPath(request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}

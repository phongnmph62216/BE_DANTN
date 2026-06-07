package com.example.be_dantn.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice


public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public final ResponseEntity<CustomErrorDetails> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        CustomErrorDetails errorDetails = new CustomErrorDetails(LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)

public  final ResponseEntity<CustomErrorDetails>      handleAllException(Exception ex, WebRequest request) {

    CustomErrorDetails errorDetails = new CustomErrorDetails(LocalDateTime.now(),
            ex.getMessage(),
            request.getDescription(false));

    return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
}

@ExceptionHandler(CustomResourceotFoundException.class)
    public final ResponseEntity<CustomErrorDetails> handleResourceNotFoundException(Exception ex, WebRequest request) {

           CustomErrorDetails errorDetails = new CustomErrorDetails(LocalDateTime.now(),
            ex.getMessage(),
            request.getDescription(false));
           return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);

    }


    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> {
                    errors.put(
                            error.getField(),
                            error.getDefaultMessage()
                    );
                });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(DuplicateDataException.class)
    public ResponseEntity<?> handleDuplicate(
            DuplicateDataException ex
    ) {

        Map<String, Object> response = new HashMap<>();

        response.put("success", false);
        response.put("message", ex.getMessage());

        return ResponseEntity.badRequest().body(response);
    }

}

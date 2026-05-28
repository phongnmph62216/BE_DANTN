package com.example.be_dantn.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CustomResourceotFoundException extends RuntimeException {

    public CustomResourceotFoundException(String message) {
        super(message);
    }




}

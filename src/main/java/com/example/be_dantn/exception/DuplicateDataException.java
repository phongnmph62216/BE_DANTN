package com.example.be_dantn.exception;

public class DuplicateDataException extends RuntimeException {

    public DuplicateDataException(String message) {
        super(message);
    }
}
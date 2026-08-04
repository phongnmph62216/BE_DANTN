package com.example.be_dantn.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseObject<T> {
    private HttpStatus status;
    private String message;
    private T data;
}

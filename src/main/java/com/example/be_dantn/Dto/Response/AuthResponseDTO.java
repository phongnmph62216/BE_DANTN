package com.example.be_dantn.Dto.Response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthResponseDTO {
    private Long id;
    private String ma;
    private String hoTen;
    private String sdt;
    private String email;
    private String role;
}

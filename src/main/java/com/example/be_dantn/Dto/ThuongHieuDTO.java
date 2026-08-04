package com.example.be_dantn.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ThuongHieuDTO {

    private Long id;

    private String ma;

    @NotBlank(message = "Ten thuong hieu khong duoc de trong")
    private String ten;

    private Integer trangThai;

    private LocalDateTime ngayTao;

    private LocalDateTime ngaySua;

    private String nguoiTao;

    private String nguoiSua;
}


package com.example.be_dantn.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LichLamViecDTO {

    private Long id;

    @NotNull(message = "Nhân viên không được để trống")
    private Long idNhanVien;

    @NotNull(message = "Ca làm việc không được để trống")
    private Long idCaLamViec;

    private String nguoiTaoQuanLy;

    @NotNull(message = "Ngày làm việc không được để trống")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayLamViec;

    private String ghiChu;

    private Integer trangThai;

    private LocalDateTime ngayTao;

    private LocalDateTime ngaySua;

    private String nguoiTao;

    private String nguoiSua;
}

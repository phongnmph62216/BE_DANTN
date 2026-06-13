package com.example.be_dantn.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LichSuHoaDonResponseDTO {

    private Long id;

    private Long idHoaDon;

    private String maHoaDon;

    private Integer trangThai;

    private String hanhDong;

    private String ghiChu;

    private String nguoiThucHien;

    private LocalDateTime thoiGian;
}
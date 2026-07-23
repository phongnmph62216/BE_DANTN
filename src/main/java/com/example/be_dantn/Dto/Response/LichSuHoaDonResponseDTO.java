package com.example.be_dantn.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LichSuHoaDonResponseDTO {
    private String nguoiThucHien; // Tên nhân viên
    private String hanhDong;
    private Integer trangThai;
    private LocalDateTime thoiGian;
    private String ghiChu;
}

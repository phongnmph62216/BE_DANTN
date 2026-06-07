package com.example.be_dantn.Dto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LichSuHoaDonResponse {

    private Long id;

    private Long idHoaDon;

    private String trangThai;

    private String trangThaiText;

    private LocalDateTime thoiGian;

    private String ghiChu;

    private String hanhDong;
}
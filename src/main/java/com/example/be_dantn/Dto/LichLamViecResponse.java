package com.example.be_dantn.Dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LichLamViecResponse {
    private Long id;

    private Long nhanVienId;

    private String tenNhanVien;

    private Long caLamViecId;

    private String tenCaLamViec;

    private Long nguoiTaoQuanLyId;

    private String tenQuanLy;

    private LocalDate ngayLamViec;

    private String ghiChu;

    private Integer trangThai;
}

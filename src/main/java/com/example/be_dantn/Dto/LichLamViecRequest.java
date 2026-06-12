package com.example.be_dantn.Dto;
import java.time.LocalDate;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LichLamViecRequest {
    private Long id;

    private Long nhanVienId;

    private Long caLamViecId;

    private Long nguoiTaoQuanLyId;

    private LocalDate ngayLamViec;

    private String ghiChu;

    private Integer trangThai;
}

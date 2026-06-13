package com.example.be_dantn.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaChiResponseDTO {
    private Long id;
    private String tenNguoiNhan;
    private String sdtNguoiNhan;
    private String diaChiCuThe;
    private String tinhThanhPho;
    private String quanHuyen;
    private String phuongXa;
    private Boolean kieuDiaChiLaMacDinh;
}

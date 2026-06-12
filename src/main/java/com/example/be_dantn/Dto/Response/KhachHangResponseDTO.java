package com.example.be_dantn.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhachHangResponseDTO {
    private Long id;
    private String maKhachHang;
    private String hoTen;
    private String sdt;
    private String email;
    private Integer gioiTinh;
    private Integer trangThai;
    private String diaChiMacDinh;
}

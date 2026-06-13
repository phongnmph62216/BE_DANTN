package com.example.be_dantn.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NhanVienResponseDTO {
    private Long id;
    private String anh;
    private String maNhanVien;
    private String hoVaTen;
    private String email;
    private String soDienThoai;
    private String diaChi;
    private Integer trangThai;
    private String tenVaiTro;
}

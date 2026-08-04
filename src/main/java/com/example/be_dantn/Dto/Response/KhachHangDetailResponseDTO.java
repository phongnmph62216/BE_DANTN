package com.example.be_dantn.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhachHangDetailResponseDTO {
    private Long id;
    private String maKhachHang;
    private String hoTen;
    private String sdt;
    private String email;
    private Integer gioiTinh;
    private LocalDate ngaySinh;
    private Integer trangThai;
    private List<DiaChiResponseDTO> danhSachDiaChi;
}

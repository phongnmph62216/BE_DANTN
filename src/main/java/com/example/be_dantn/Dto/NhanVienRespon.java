package com.example.be_dantn.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NhanVienRespon {

    private Long id;

    private String maNhanVien;

    private String hoVaTen;

    private String soDienThoai;

    private String email;

    private String matKhau;

    private String cccd;

    private Integer gioiTinh;

    private LocalDate ngaySinh;

    private String diaChi;

    private String vaiTro;

    private Integer trangThai;

    private LocalDate ngayVaoLam;

    private String anhDaiDien;

    private LocalDateTime ngayTao;

    private LocalDateTime ngaySua;

}

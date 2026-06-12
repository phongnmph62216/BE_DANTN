package com.example.be_dantn.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "nhan_vien")
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    private String nguoiTao;

    private String nguoiSua;

    @OneToMany(mappedBy = "nhanVien",cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    private Set<LichLamViec> lichLamViec = new HashSet<>();

    @OneToMany(mappedBy = "nguoiTaoQuanLy",cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    private Set<LichLamViec> lichLamViecQuanLy = new HashSet<>();

  
}

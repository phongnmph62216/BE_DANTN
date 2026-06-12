package com.example.be_dantn.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lich_lam_viec")
public class LichLamViec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private NhanVien nhanVien;


    @ManyToOne
    private CaLamViec caLamViec;


    @ManyToOne
    private NhanVien nguoiTaoQuanLy;

    private LocalDate ngayLamViec;

    private String ghiChu;

    private Integer trangThai;


}

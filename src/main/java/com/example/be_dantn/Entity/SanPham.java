package com.example.be_dantn.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "san_pham")
public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String maSanPham;
    private String tenSanPham;

    @Column(name = "mo_ta", columnDefinition = "NVARCHAR(MAX)")
    private String moTa;

    private String hinhAnh;
    private Integer trangThai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_thuong_hieu")
    private ThuongHieu thuongHieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chat_lieu")
    private ChatLieu chatLieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_xuat_su")
    private XuatSu xuatSu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kieu_dang")
    private KieuDang kieuDang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_loai_san_pham")
    private LoaiSanPham loaiSanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_co_ao")
    private CoAo coAo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tay_ao")
    private TayAo tayAo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vai_ao")
    private VaiAo vaiAo;

    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;
    private String nguoiTao;
    private String nguoiSua;

    @PrePersist
    public void prePersist() {
        ngayTao = LocalDateTime.now();
        ngaySua = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        ngaySua = LocalDateTime.now();
    }
}

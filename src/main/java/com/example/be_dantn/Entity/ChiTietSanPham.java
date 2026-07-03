package com.example.be_dantn.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "chi_tiet_san_pham")
public class ChiTietSanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String maChiTietSanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham")
    private SanPham sanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mau_sac")
    private MauSac mauSac;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kich_thuoc")
    private KichThuoc kichThuoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dot_giam_gia")
    private DotGiamGia dotGiamGia; // Bổ sung

    private Integer soLuongTon;
    private BigDecimal giaNhap;
    private BigDecimal giaBan;
    private String anh;
    private Integer trangThai;

    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;

    @PrePersist
    public void prePersist() {
        ngayTao = LocalDateTime.now();
        ngaySua = LocalDateTime.now();
        if (this.trangThai == null) {
            this.trangThai = 1; // Mặc định là đang bán
        }
        if (this.maChiTietSanPham == null || this.maChiTietSanPham.isEmpty()) {
            try {
                com.example.be_dantn.Config.CodeGenerator generator = com.example.be_dantn.Config.SpringContextUtils.getBean(com.example.be_dantn.Config.CodeGenerator.class);
                this.maChiTietSanPham = generator.generateCode("chi_tiet_san_pham", "ma_chi_tiet_san_pham", "CTSP");
            } catch (Exception e) {
                this.maChiTietSanPham = "CTSP" + System.currentTimeMillis();
            }
        }
    }

    @PreUpdate
    public void preUpdate() {
        ngaySua = LocalDateTime.now();
    }
}

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
@Table(name = "phieu_giam_gia")
public class PhieuGiamGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String maPhieuGiamGia;
    private String tenPhieuGiamGia;
    private Integer loaiGiam; // 0: Giảm theo %, 1: Giảm theo số tiền
    private BigDecimal giaTri; // Giá trị giảm (hoặc % hoặc số tiền)
    private BigDecimal giaGiamToiDa; // Giá trị giảm tối đa (nếu loaiGiam là %)
    private BigDecimal dieuKienGiam; // Điều kiện hóa đơn tối thiểu
    private Integer soLuong;
    private Integer kieuApDung; // 0: Toàn cửa hàng, 1: Cá nhân
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private Integer trangThai; // 0: Sắp diễn ra, 1: Đang diễn ra, 2: Đã kết thúc

    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;

    @PrePersist
    public void prePersist() {
        ngayTao = LocalDateTime.now();
        ngaySua = LocalDateTime.now();
        if (this.maPhieuGiamGia == null || this.maPhieuGiamGia.isEmpty()) {
            this.maPhieuGiamGia = "PGG" + System.currentTimeMillis();
        }
    }

    @PreUpdate
    public void preUpdate() {
        ngaySua = LocalDateTime.now();
    }
}

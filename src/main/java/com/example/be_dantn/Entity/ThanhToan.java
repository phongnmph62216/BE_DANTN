package com.example.be_dantn.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "thanh_toan")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don")
    private HoaDon hoaDon;

    @Column(name = "phuong_thuc")
    private String phuongThuc;

    @Column(name = "so_tien")
    private BigDecimal soTien;

    @Column(name = "thoi_gian")
    private LocalDateTime thoiGian;

    @Column(name = "nguoi_thuc_hien")
    private String nguoiThucHien;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @PrePersist
    protected void onCreate() {
        this.thoiGian = LocalDateTime.now();
    }
}

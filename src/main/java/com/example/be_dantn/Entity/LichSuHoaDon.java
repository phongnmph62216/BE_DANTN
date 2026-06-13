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
@Table(name = "lich_su_hoa_don")
public class LichSuHoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don")
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien")
    private NhanVien nhanVien;

    private Integer trangThai;

    private String hanhDong;

    @Column(columnDefinition = "nvarchar(1000)")
    private String ghiChu;

    private String nguoiThucHien;

    private LocalDateTime thoiGian;

    private LocalDateTime ngayTao;

    @PrePersist
    public void prePersist() {
        if (thoiGian == null) {
            thoiGian = LocalDateTime.now();
        }

        if (ngayTao == null) {
            ngayTao = LocalDateTime.now();
        }
    }
}
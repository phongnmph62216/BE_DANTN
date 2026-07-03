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
@Table(name = "giao_ca")
public class GiaoCa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lich_lam_viec")
    private LichLamViec lichLamViec;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_dong_ca")
    private NhanVien nguoiDongCa;

    private LocalDateTime thoiGianMoCa;

    private LocalDateTime thoiGianDongCa;

    private BigDecimal tienMatDauCa;

    private BigDecimal tienMatThuTrongCa;

    private BigDecimal tienChuyenKhoanTrongCa;

    private BigDecimal tienMatThucTeChotCa;

    private BigDecimal tienChenhLech;

    private Integer trangThai; // 0: Đang hoạt động, 1: Đã đóng

    private BigDecimal tienGiaoCaSau;

    private Integer trangThaiDoiSoat; // null/0: Chờ đối soát, 1: Đã đối soát, 2: Có sai lệch

    private String phuongAnXuLy; // TRU_LUONG, CHI_PHI_CUA_HANG, KHONG_XU_LY

    private String ghiChuDoiSoat;

    private String ghiChu;
}

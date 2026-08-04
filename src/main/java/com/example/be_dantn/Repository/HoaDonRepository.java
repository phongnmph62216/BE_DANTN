package com.example.be_dantn.repository;

import com.example.be_dantn.Dto.HoaDonResponseDTO;
import com.example.be_dantn.Entity.HoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Long> {

    Optional<HoaDon> findByMaHoaDon(String maHoaDon);

    @Query("SELECT h FROM HoaDon h LEFT JOIN h.khachHang kh WHERE LOWER(TRIM(h.maHoaDon)) = LOWER(TRIM(:maHoaDon)) AND (LOWER(TRIM(h.email)) = LOWER(TRIM(:email)) OR (kh IS NOT NULL AND LOWER(TRIM(kh.email)) = LOWER(TRIM(:email))))")
    Optional<HoaDon> findByMaHoaDonAndEmail(@Param("maHoaDon") String maHoaDon, @Param("email") String email);

    @Query("""
            SELECT new com.example.be_dantn.Dto.HoaDonResponseDTO(
                h.id,
                h.maHoaDon,
                nv.hoVaTen,
                h.tenKhachHang,
                h.ngayTao,
                h.tongTienThanhToan,
                h.loaiHoaDon,
                h.soDienThoai,
                h.trangThai
            )
            FROM HoaDon h
            LEFT JOIN h.nhanVien nv
            WHERE
                (:maHoaDon IS NULL OR h.maHoaDon LIKE %:maHoaDon% OR h.tenKhachHang LIKE %:maHoaDon% OR h.soDienThoai LIKE %:maHoaDon%) AND
                (:tuNgay IS NULL OR h.ngayTao >= :tuNgay) AND
                (:denNgay IS NULL OR h.ngayTao <= :denNgay) AND
                (:loaiDon IS NULL OR h.loaiHoaDon = :loaiDon) AND
                (:trangThai IS NULL OR h.trangThai IN :trangThai)
            ORDER BY h.ngayTao DESC
            """)
    Page<HoaDonResponseDTO> findHoaDonByFilters(
            @Param("maHoaDon") String maHoaDon,
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgay") LocalDateTime denNgay,
            @Param("loaiDon") Integer loaiDon,
            @Param("trangThai") List<Integer> trangThai,
            Pageable pageable
    );

    @Query("""
            SELECT new com.example.be_dantn.Dto.HoaDonResponseDTO(
                h.id,
                h.maHoaDon,
                nv.hoVaTen,
                h.tenKhachHang,
                h.ngayTao,
                h.tongTienThanhToan,
                h.loaiHoaDon,
                h.soDienThoai,
                h.trangThai
            )
            FROM HoaDon h
            LEFT JOIN h.nhanVien nv
            WHERE
                (:maHoaDon IS NULL OR h.maHoaDon LIKE %:maHoaDon% OR h.tenKhachHang LIKE %:maHoaDon% OR h.soDienThoai LIKE %:maHoaDon%) AND
                (:tuNgay IS NULL OR h.ngayTao >= :tuNgay) AND
                (:denNgay IS NULL OR h.ngayTao <= :denNgay) AND
                (:loaiDon IS NULL OR h.loaiHoaDon = :loaiDon) AND
                (:trangThai IS NULL OR h.trangThai IN :trangThai)
            ORDER BY h.ngayTao DESC
            """)
    List<HoaDonResponseDTO> findHoaDonByFiltersForExport(
            @Param("maHoaDon") String maHoaDon,
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgay") LocalDateTime denNgay,
            @Param("loaiDon") Integer loaiDon,
            @Param("trangThai") List<Integer> trangThai
    );

    @Query("SELECT COUNT(h) FROM HoaDon h WHERE h.phieuGiamGia.id = :phieuGiamGiaId AND h.trangThai <> 5")
    int countUsedVouchers(@Param("phieuGiamGiaId") Long phieuGiamGiaId);

    List<HoaDon> findByTrangThaiOrderByNgayTaoDesc(Integer trangThai);

    @Query("SELECT h FROM HoaDon h WHERE h.trangThai = :trangThai AND h.loaiHoaDon IN :loaiHoaDons ORDER BY h.ngayTao DESC")
    List<HoaDon> findByTrangThaiAndLoaiHoaDonIn(@Param("trangThai") Integer trangThai, @Param("loaiHoaDons") List<Integer> loaiHoaDons);

    List<HoaDon> findByKhachHangIdOrderByNgayTaoDesc(Long khachHangId);

    @Query("SELECT h FROM HoaDon h WHERE h.loaiHoaDon = 2 AND h.trangThai = 0 AND h.ngayTao <= :threshold AND h.ghiChu LIKE '[VNPAY_PENDING]%'")
    List<HoaDon> findExpiredVnpayOrders(@Param("threshold") LocalDateTime threshold);
}

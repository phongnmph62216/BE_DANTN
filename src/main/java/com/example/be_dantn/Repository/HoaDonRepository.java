package com.example.be_dantn.Repository;

import com.example.be_dantn.Dto.Response.HoaDonResponseDTO;
import com.example.be_dantn.Entity.HoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface HoaDonRepository extends JpaRepository<HoaDon, Long> {

    @Query("""

            SELECT new com.example.be_dantn.Dto.Response.HoaDonResponseDTO(
           hd.id,
           hd.maHoaDon,
           hd.loaiHoaDon,
           COALESCE(kh.hoTen, 'Khách lẻ'),
           COALESCE(kh.sdt, '-'),
           COALESCE(kh.email, '-'),
           COALESCE(nv.hoVaTen, '-'),
           COALESCE(nv.maNhanVien, '-'),
           COALESCE(pgg.maPhieuGiamGia, '-'),
           COALESCE(pgg.tenPhieuGiamGia, '-'),
           hd.soTienGoc,
           hd.soTienGiam,
           hd.phiVanChuyen,
           hd.tongTienThanhToan,
           hd.ngayTao,
           hd.ngayThanhToan,
           hd.ngayNhanHang,
           hd.trangThai,
           hd.ghiChu,
           hd.diaChi
       )
            FROM HoaDon hd
            LEFT JOIN hd.khachHang kh
            LEFT JOIN hd.nhanVien nv
            LEFT JOIN hd.phieuGiamGia pgg
            WHERE
                (:keyword IS NULL OR hd.maHoaDon LIKE %:keyword% OR kh.hoTen LIKE %:keyword% OR kh.sdt LIKE %:keyword%)
            AND (:loaiHoaDon IS NULL OR hd.loaiHoaDon = :loaiHoaDon)
            AND (:trangThai IS NULL OR hd.trangThai = :trangThai)
            AND (CAST(:tuNgay AS timestamp) IS NULL OR hd.ngayTao >= :tuNgay)
            AND (CAST(:denNgay AS timestamp) IS NULL OR hd.ngayTao <= :denNgay)
            ORDER BY hd.ngayTao DESC
            """)
    Page<HoaDonResponseDTO> findByFilters(
            @Param("keyword") String keyword,
            @Param("loaiHoaDon") Integer loaiHoaDon,
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgay") LocalDateTime denNgay,
            @Param("trangThai") Integer trangThai,
            Pageable pageable
    );

    @Query("""
            SELECT new com.example.be_dantn.Dto.Response.HoaDonResponseDTO(
                 hd.id,
                 hd.maHoaDon,
                 hd.loaiHoaDon,
                 COALESCE(kh.hoTen, 'Khách lẻ'),
                 COALESCE(kh.sdt, '-'),
                 COALESCE(kh.email, '-'),
                 COALESCE(nv.hoVaTen, '-'),
                 COALESCE(nv.maNhanVien, '-'),
                 COALESCE(pgg.maPhieuGiamGia, '-'),
                 COALESCE(pgg.tenPhieuGiamGia, '-'),
                 hd.soTienGoc,
                 hd.soTienGiam,
                 hd.phiVanChuyen,
                 hd.tongTienThanhToan,
                 hd.ngayTao,
                 hd.ngayThanhToan,
                 hd.ngayNhanHang,
                 hd.trangThai,
                 hd.ghiChu,
                 hd.diaChi
             )
            FROM HoaDon hd
            LEFT JOIN hd.khachHang kh
            LEFT JOIN hd.nhanVien nv
            LEFT JOIN hd.phieuGiamGia pgg
            WHERE hd.id = :id
            """)
    Optional<HoaDonResponseDTO> findResponseById(@Param("id") Long id);
}
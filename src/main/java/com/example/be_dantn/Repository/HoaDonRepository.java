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

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Long> {

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
                (:trangThai IS NULL OR h.trangThai = :trangThai)
            ORDER BY h.ngayTao DESC
            """)
    Page<HoaDonResponseDTO> findHoaDonByFilters(
            @Param("maHoaDon") String maHoaDon,
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgay") LocalDateTime denNgay,
            @Param("loaiDon") Integer loaiDon,
            @Param("trangThai") Integer trangThai,
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
                (:trangThai IS NULL OR h.trangThai = :trangThai)
            ORDER BY h.ngayTao DESC
            """)
    List<HoaDonResponseDTO> findHoaDonByFiltersForExport(
            @Param("maHoaDon") String maHoaDon,
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgay") LocalDateTime denNgay,
            @Param("loaiDon") Integer loaiDon,
            @Param("trangThai") Integer trangThai
    );
}

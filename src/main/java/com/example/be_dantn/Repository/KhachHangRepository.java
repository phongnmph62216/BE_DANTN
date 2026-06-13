package com.example.be_dantn.Repository;

import com.example.be_dantn.Dto.Response.KhachHangResponseDTO;
import com.example.be_dantn.Entity.KhachHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Long> {

    @Query("""
            SELECT new com.example.be_dantn.Dto.Response.KhachHangResponseDTO(
                kh.id,
                kh.maKhachHang,
                kh.hoTen,
                kh.sdt,
                kh.email,
                kh.gioiTinh,
                kh.trangThai,
                CONCAT(dc.diaChiCuThe, ', ', dc.phuongXa, ', ', dc.quanHuyen, ', ', dc.tinhThanhPho),
                kh.ngaySinh
            )
            FROM KhachHang kh
            LEFT JOIN kh.danhSachDiaChi dc ON dc.kieuDiaChiLaMacDinh = true
            WHERE
                (:keyword IS NULL OR kh.hoTen LIKE %:keyword% OR kh.sdt LIKE %:keyword% OR kh.email LIKE %:keyword%)
            AND (:gioiTinh IS NULL OR kh.gioiTinh = :gioiTinh)
            AND (:trangThai IS NULL OR kh.trangThai = :trangThai)
            """)
    Page<KhachHangResponseDTO> findByFilters(
            @Param("keyword") String keyword,
            @Param("gioiTinh") Integer gioiTinh,
            @Param("trangThai") Integer trangThai,
            Pageable pageable
    );

    @Query("""
            SELECT new com.example.be_dantn.Dto.Response.KhachHangResponseDTO(
                kh.id,
                kh.maKhachHang,
                kh.hoTen,
                kh.sdt,
                kh.email,
                kh.gioiTinh,
                kh.trangThai,
                CONCAT(dc.diaChiCuThe, ', ', dc.phuongXa, ', ', dc.quanHuyen, ', ', dc.tinhThanhPho),
                kh.ngaySinh
            )
            FROM KhachHang kh
            LEFT JOIN kh.danhSachDiaChi dc ON dc.kieuDiaChiLaMacDinh = true
            """)
    List<KhachHangResponseDTO> findAllForExcel();
}

package com.example.be_dantn.Repository;

import com.example.be_dantn.Dto.Response.HoaDonChiTietResponseDTO;
import com.example.be_dantn.Entity.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, Long> {

    @Query("""
            SELECT new com.example.be_dantn.Dto.Response.HoaDonChiTietResponseDTO(
                hdct.id,
                hd.id,
                ctsp.id,
                ctsp.maChiTietSanPham,
                sp.maSanPham,
                sp.tenSanPham,
                COALESCE(ms.tenMauSac, '-'),
                COALESCE(kt.tenKichThuoc, '-'),
                ctsp.anh,
                hdct.donGia,
                hdct.soLuong,
                hdct.thanhTien,
                hdct.trangThai
            )
            FROM HoaDonChiTiet hdct
            JOIN hdct.hoaDon hd
            JOIN hdct.chiTietSanPham ctsp
            JOIN ctsp.sanPham sp
            LEFT JOIN ctsp.mauSac ms
            LEFT JOIN ctsp.kichThuoc kt
            WHERE hd.id = :hoaDonId
            ORDER BY hdct.id ASC
            """)
    List<HoaDonChiTietResponseDTO> findByHoaDonId(@Param("hoaDonId") Long hoaDonId);
}
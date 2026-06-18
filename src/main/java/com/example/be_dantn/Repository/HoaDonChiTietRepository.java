package com.example.be_dantn.repository;

import com.example.be_dantn.Dto.Response.HoaDonChiTietDTO;
import com.example.be_dantn.Entity.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, Long> {

    @Query("""
            SELECT new com.example.be_dantn.Dto.Response.HoaDonChiTietDTO(
                ctsp.id,
                sp.tenSanPham,
                sp.hinhAnh,
                kt.tenKichThuoc,
                ms.tenMauSac,
                hct.soLuong,
                hct.donGia,
                (hct.soLuong * hct.donGia)
            )
            FROM HoaDonChiTiet hct
            JOIN hct.chiTietSanPham ctsp
            JOIN ctsp.sanPham sp
            JOIN ctsp.kichThuoc kt
            JOIN ctsp.mauSac ms
            WHERE hct.hoaDon.id = :idHoaDon
            """)
    List<HoaDonChiTietDTO> findByIdHoaDon(@Param("idHoaDon") Long idHoaDon);
}

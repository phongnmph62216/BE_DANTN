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
                ctsp.maChiTietSanPham,
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

    @Query(value = """
            SELECT sp.id, sp.ten_san_pham, SUM(hdct.so_luong) AS totalSold
            FROM hoa_don_chi_tiet hdct
            JOIN hoa_don hd ON hdct.id_hoa_don = hd.id
            JOIN chi_tiet_san_pham ctsp ON hdct.id_chi_tiet_san_pham = ctsp.id
            JOIN san_pham sp ON ctsp.id_san_pham = sp.id
            WHERE hd.trang_thai = 4
            GROUP BY sp.id, sp.ten_san_pham
            ORDER BY SUM(hdct.so_luong) DESC
            LIMIT 10
            """, nativeQuery = true)
    List<Object[]> findTopSellingProducts();
}

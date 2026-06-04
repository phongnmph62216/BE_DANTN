package com.example.be_dantn.Repository;

import com.example.be_dantn.Dto.Response.ChiTietSanPhamResponseDTO;
import com.example.be_dantn.Entity.ChiTietSanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChiTietSanPhamRepository extends JpaRepository<ChiTietSanPham, Long> {

    @Query("""
            SELECT new com.example.be_dantn.Dto.Response.ChiTietSanPhamResponseDTO(
                ctsp.id,
                ctsp.anh,
                sp.maSanPham,
                ctsp.maChiTietSanPham,
                kt.tenKichThuoc,
                ms.tenMauSac,
                ctsp.soLuongTon,
                ctsp.giaNhap,
                ctsp.giaBan,
                ctsp.trangThai
            )
            FROM ChiTietSanPham ctsp
            JOIN ctsp.sanPham sp
            JOIN ctsp.mauSac ms
            JOIN ctsp.kichThuoc kt
            WHERE
                (:keyword IS NULL OR ctsp.maChiTietSanPham LIKE %:keyword% OR sp.maSanPham LIKE %:keyword%)
            AND (:idMauSac IS NULL OR ms.id = :idMauSac)
            AND (:idKichThuoc IS NULL OR kt.id = :idKichThuoc)
            AND (:trangThai IS NULL OR ctsp.trangThai = :trangThai)
            AND (:minPrice IS NULL OR ctsp.giaBan >= :minPrice)
            AND (:maxPrice IS NULL OR ctsp.giaBan <= :maxPrice)
            """)
    Page<ChiTietSanPhamResponseDTO> findByFilters(
            @Param("keyword") String keyword,
            @Param("idMauSac") Long idMauSac,
            @Param("idKichThuoc") Long idKichThuoc,
            @Param("trangThai") Integer trangThai,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    // Query tương tự nhưng không phân trang, dùng cho việc xuất Excel
    @Query("""
            SELECT new com.example.be_dantn.Dto.Response.ChiTietSanPhamResponseDTO(
                ctsp.id,
                ctsp.anh,
                sp.maSanPham,
                ctsp.maChiTietSanPham,
                kt.tenKichThuoc,
                ms.tenMauSac,
                ctsp.soLuongTon,
                ctsp.giaNhap,
                ctsp.giaBan,
                ctsp.trangThai
            )
            FROM ChiTietSanPham ctsp
            JOIN ctsp.sanPham sp
            JOIN ctsp.mauSac ms
            JOIN ctsp.kichThuoc kt
            WHERE
                (:keyword IS NULL OR ctsp.maChiTietSanPham LIKE %:keyword% OR sp.maSanPham LIKE %:keyword%)
            AND (:idMauSac IS NULL OR ms.id = :idMauSac)
            AND (:idKichThuoc IS NULL OR kt.id = :idKichThuoc)
            AND (:trangThai IS NULL OR ctsp.trangThai = :trangThai)
            AND (:minPrice IS NULL OR ctsp.giaBan >= :minPrice)
            AND (:maxPrice IS NULL OR ctsp.giaBan <= :maxPrice)
            """)
    List<ChiTietSanPhamResponseDTO> findByFiltersForExcel(
            @Param("keyword") String keyword,
            @Param("idMauSac") Long idMauSac,
            @Param("idKichThuoc") Long idKichThuoc,
            @Param("trangThai") Integer trangThai,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    Optional<ChiTietSanPham> findByMaChiTietSanPham(String maChiTietSanPham);
}

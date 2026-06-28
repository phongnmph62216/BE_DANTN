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
                ctsp.trangThai,
                dgg.phanTramGiam,
                dgg.ngayBatDau,
                dgg.ngayKetThuc,
                dgg.trangThai
            )
            FROM ChiTietSanPham ctsp
            JOIN ctsp.sanPham sp
            JOIN ctsp.mauSac ms
            JOIN ctsp.kichThuoc kt
            LEFT JOIN ctsp.dotGiamGia dgg
            WHERE
                (:keyword IS NULL OR ctsp.maChiTietSanPham LIKE %:keyword% OR sp.maSanPham LIKE %:keyword%)
            AND (:idMauSac IS NULL OR ms.id = :idMauSac)
            AND (:idKichThuoc IS NULL OR kt.id = :idKichThuoc)
            AND (:trangThai IS NULL OR ctsp.trangThai = :trangThai)
            AND (:minPrice IS NULL OR ctsp.giaBan >= :minPrice)
            AND (:maxPrice IS NULL OR ctsp.giaBan <= :maxPrice)
            AND (:idSanPham IS NULL OR sp.id = :idSanPham)
            """)
    Page<ChiTietSanPhamResponseDTO> findByFilters(
            @Param("keyword") String keyword,
            @Param("idMauSac") Long idMauSac,
            @Param("idKichThuoc") Long idKichThuoc,
            @Param("trangThai") Integer trangThai,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("idSanPham") Long idSanPham,
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
                ctsp.trangThai,
                dgg.phanTramGiam,
                dgg.ngayBatDau,
                dgg.ngayKetThuc,
                dgg.trangThai
            )
            FROM ChiTietSanPham ctsp
            JOIN ctsp.sanPham sp
            JOIN ctsp.mauSac ms
            JOIN ctsp.kichThuoc kt
            LEFT JOIN ctsp.dotGiamGia dgg
            WHERE
                (:keyword IS NULL OR ctsp.maChiTietSanPham LIKE %:keyword% OR sp.maSanPham LIKE %:keyword%)
            AND (:idMauSac IS NULL OR ms.id = :idMauSac)
            AND (:idKichThuoc IS NULL OR kt.id = :idKichThuoc)
            AND (:trangThai IS NULL OR ctsp.trangThai = :trangThai)
            AND (:minPrice IS NULL OR ctsp.giaBan >= :minPrice)
            AND (:maxPrice IS NULL OR ctsp.giaBan <= :maxPrice)
            AND (:idSanPham IS NULL OR sp.id = :idSanPham)
            """)
    List<ChiTietSanPhamResponseDTO> findByFiltersForExcel(
            @Param("keyword") String keyword,
            @Param("idMauSac") Long idMauSac,
            @Param("idKichThuoc") Long idKichThuoc,
            @Param("trangThai") Integer trangThai,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("idSanPham") Long idSanPham
    );

    Optional<ChiTietSanPham> findByMaChiTietSanPham(String maChiTietSanPham);

    // New methods for discount campaign
    List<ChiTietSanPham> findByDotGiamGia_Id(Long idDotGiamGia);
    List<ChiTietSanPham> findAllByIdIn(List<Long> ids);

    boolean existsBySanPham_IdAndMauSac_IdAndKichThuoc_Id(Long idSanPham, Long idMauSac, Long idKichThuoc);
}

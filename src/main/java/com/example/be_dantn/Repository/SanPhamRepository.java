package com.example.be_dantn.Repository;

import com.example.be_dantn.Dto.Response.SanPhamResponse;
import com.example.be_dantn.Entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Long> {

    @Query("""
            SELECT new com.example.be_dantn.Dto.Response.SanPhamResponse(
                sp.id,
                sp.maSanPham,
                sp.tenSanPham,
                sp.hinhAnh,
                sp.trangThai,
                th.tenThuongHieu,
                cl.tenChatLieu,
                SUM(ctsp.soLuongTon),
                MIN(ctsp.giaBan),
                MAX(ctsp.giaBan),
                MAX(CASE WHEN dgg.trangThai = 1 AND CURRENT_TIMESTAMP BETWEEN dgg.ngayBatDau AND dgg.ngayKetThuc THEN dgg.phanTramGiam ELSE 0 END),
                MIN(CASE WHEN dgg.trangThai = 1 AND CURRENT_TIMESTAMP BETWEEN dgg.ngayBatDau AND dgg.ngayKetThuc THEN ctsp.giaBan * (100 - dgg.phanTramGiam) / 100 ELSE ctsp.giaBan END),
                MAX(CASE WHEN dgg.trangThai = 1 AND CURRENT_TIMESTAMP BETWEEN dgg.ngayBatDau AND dgg.ngayKetThuc THEN ctsp.giaBan * (100 - dgg.phanTramGiam) / 100 ELSE ctsp.giaBan END)
            )
            FROM SanPham sp
            LEFT JOIN sp.thuongHieu th
            LEFT JOIN sp.chatLieu cl
            LEFT JOIN ChiTietSanPham ctsp ON sp.id = ctsp.sanPham.id
            LEFT JOIN ctsp.dotGiamGia dgg
            WHERE
                (:keyword IS NULL OR sp.maSanPham LIKE %:keyword% OR sp.tenSanPham LIKE %:keyword%)
            AND (:idThuongHieu IS NULL OR sp.thuongHieu.id = :idThuongHieu)
            AND (:idChatLieu IS NULL OR sp.chatLieu.id = :idChatLieu)
            AND (:trangThai IS NULL OR sp.trangThai = :trangThai)
            GROUP BY
                sp.id, sp.maSanPham, sp.tenSanPham, sp.hinhAnh, sp.trangThai, th.tenThuongHieu, cl.tenChatLieu
            """)
    Page<SanPhamResponse> findSanPhamByFilters(
            @Param("keyword") String keyword,
            @Param("idThuongHieu") Long idThuongHieu,
            @Param("idChatLieu") Long idChatLieu,
            @Param("trangThai") Integer trangThai,
            Pageable pageable
    );

    boolean existsByMaSanPham(String maSanPham);

    // Query tương tự nhưng không phân trang, dùng cho việc xuất Excel
    @Query("""
            SELECT new com.example.be_dantn.Dto.Response.SanPhamResponse(
                sp.id, sp.maSanPham, sp.tenSanPham, sp.hinhAnh, sp.trangThai,
                th.tenThuongHieu, cl.tenChatLieu, SUM(ctsp.soLuongTon),
                MIN(ctsp.giaBan), MAX(ctsp.giaBan),
                MAX(CASE WHEN dgg.trangThai = 1 AND CURRENT_TIMESTAMP BETWEEN dgg.ngayBatDau AND dgg.ngayKetThuc THEN dgg.phanTramGiam ELSE 0 END),
                MIN(CASE WHEN dgg.trangThai = 1 AND CURRENT_TIMESTAMP BETWEEN dgg.ngayBatDau AND dgg.ngayKetThuc THEN ctsp.giaBan * (100 - dgg.phanTramGiam) / 100 ELSE ctsp.giaBan END),
                MAX(CASE WHEN dgg.trangThai = 1 AND CURRENT_TIMESTAMP BETWEEN dgg.ngayBatDau AND dgg.ngayKetThuc THEN ctsp.giaBan * (100 - dgg.phanTramGiam) / 100 ELSE ctsp.giaBan END)
            )
            FROM SanPham sp
            LEFT JOIN sp.thuongHieu th
            LEFT JOIN sp.chatLieu cl
            LEFT JOIN ChiTietSanPham ctsp ON sp.id = ctsp.sanPham.id
            LEFT JOIN ctsp.dotGiamGia dgg
            WHERE
                (:keyword IS NULL OR sp.maSanPham LIKE %:keyword% OR sp.tenSanPham LIKE %:keyword%)
            AND (:idThuongHieu IS NULL OR sp.thuongHieu.id = :idThuongHieu)
            AND (:idChatLieu IS NULL OR sp.chatLieu.id = :idChatLieu)
            AND (:trangThai IS NULL OR sp.trangThai = :trangThai)
            GROUP BY
                sp.id, sp.maSanPham, sp.tenSanPham, sp.hinhAnh, sp.trangThai, th.tenThuongHieu, cl.tenChatLieu
            """)
    List<SanPhamResponse> findSanPhamByFiltersForExcel(
            @Param("keyword") String keyword,
            @Param("idThuongHieu") Long idThuongHieu,
            @Param("idChatLieu") Long idChatLieu,
            @Param("trangThai") Integer trangThai
    );

    Optional<SanPham> findByMaSanPham(String maSanPham);
}

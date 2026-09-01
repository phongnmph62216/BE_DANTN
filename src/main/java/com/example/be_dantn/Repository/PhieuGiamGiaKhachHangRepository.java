package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.PhieuGiamGiaKhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhieuGiamGiaKhachHangRepository extends JpaRepository<PhieuGiamGiaKhachHang, Long> {

    // Lấy danh sách khách hàng đang được áp dụng phiếu này
    List<PhieuGiamGiaKhachHang> findByPhieuGiamGia_Id(Long phieuGiamGiaId);

    // Kiểm tra xem khách hàng có sở hữu phiếu giảm giá cá nhân này không
    boolean existsByPhieuGiamGia_IdAndKhachHang_Id(Long phieuGiamGiaId, Long khachHangId);

    // Xóa tất cả các khách hàng đang được áp dụng phiếu này
    @Modifying
    @Query("DELETE FROM PhieuGiamGiaKhachHang p WHERE p.phieuGiamGia.id = :phieuGiamGiaId")
    void deleteByPhieuGiamGiaId(@Param("phieuGiamGiaId") Long phieuGiamGiaId);
}

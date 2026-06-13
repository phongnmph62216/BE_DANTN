package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.PhieuGiamGia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PhieuGiamGiaRepository extends JpaRepository<PhieuGiamGia, Long> {

    @Query("""
            SELECT pgg FROM PhieuGiamGia pgg
            WHERE
                (:keyword IS NULL OR pgg.maPhieuGiamGia LIKE %:keyword% OR pgg.tenPhieuGiamGia LIKE %:keyword%)
            AND (:loaiGiam IS NULL OR pgg.loaiGiam = :loaiGiam)
            AND (:trangThai IS NULL OR pgg.trangThai = :trangThai)
            AND (CAST(:tuNgay AS timestamp) IS NULL OR pgg.ngayKetThuc >= :tuNgay)
            AND (CAST(:denNgay AS timestamp) IS NULL OR pgg.ngayBatDau <= :denNgay)
            ORDER BY pgg.ngayTao DESC
            """)
    Page<PhieuGiamGia> findByFilters(
            @Param("keyword") String keyword,
            @Param("loaiGiam") Integer loaiGiam,
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgay") LocalDateTime denNgay,
            @Param("trangThai") Integer trangThai,
            Pageable pageable
    );
}

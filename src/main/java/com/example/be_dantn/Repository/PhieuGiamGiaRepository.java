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

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query("""
            UPDATE PhieuGiamGia pgg
            SET pgg.trangThai = 2
            WHERE pgg.trangThai != 2
              AND pgg.ngayKetThuc IS NOT NULL
              AND pgg.ngayKetThuc < :now
            """)
    void updateExpiredStatus(@Param("now") LocalDateTime now);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query("""
            UPDATE PhieuGiamGia pgg
            SET pgg.trangThai = 1
            WHERE pgg.trangThai = 0
              AND pgg.ngayBatDau IS NOT NULL
              AND pgg.ngayBatDau <= :now
              AND (pgg.ngayKetThuc IS NULL OR pgg.ngayKetThuc >= :now)
            """)
    void updateActiveStatus(@Param("now") LocalDateTime now);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query("""
            UPDATE PhieuGiamGia pgg
            SET pgg.trangThai = 0
            WHERE pgg.trangThai = 1
              AND pgg.ngayBatDau IS NOT NULL
              AND pgg.ngayBatDau > :now
            """)
    void updateUpcomingStatus(@Param("now") LocalDateTime now);
}


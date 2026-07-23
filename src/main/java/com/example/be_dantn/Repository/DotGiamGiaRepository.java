package com.example.be_dantn.Repository;
// Trigger compile at 4:27 PM

import com.example.be_dantn.Dto.Response.DotGiamGiaResponseDTO;
import com.example.be_dantn.Entity.DotGiamGia;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface DotGiamGiaRepository extends JpaRepository<DotGiamGia, Long> {

    @Query("""
            SELECT new com.example.be_dantn.Dto.Response.DotGiamGiaResponseDTO(
                dgg.id,
                dgg.maDotGiamGia,
                dgg.tenDotGiamGia,
                dgg.phanTramGiam,
                dgg.ngayBatDau,
                dgg.ngayKetThuc,
                dgg.trangThai
            )
            FROM DotGiamGia dgg
            WHERE
                (:keyword IS NULL OR dgg.maDotGiamGia LIKE %:keyword% OR dgg.tenDotGiamGia LIKE %:keyword%)
            AND (:trangThai IS NULL OR dgg.trangThai = :trangThai)
            AND (CAST(:tuNgay AS timestamp) IS NULL OR dgg.ngayBatDau >= :tuNgay)
            AND (CAST(:denNgay AS timestamp) IS NULL OR dgg.ngayBatDau <= :denNgay)
            """)
    Page<DotGiamGiaResponseDTO> findByFilters(
            @Param("keyword") String keyword,
            @Param("trangThai") Integer trangThai,
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgay") LocalDateTime denNgay,
            Pageable pageable
    );

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query("""
            UPDATE DotGiamGia dgg
            SET dgg.trangThai = 0
            WHERE dgg.trangThai = 1
              AND dgg.ngayKetThuc IS NOT NULL
              AND dgg.ngayKetThuc < :now
            """)
    void updateExpiredStatus(@Param("now") LocalDateTime now);
}


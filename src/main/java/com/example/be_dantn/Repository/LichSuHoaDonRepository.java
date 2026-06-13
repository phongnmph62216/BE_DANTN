package com.example.be_dantn.Repository;

import com.example.be_dantn.Dto.Response.LichSuHoaDonResponseDTO;
import com.example.be_dantn.Entity.LichSuHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LichSuHoaDonRepository extends JpaRepository<LichSuHoaDon, Long> {

    @Query("""
            SELECT new com.example.be_dantn.Dto.Response.LichSuHoaDonResponseDTO(
                lshd.id,
                hd.id,
                hd.maHoaDon,
                lshd.trangThai,
                lshd.hanhDong,
                lshd.ghiChu,
                lshd.nguoiThucHien,
                lshd.thoiGian
            )
            FROM LichSuHoaDon lshd
            JOIN lshd.hoaDon hd
            WHERE hd.id = :hoaDonId
            ORDER BY lshd.thoiGian DESC
            """)
    List<LichSuHoaDonResponseDTO> findByHoaDonId(@Param("hoaDonId") Long hoaDonId);
}
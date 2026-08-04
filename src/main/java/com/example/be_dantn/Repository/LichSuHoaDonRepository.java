package com.example.be_dantn.repository;

import com.example.be_dantn.Dto.Response.LichSuHoaDonResponseDTO;
import com.example.be_dantn.Entity.LichSuHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichSuHoaDonRepository extends JpaRepository<LichSuHoaDon, Long> {

    @Query("""
            SELECT new com.example.be_dantn.Dto.Response.LichSuHoaDonResponseDTO(
                CASE WHEN nv IS NOT NULL THEN CONCAT(nv.maNhanVien, ' - ', nv.hoVaTen) WHEN ls.hoaDon.nguoiSua IS NOT NULL THEN ls.hoaDon.nguoiSua ELSE 'Hệ thống' END,
                ls.hanhDong,
                ls.trangThai,
                ls.thoiGian,
                ls.ghiChu
            )
            FROM LichSuHoaDon ls
            LEFT JOIN ls.nhanVien nv
            WHERE ls.hoaDon.id = :idHoaDon
            ORDER BY ls.thoiGian DESC
            """)
    List<LichSuHoaDonResponseDTO> findByIdHoaDon(@Param("idHoaDon") Long idHoaDon);
}

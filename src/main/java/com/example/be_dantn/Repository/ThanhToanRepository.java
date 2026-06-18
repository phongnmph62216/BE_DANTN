package com.example.be_dantn.repository;

import com.example.be_dantn.Dto.Response.ThanhToanDTO;
import com.example.be_dantn.Entity.ThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThanhToanRepository extends JpaRepository<ThanhToan, Long> {
    @Query("""
            SELECT new com.example.be_dantn.Dto.Response.ThanhToanDTO(
                tt.phuongThuc,
                tt.soTien,
                tt.thoiGian,
                tt.nguoiThucHien,
                tt.ghiChu
            )
            FROM ThanhToan tt
            WHERE tt.hoaDon.id = :idHoaDon
            """)
    List<ThanhToanDTO> findByIdHoaDon(@Param("idHoaDon") Long idHoaDon);
}

package com.example.be_dantn.repositoty;
import com.example.be_dantn.entity.LichSuHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LichSuHoaDonRepository extends JpaRepository<LichSuHoaDon, Long> {

    List<LichSuHoaDon> findByIdHoaDonOrderByThoiGianDesc(Long idHoaDon);
}
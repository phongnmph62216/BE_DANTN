package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.DiaChi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaChiRepository extends JpaRepository<DiaChi, Long> {

    List<DiaChi> findByKhachHang_Id(Long khachHangId);

    @Modifying
    @Query("UPDATE DiaChi dc SET dc.kieuDiaChiLaMacDinh = false WHERE dc.khachHang.id = :khachHangId")
    void updateAllToNotDefaultByKhachHangId(@Param("khachHangId") Long khachHangId);
}

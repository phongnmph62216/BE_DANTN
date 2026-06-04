package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.KieuDang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KieuDangRepository extends JpaRepository<KieuDang, Long>, JpaSpecificationExecutor<KieuDang> {
    List<KieuDang> findAllByTrangThai(Integer trangThai);

    boolean existsByMaKieuDang(String maKieuDang);

    boolean existsByMaKieuDangAndIdNot(String maKieuDang, Long id);
}

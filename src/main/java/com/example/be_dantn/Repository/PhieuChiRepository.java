package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.PhieuChi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhieuChiRepository extends JpaRepository<PhieuChi, Long> {

    List<PhieuChi> findByGiaoCaId(Long giaoCaId);
}

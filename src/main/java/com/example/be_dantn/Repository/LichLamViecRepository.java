package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.LichLamViec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LichLamViecRepository extends JpaRepository<LichLamViec, Long>, JpaSpecificationExecutor<LichLamViec> {

    boolean existsByNhanVienIdAndNgayLamViecAndCaLamViecId(Long nhanVienId, java.time.LocalDate ngayLamViec, Long caLamViecId);

    boolean existsByNhanVienIdAndNgayLamViecAndCaLamViecIdAndIdNot(Long nhanVienId, java.time.LocalDate ngayLamViec, Long caLamViecId, Long id);
}

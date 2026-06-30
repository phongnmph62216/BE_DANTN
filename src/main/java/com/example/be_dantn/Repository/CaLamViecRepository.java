package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.CaLamViec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaLamViecRepository extends JpaRepository<CaLamViec, Long>, JpaSpecificationExecutor<CaLamViec> {

    List<CaLamViec> findAllByTrangThai(Integer trangThai);

    boolean existsByMaCa(String maCa);

    boolean existsByMaCaAndIdNot(String maCa, Long id);
}

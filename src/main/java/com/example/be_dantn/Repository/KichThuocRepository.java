package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.KichThuoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KichThuocRepository extends JpaRepository<KichThuoc, Long>, JpaSpecificationExecutor<KichThuoc> {
    List<KichThuoc> findAllByTrangThai(Integer trangThai);

    boolean existsByMaKichThuoc(String maKichThuoc);

    boolean existsByMaKichThuocAndIdNot(String maKichThuoc, Long id);
}

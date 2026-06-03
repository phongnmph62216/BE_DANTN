package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.KichThuoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface KichThuocRepository extends JpaRepository<KichThuoc, Long>, JpaSpecificationExecutor<KichThuoc> {

    boolean existsByMaKichThuoc(String maKichThuoc);

    boolean existsByMaKichThuocAndIdNot(String maKichThuoc, Long id);
}


package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.XuatSu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface XuatSuRepository extends JpaRepository<XuatSu, Long>, JpaSpecificationExecutor<XuatSu> {

    Optional<XuatSu> findByMaXuatSu(String maXuatSu);

    boolean existsByMaXuatSu(String maXuatSu);

    boolean existsByMaXuatSuAndIdNot(String maXuatSu, Long id);
}


package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.XuatSu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface XuatSuRepository extends JpaRepository<XuatSu, Long>, JpaSpecificationExecutor<XuatSu> {
    List<XuatSu> findAllByTrangThai(Integer trangThai);

    boolean existsByMaXuatSu(String maXuatSu);

    boolean existsByMaXuatSuAndIdNot(String maXuatSu, Long id);
}

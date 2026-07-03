package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.VaiAo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaiAoRepository extends JpaRepository<VaiAo, Long>, JpaSpecificationExecutor<VaiAo> {
    List<VaiAo> findAllByTrangThai(Integer trangThai);

    boolean existsByMaVaiAo(String maVaiAo);

    boolean existsByMaVaiAoAndIdNot(String maVaiAo, Long id);
}

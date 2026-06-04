package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.CoAo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoAoRepository extends JpaRepository<CoAo, Long>, JpaSpecificationExecutor<CoAo> {
    List<CoAo> findAllByTrangThai(Integer trangThai);

    boolean existsByMaCoAo(String maCoAo);

    boolean existsByMaCoAoAndIdNot(String maCoAo, Long id);
}

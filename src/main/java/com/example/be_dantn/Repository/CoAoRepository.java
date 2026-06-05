package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.CoAo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CoAoRepository extends JpaRepository<CoAo, Long>, JpaSpecificationExecutor<CoAo> {

    boolean existsByMaCoAo(String maCoAo);

    boolean existsByMaCoAoAndIdNot(String maCoAo, Long id);
}


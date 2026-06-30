package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.GiaoCa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GiaoCaRepository extends JpaRepository<GiaoCa, Long>, JpaSpecificationExecutor<GiaoCa> {
}

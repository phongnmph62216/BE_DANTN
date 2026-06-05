package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.MauSac;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MauSacRepository extends JpaRepository<MauSac, Long>, JpaSpecificationExecutor<MauSac> {

    boolean existsByMaMauSac(String maMauSac);

    boolean existsByMaMauSacAndIdNot(String maMauSac, Long id);
}


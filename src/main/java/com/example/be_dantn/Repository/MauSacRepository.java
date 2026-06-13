package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.MauSac;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MauSacRepository extends JpaRepository<MauSac, Long>, JpaSpecificationExecutor<MauSac> {
    List<MauSac> findAllByTrangThai(Integer trangThai);

    boolean existsByMaMauSac(String maMauSac);

    boolean existsByMaMauSacAndIdNot(String maMauSac, Long id);
}

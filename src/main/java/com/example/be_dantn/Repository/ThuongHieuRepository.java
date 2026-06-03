package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.ThuongHieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ThuongHieuRepository extends JpaRepository<ThuongHieu, Long>, JpaSpecificationExecutor<ThuongHieu> {

    boolean existsByMaThuongHieu(String maThuongHieu);

    boolean existsByMaThuongHieuAndIdNot(String maThuongHieu, Long id);
}


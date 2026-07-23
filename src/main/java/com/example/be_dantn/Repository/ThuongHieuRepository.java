package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.ThuongHieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThuongHieuRepository extends JpaRepository<ThuongHieu, Long>, JpaSpecificationExecutor<ThuongHieu> {
    List<ThuongHieu> findAllByTrangThai(Integer trangThai);

    boolean existsByMaThuongHieu(String maThuongHieu);

    boolean existsByMaThuongHieuAndIdNot(String maThuongHieu, Long id);
}

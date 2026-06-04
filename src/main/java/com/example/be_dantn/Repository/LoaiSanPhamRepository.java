package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.LoaiSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoaiSanPhamRepository extends JpaRepository<LoaiSanPham, Long>, JpaSpecificationExecutor<LoaiSanPham> {
    List<LoaiSanPham> findAllByTrangThai(Integer trangThai);

    boolean existsByMaLoaiSanPham(String maLoaiSanPham);

    boolean existsByMaLoaiSanPhamAndIdNot(String maLoaiSanPham, Long id);
}

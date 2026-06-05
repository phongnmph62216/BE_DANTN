package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.LoaiSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LoaiSanPhamRepository extends JpaRepository<LoaiSanPham, Long>, JpaSpecificationExecutor<LoaiSanPham> {

    boolean existsByMaLoaiSanPham(String maLoaiSanPham);

    boolean existsByMaLoaiSanPhamAndIdNot(String maLoaiSanPham, Long id);
}


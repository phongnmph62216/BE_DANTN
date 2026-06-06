package com.example.be_dantn.repositoty;

import com.example.be_dantn.entity.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, Long> {

    List<HoaDonChiTiet> findByHoaDon_Id(Long idHoaDon);

    List<HoaDonChiTiet> findByHoaDon_IdAndTrangThai(Long idHoaDon, String trangThai);

    Optional<HoaDonChiTiet> findByHoaDon_IdAndChiTietSanPham_IdAndTrangThai(
            Long idHoaDon,
            Long idChiTietSanPham,
            String trangThai
    );
}
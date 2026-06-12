package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NhanVienRepository extends JpaRepository<NhanVien, Long>{
    boolean existsByMaNhanVien(String maNhanVien);

    boolean existsByEmail(String email);

    boolean existsBySoDienThoai(String soDienThoai);

    boolean existsByCccd(String cccd);

}

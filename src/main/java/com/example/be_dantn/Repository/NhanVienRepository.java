package com.example.be_dantn.Repository;

import com.example.be_dantn.Dto.Response.NhanVienResponseDTO;
import com.example.be_dantn.Entity.NhanVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Long> {

    @Query("""
            SELECT new com.example.be_dantn.Dto.Response.NhanVienResponseDTO(
                nv.id, nv.anh, nv.maNhanVien, nv.hoVaTen, nv.email, nv.soDienThoai, nv.diaChi, nv.trangThai, vt.ten
            )
            FROM NhanVien nv
            JOIN nv.vaiTro vt
            WHERE
                (:keyword IS NULL OR nv.maNhanVien LIKE %:keyword% OR nv.hoVaTen LIKE %:keyword% OR nv.email LIKE %:keyword%)
            AND (:trangThai IS NULL OR nv.trangThai = :trangThai)
            """)
    Page<NhanVienResponseDTO> findByFilters(
            @Param("keyword") String keyword,
            @Param("trangThai") Integer trangThai,
            Pageable pageable
    );

    @Query("""
            SELECT new com.example.be_dantn.Dto.Response.NhanVienResponseDTO(
                nv.id, nv.anh, nv.maNhanVien, nv.hoVaTen, nv.email, nv.soDienThoai, nv.diaChi, nv.trangThai, vt.ten
            )
            FROM NhanVien nv
            JOIN nv.vaiTro vt
            WHERE
                (:keyword IS NULL OR nv.maNhanVien LIKE %:keyword% OR nv.hoVaTen LIKE %:keyword% OR nv.email LIKE %:keyword%)
            AND (:trangThai IS NULL OR nv.trangThai = :trangThai)
            """)
    List<NhanVienResponseDTO> findAllForExcel(
            @Param("keyword") String keyword,
            @Param("trangThai") Integer trangThai
    );

    Optional<NhanVien> findByEmailAndIdNot(String email, Long id);
    Optional<NhanVien> findBySoDienThoaiAndIdNot(String soDienThoai, Long id);
    
    boolean existsByEmail(String email);
    boolean existsBySoDienThoai(String soDienThoai);
    boolean existsByCccd(String cccd);
    Optional<NhanVien> findBySoDienThoai(String soDienThoai);
    Optional<NhanVien> findByEmail(String email);
}

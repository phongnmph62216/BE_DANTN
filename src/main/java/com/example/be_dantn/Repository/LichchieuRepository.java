package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.Lichchieu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LichchieuRepository extends JpaRepository<Lichchieu, Long> {

    // Code moi: tim kiem chi theo ten.
    @Query("select l from Lichchieu l where lower(l.phim) like lower(concat('%', :tenPhim, '%'))")
    List<Lichchieu> findByPhimContainingIgnoreCase(@Param("tenPhim") String tenPhim);

    // Code moi: lay danh sach theo diem danh gia giam dan.
    @Query("select l from Lichchieu l left join fetch l.phim12 p order by p.diemDanhGia desc")
    List<Lichchieu> findAllByOrderByDiemDanhGiaDesc();
}

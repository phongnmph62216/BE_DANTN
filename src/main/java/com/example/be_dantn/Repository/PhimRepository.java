package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.Phim;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhimRepository extends JpaRepository<Phim, Long> {

    // Code moi: tim kiem chi theo ten.
    List<Phim> findByTenPhimContainingIgnoreCase(String tenPhim);

    // Code moi: lay danh sach theo diem danh gia giam dan.
    List<Phim> findAllByOrderByDiemDanhGiaDesc();
}

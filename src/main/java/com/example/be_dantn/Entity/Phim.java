package com.example.be_dantn.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
        @AllArgsConstructor
        @NoArgsConstructor
        @Getter
        @Setter
        @Entity
        @Table(name = "phim")
public class Phim {


//    id, tenPhim, daoDien, theLoai, namSanXuat,
//    diemDanhGia, trangThai
    @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private String tenPhim;

    private String daoDien;

    private String theLoai;

    private String namSanXuat;

    private String diemDanhGia;


    @Column(name = "trang_thai")
    private String trangThai;


     @OneToMany(mappedBy = "phim12", cascade = CascadeType.ALL, orphanRemoval = true)
         private List<Lichchieu> lichchieus = new ArrayList<Lichchieu>();

}

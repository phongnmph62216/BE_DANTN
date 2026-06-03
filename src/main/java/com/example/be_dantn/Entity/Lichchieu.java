package com.example.be_dantn.Entity;


import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "lichchieu")

public class Lichchieu {

//    id, phim, ngayGioChieu,
//    phongChieu, giaVe


    @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phim;

    private String ngayGioChieu;

    private String phongChieu;

    private String giaVe;

    @ManyToOne
    private Phim phim12;
}

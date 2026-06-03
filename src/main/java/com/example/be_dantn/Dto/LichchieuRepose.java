package com.example.be_dantn.Dto;


import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LichchieuRepose {

    private Long id;

    private String phim;

    private String ngayGioChieu;

    private String phongChieu;

    private String giaVe;

    private String tenPhim;

    private String daoDien;


    private String namSanXuat;

    private String diemDanhGia;
}

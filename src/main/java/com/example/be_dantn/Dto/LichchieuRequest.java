package com.example.be_dantn.Dto;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LichchieuRequest {

    private Long idPhim;

    @NotBlank(message = "Phim không được để trống")
    private String phim;

    @NotBlank(message = "Ngày giờ chiếu không được để trống")
    private String ngayGioChieu;

    @NotBlank(message = "Phòng chiếu không được để trống")
    private String phongChieu;


    @NotBlank(message = "Giá vé không được để trống")
    private String giaVe;

}

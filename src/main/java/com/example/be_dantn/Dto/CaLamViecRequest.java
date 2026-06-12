package com.example.be_dantn.Dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CaLamViecRequest {

    private Long id;

    private String maCa;

    private String tenCa;

    private LocalTime gioBatDau;

    private LocalTime gioKetThuc;

    private Integer trangThai;
}

package com.example.be_dantn.Dto;

import java.time.LocalTime;import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CaLamViecRespon {
    private Long id;

    private String maCa;

    private String tenCa;

    private LocalTime gioBatDau;

    private LocalTime gioKetThuc;

    private Integer trangThai;
}

package com.example.be_dantn.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ca_lam_viec")
public class CaLamViec {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String maCa;

    private String tenCa;

    private LocalTime gioBatDau;

    private LocalTime gioKetThuc;

    private Integer trangThai;

    @OneToMany(mappedBy = "caLamViec",cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    private Set<LichLamViec> lichLamViec = new HashSet<>();
}

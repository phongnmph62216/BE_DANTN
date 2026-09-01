package com.example.be_dantn.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "phieu_chi")
public class PhieuChi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_giao_ca")
    private GiaoCa giaoCa;

    private String maPhieu;

    private LocalDateTime ngayTao;

    private BigDecimal soTien;

    private String lyDo;

    private String nguoiTao;

    @PrePersist
    public void prePersist() {
        if (ngayTao == null) {
            ngayTao = LocalDateTime.now();
        }
        if (maPhieu == null) {
            maPhieu = "PC-" + System.currentTimeMillis();
        }
    }
}

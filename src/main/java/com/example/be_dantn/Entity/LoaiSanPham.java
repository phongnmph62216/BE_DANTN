package com.example.be_dantn.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "loai_san_pham")
public class LoaiSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String maLoaiSanPham;

    private String tenLoaiSanPham;

    private Integer trangThai;

    private LocalDateTime ngayTao;

    private LocalDateTime ngaySua;

    private String nguoiTao;

    private String nguoiSua;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (ngayTao == null) {
            ngayTao = now;
        }
        ngaySua = now;
    }

    @PreUpdate
    public void preUpdate() {
        ngaySua = LocalDateTime.now();
    }
}


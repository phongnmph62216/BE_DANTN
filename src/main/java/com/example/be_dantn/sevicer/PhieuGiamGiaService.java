package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.Request.PhieuGiamGiaCreateRequest;
import com.example.be_dantn.Dto.Request.PhieuGiamGiaUpdateRequest;
import com.example.be_dantn.Dto.Response.PhieuGiamGiaResponseDTO;
import com.example.be_dantn.Entity.PhieuGiamGia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface PhieuGiamGiaService {
    Page<PhieuGiamGiaResponseDTO> getByFilters(
            String keyword,
            Integer loaiGiam,
            LocalDateTime tuNgay,
            LocalDateTime denNgay,
            Integer trangThai,
            Pageable pageable
    );

    void toggleStatus(Long id);

    PhieuGiamGia createPhieuGiamGia(PhieuGiamGiaCreateRequest request);

    PhieuGiamGiaResponseDTO getPhieuGiamGiaById(Long id);

    PhieuGiamGia updatePhieuGiamGia(Long id, PhieuGiamGiaUpdateRequest request);
}

package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.Request.DotGiamGiaCreateRequest;
import com.example.be_dantn.Dto.Response.DotGiamGiaResponseDTO;
import com.example.be_dantn.Entity.DotGiamGia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface DotGiamGiaService {
    Page<DotGiamGiaResponseDTO> getByFilters(String keyword, Integer trangThai, LocalDateTime tuNgay, LocalDateTime denNgay, Pageable pageable);
    void toggleStatus(Long id);
    DotGiamGia createDotGiamGia(DotGiamGiaCreateRequest request);
}

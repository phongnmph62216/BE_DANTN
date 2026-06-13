package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.Request.HoaDonUpdateTrangThaiRequest;
import com.example.be_dantn.Dto.Response.HoaDonResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface HoaDonService {

    Page<HoaDonResponseDTO> getByFilters(
            String keyword,
            Integer loaiHoaDon,
            LocalDateTime tuNgay,
            LocalDateTime denNgay,
            Integer trangThai,
            Pageable pageable
    );

    HoaDonResponseDTO getHoaDonById(Long id);

    void updateTrangThai(Long id, HoaDonUpdateTrangThaiRequest request);
    byte[] exportExcel(
            String keyword,
            Integer loaiHoaDon,
            LocalDateTime tuNgay,
            LocalDateTime denNgay,
            Integer trangThai
    );
}
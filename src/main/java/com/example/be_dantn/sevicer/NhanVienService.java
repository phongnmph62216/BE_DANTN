package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.Request.NhanVienCreateRequest;
import com.example.be_dantn.Dto.Request.NhanVienUpdateRequest;
import com.example.be_dantn.Dto.Response.NhanVienResponseDTO;
import com.example.be_dantn.Entity.NhanVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface NhanVienService {
    Page<NhanVienResponseDTO> getByFilters(String keyword, Integer trangThai, Pageable pageable);
    void toggleStatus(Long id);
    byte[] exportNhanVienToExcel(String keyword, Integer trangThai) throws IOException;
    NhanVien getNhanVienById(Long id);
    NhanVien updateNhanVien(Long id, NhanVienUpdateRequest request);
    NhanVien createNhanVien(NhanVienCreateRequest request);
}

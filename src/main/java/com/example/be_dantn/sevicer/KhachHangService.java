package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.Request.DiaChiRequest;
import com.example.be_dantn.Dto.Request.KhachHangRequest;
import com.example.be_dantn.Dto.Response.KhachHangDetailResponseDTO;
import com.example.be_dantn.Dto.Response.KhachHangResponseDTO;
import com.example.be_dantn.Entity.KhachHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface KhachHangService {
    Page<KhachHangResponseDTO> getByFilters(String keyword, Integer gioiTinh, Integer trangThai, Pageable pageable);
    KhachHangDetailResponseDTO getKhachHangDetail(Long id);
    KhachHang createKhachHang(KhachHangRequest request);
    KhachHang updateKhachHang(Long id, KhachHangRequest request);
    void toggleStatus(Long id);
    List<KhachHangResponseDTO> getAllForExcel();

    // Dia Chi methods
    void addDiaChi(Long khachHangId, DiaChiRequest request);
    void setDiaChiMacDinh(Long khachHangId, Long diaChiId);
}

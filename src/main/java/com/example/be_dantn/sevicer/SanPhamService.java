package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.Request.SanPhamCreateRequest;
import com.example.be_dantn.Dto.Request.SanPhamUpdateRequest;
import com.example.be_dantn.Dto.Response.SanPhamDetailResponse;
import com.example.be_dantn.Dto.Response.SanPhamResponse;
import com.example.be_dantn.Entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface SanPhamService {
    Page<SanPhamResponse> getSanPhamByFilter(String keyword, Long idThuongHieu, Long idChatLieu, Integer trangThai, Pageable pageable);
    SanPhamCreateRequest createSanPham(SanPhamCreateRequest request);
    SanPham toggleStatus(Long id);
    SanPhamDetailResponse findById(Long id);
    SanPhamDetailResponse updateProduct(Long id, SanPhamUpdateRequest request);
    byte[] exportToExcel(String keyword, Long idThuongHieu, Long idChatLieu, Integer trangThai) throws IOException;
    SanPhamDetailResponse findByQrCode(String maSanPham);
}

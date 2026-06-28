package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.Request.ChiTietSanPhamUpdateRequest;
import com.example.be_dantn.Dto.Response.ChiTietSanPhamResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface ChiTietSanPhamService {
    Page<ChiTietSanPhamResponseDTO> getVariantsByFilter(String keyword, Long idMauSac, Long idKichThuoc, Integer trangThai, BigDecimal minPrice, BigDecimal maxPrice, Long idSanPham, Pageable pageable);

    void toggleStatus(Long id);

    byte[] exportToExcel(String keyword, Long idMauSac, Long idKichThuoc, Integer trangThai, BigDecimal minPrice, BigDecimal maxPrice, Long idSanPham) throws IOException;

    ChiTietSanPhamResponseDTO findById(Long id);

    ChiTietSanPhamResponseDTO updateVariant(Long id, ChiTietSanPhamUpdateRequest request);

    ChiTietSanPhamResponseDTO findByQrCode(String maChiTietSanPham);

    ChiTietSanPhamResponseDTO createVariant(com.example.be_dantn.Dto.Request.ChiTietSanPhamCreateRequest request);
}

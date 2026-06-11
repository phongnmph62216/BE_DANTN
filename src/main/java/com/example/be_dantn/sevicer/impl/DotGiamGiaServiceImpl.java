package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.Request.DotGiamGiaCreateRequest;
import com.example.be_dantn.Dto.Response.DotGiamGiaResponseDTO;
import com.example.be_dantn.Entity.ChiTietSanPham;
import com.example.be_dantn.Entity.DotGiamGia;
import com.example.be_dantn.Repository.ChiTietSanPhamRepository;
import com.example.be_dantn.Repository.DotGiamGiaRepository;
import com.example.be_dantn.sevicer.DotGiamGiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DotGiamGiaServiceImpl implements DotGiamGiaService {

    private final DotGiamGiaRepository dotGiamGiaRepository;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository; // Bổ sung

    @Override
    public Page<DotGiamGiaResponseDTO> getByFilters(String keyword, Integer trangThai, LocalDateTime tuNgay, LocalDateTime denNgay, Pageable pageable) {
        return dotGiamGiaRepository.findByFilters(keyword, trangThai, tuNgay, denNgay, pageable);
    }

    @Override
    @Transactional
    public void toggleStatus(Long id) {
        DotGiamGia dotGiamGia = dotGiamGiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt giảm giá với id: " + id));
        dotGiamGia.setTrangThai(dotGiamGia.getTrangThai() == 1 ? 0 : 1);
        dotGiamGiaRepository.save(dotGiamGia);
    }

    @Override
    @Transactional
    public DotGiamGia createDotGiamGia(DotGiamGiaCreateRequest request) {
        // Bước 1: Validate dữ liệu
        if (request.getNgayKetThuc().isBefore(request.getNgayBatDau())) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu.");
        }

        // Bước 2: Tạo và lưu DotGiamGia
        DotGiamGia dotGiamGia = new DotGiamGia();
        dotGiamGia.setMaDotGiamGia("DGG" + System.currentTimeMillis()); // Generate mã
        dotGiamGia.setTenDotGiamGia(request.getTenDotGiamGia());
        dotGiamGia.setPhanTramGiam(request.getPhanTramGiam());
        dotGiamGia.setNgayBatDau(request.getNgayBatDau());
        dotGiamGia.setNgayKetThuc(request.getNgayKetThuc());
        dotGiamGia.setTrangThai(1); // Mặc định là hoạt động khi mới tạo

        DotGiamGia savedDotGiamGia = dotGiamGiaRepository.save(dotGiamGia);

        // Bước 3, 4, 5: Áp dụng cho các biến thể sản phẩm
        List<Long> idList = request.getDanhSachIdChiTietSanPham();
        if (idList != null && !idList.isEmpty()) {
            List<ChiTietSanPham> variantsToUpdate = chiTietSanPhamRepository.findAllById(idList);
            
            for (ChiTietSanPham variant : variantsToUpdate) {
                variant.setDotGiamGia(savedDotGiamGia); // Set khóa ngoại
            }
            
            chiTietSanPhamRepository.saveAll(variantsToUpdate);
        }

        return savedDotGiamGia;
    }
}

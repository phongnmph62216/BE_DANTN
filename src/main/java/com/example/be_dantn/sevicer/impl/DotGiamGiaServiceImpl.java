package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.Request.DotGiamGiaCreateRequest;
import com.example.be_dantn.Dto.Request.DotGiamGiaUpdateRequest;
import com.example.be_dantn.Dto.Response.DotGiamGiaResponseDTO;
import com.example.be_dantn.Entity.ChiTietSanPham;
import com.example.be_dantn.Entity.DotGiamGia;
import com.example.be_dantn.Exception.ResourceNotFoundException;
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
import java.util.stream.Collectors;

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

    @Override
    public DotGiamGiaResponseDTO getDotGiamGiaById(Long id) {
        DotGiamGia dotGiamGia = dotGiamGiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đợt giảm giá với id: " + id));

        List<Long> danhSachIdChiTietSanPham = chiTietSanPhamRepository.findByDotGiamGia_Id(id)
                .stream()
                .map(ChiTietSanPham::getId)
                .collect(Collectors.toList());

        DotGiamGiaResponseDTO responseDTO = new DotGiamGiaResponseDTO();
        responseDTO.setId(dotGiamGia.getId());
        responseDTO.setMaDotGiamGia(dotGiamGia.getMaDotGiamGia());
        responseDTO.setTenDotGiamGia(dotGiamGia.getTenDotGiamGia());
        responseDTO.setPhanTramGiam(dotGiamGia.getPhanTramGiam());
        responseDTO.setNgayBatDau(dotGiamGia.getNgayBatDau());
        responseDTO.setNgayKetThuc(dotGiamGia.getNgayKetThuc());
        responseDTO.setTrangThai(dotGiamGia.getTrangThai());
        responseDTO.setDanhSachIdChiTietSanPham(danhSachIdChiTietSanPham);

        return responseDTO;
    }

    @Override
    @Transactional
    public DotGiamGia updateDotGiamGia(Long id, DotGiamGiaUpdateRequest request) {
        // Bước 1: Validate dữ liệu
        if (request.getNgayKetThuc().isBefore(request.getNgayBatDau())) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu.");
        }

        DotGiamGia dotGiamGia = dotGiamGiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đợt giảm giá với id: " + id));

        // Cập nhật thông tin cơ bản
        dotGiamGia.setTenDotGiamGia(request.getTenDotGiamGia());
        dotGiamGia.setPhanTramGiam(request.getPhanTramGiam());
        dotGiamGia.setNgayBatDau(request.getNgayBatDau());
        dotGiamGia.setNgayKetThuc(request.getNgayKetThuc());
        dotGiamGia.setTrangThai(request.getTrangThai());

        DotGiamGia savedDotGiamGia = dotGiamGiaRepository.save(dotGiamGia);

        // Bước 1 (Gỡ bỏ): Tìm TẤT CẢ các chi_tiet_san_pham hiện đang có id_dot_giam_gia = ID này
        List<ChiTietSanPham> currentVariants = chiTietSanPhamRepository.findByDotGiamGia_Id(id);
        for (ChiTietSanPham variant : currentVariants) {
            variant.setDotGiamGia(null);
        }
        chiTietSanPhamRepository.saveAll(currentVariants);

        // Bước 2 (Áp dụng mới): Lấy danh sách biến thể theo danhSachIdChiTietSanPham request gửi lên
        List<Long> newVariantIds = request.getDanhSachIdChiTietSanPham();
        if (newVariantIds != null && !newVariantIds.isEmpty()) {
            List<ChiTietSanPham> variantsToUpdate = chiTietSanPhamRepository.findAllByIdIn(newVariantIds);
            for (ChiTietSanPham variant : variantsToUpdate) {
                variant.setDotGiamGia(savedDotGiamGia);
            }
            chiTietSanPhamRepository.saveAll(variantsToUpdate);
        }

        return savedDotGiamGia;
    }
}

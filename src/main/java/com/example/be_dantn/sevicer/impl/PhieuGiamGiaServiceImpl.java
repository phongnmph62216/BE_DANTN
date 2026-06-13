package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.Request.PhieuGiamGiaCreateRequest;
import com.example.be_dantn.Dto.Request.PhieuGiamGiaUpdateRequest;
import com.example.be_dantn.Dto.Response.PhieuGiamGiaResponseDTO;
import com.example.be_dantn.Entity.KhachHang;
import com.example.be_dantn.Entity.PhieuGiamGia;
import com.example.be_dantn.Entity.PhieuGiamGiaKhachHang;
import com.example.be_dantn.Exception.ResourceNotFoundException;
import com.example.be_dantn.Repository.KhachHangRepository;
import com.example.be_dantn.Repository.PhieuGiamGiaKhachHangRepository;
import com.example.be_dantn.Repository.PhieuGiamGiaRepository;
import com.example.be_dantn.sevicer.EmailService;
import com.example.be_dantn.sevicer.PhieuGiamGiaService;
import lombok.RequiredArgsConstructor;
import java.time.format.DateTimeFormatter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhieuGiamGiaServiceImpl implements PhieuGiamGiaService {

    private final PhieuGiamGiaRepository phieuGiamGiaRepository;
    private final PhieuGiamGiaKhachHangRepository phieuGiamGiaKhachHangRepository;
    private final KhachHangRepository khachHangRepository;
    private final EmailService emailService;

    @Override
    public Page<PhieuGiamGiaResponseDTO> getByFilters(String keyword, Integer loaiGiam, LocalDateTime tuNgay, LocalDateTime denNgay, Integer trangThai, Pageable pageable) {
        Page<PhieuGiamGia> phieuPage = phieuGiamGiaRepository.findByFilters(keyword, loaiGiam, tuNgay, denNgay, trangThai, pageable);
        return phieuPage.map(this::convertToDto);
    }

    @Override
    @Transactional
    public void toggleStatus(Long id) {
        PhieuGiamGia phieu = phieuGiamGiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu giảm giá với id: " + id));

        if (phieu.getTrangThai() == 1) {
            phieu.setTrangThai(2); // Đang diễn ra -> Kết thúc
        } else if (phieu.getTrangThai() == 2) {
            if (phieu.getNgayKetThuc() != null && phieu.getNgayKetThuc().isAfter(LocalDateTime.now())) {
                phieu.setTrangThai(1); // Kết thúc -> Đang diễn ra
            } else {
                throw new IllegalArgumentException("Không thể kích hoạt lại phiếu đã hết hạn.");
            }
        } else if (phieu.getTrangThai() == 0) {
             phieu.setTrangThai(2); // Sắp diễn ra -> Kết thúc (hủy)
        }
        phieuGiamGiaRepository.save(phieu);
    }

    @Override
    @Transactional
    public PhieuGiamGia createPhieuGiamGia(PhieuGiamGiaCreateRequest request) {
        // Bước 1: Validate dữ liệu
        if (request.getNgayKetThuc().isBefore(request.getNgayBatDau())) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau hoặc bằng ngày bắt đầu.");
        }
        if (request.getLoaiGiam() == 0 && request.getGiamToiDa() == null) {
            throw new IllegalArgumentException("Giảm tối đa là bắt buộc khi loại giảm là %.");
        }

        // Bước 2: Map và lưu PhieuGiamGia
        PhieuGiamGia phieuGiamGia = new PhieuGiamGia();
        phieuGiamGia.setMaPhieuGiamGia(request.getMaPhieu());
        phieuGiamGia.setTenPhieuGiamGia(request.getTenPhieu());
        phieuGiamGia.setKieuApDung(request.getKieuApDung());
        phieuGiamGia.setLoaiGiam(request.getLoaiGiam());
        phieuGiamGia.setGiaTri(request.getGiaTriGiam());
        phieuGiamGia.setGiaGiamToiDa(request.getGiamToiDa());
        phieuGiamGia.setDieuKienGiam(request.getDonToiThieu());
        phieuGiamGia.setSoLuong(request.getSoLuong());
        phieuGiamGia.setNgayBatDau(request.getNgayBatDau());
        phieuGiamGia.setNgayKetThuc(request.getNgayKetThuc());

        // Xác định trạng thái ban đầu
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(request.getNgayBatDau())) {
            phieuGiamGia.setTrangThai(0); // Sắp diễn ra
        } else {
            phieuGiamGia.setTrangThai(1); // Đang diễn ra
        }

        PhieuGiamGia savedPhieu = phieuGiamGiaRepository.save(phieuGiamGia);

        // Bước 3: Xử lý áp dụng cho khách hàng cá nhân
        if (request.getKieuApDung() == 1 && !CollectionUtils.isEmpty(request.getDanhSachKhachHangIds())) {
            List<PhieuGiamGiaKhachHang> listToSave = new ArrayList<>();
            List<KhachHang> khachHangs = khachHangRepository.findAllById(request.getDanhSachKhachHangIds());

            String discountDetails;
            if (savedPhieu.getLoaiGiam() == 0) {
                discountDetails = String.format("Giảm %,.0f%% (Giảm tối đa: %,.0f đ)", 
                        savedPhieu.getGiaTri().doubleValue(), 
                        savedPhieu.getGiaGiamToiDa().doubleValue());
            } else {
                discountDetails = String.format("Giảm %,.0f đ", savedPhieu.getGiaTri().doubleValue());
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String expiryDateStr = savedPhieu.getNgayKetThuc() != null 
                    ? savedPhieu.getNgayKetThuc().format(formatter) 
                    : "Không giới hạn";

            for (KhachHang kh : khachHangs) {
                PhieuGiamGiaKhachHang pggkh = new PhieuGiamGiaKhachHang();
                pggkh.setPhieuGiamGia(savedPhieu);
                pggkh.setKhachHang(kh);
                listToSave.add(pggkh);

                // Gửi email cho khách hàng được áp dụng phiếu giảm giá cá nhân
                emailService.sendVoucherEmail(
                        kh.getEmail(),
                        kh.getHoTen(),
                        savedPhieu.getMaPhieuGiamGia(),
                        savedPhieu.getTenPhieuGiamGia(),
                        discountDetails,
                        expiryDateStr
                );
            }
            phieuGiamGiaKhachHangRepository.saveAll(listToSave);
        }

        return savedPhieu;
    }

    @Override
    public PhieuGiamGiaResponseDTO getPhieuGiamGiaById(Long id) {
        PhieuGiamGia phieu = phieuGiamGiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu giảm giá với id: " + id));

        PhieuGiamGiaResponseDTO dto = convertToDto(phieu);

        if (phieu.getKieuApDung() == 1) {
            List<Long> khachHangIds = phieuGiamGiaKhachHangRepository.findByPhieuGiamGia_Id(id).stream()
                    .map(pggkh -> pggkh.getKhachHang().getId())
                    .collect(Collectors.toList());
            dto.setDanhSachKhachHangIds(khachHangIds);
        }

        return dto;
    }

    @Override
    @Transactional
    public PhieuGiamGia updatePhieuGiamGia(Long id, PhieuGiamGiaUpdateRequest request) {
        // Validate
        if (request.getNgayKetThuc().isBefore(request.getNgayBatDau())) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau hoặc bằng ngày bắt đầu.");
        }
        if (request.getLoaiGiam() == 0 && request.getGiamToiDa() == null) {
            throw new IllegalArgumentException("Giảm tối đa là bắt buộc khi loại giảm là %.");
        }

        PhieuGiamGia phieuGiamGia = phieuGiamGiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu giảm giá với id: " + id));

        // Cập nhật thông tin cơ bản
        phieuGiamGia.setTenPhieuGiamGia(request.getTenPhieu());
        phieuGiamGia.setKieuApDung(request.getKieuApDung());
        phieuGiamGia.setLoaiGiam(request.getLoaiGiam());
        phieuGiamGia.setGiaTri(request.getGiaTriGiam());
        phieuGiamGia.setGiaGiamToiDa(request.getGiamToiDa());
        phieuGiamGia.setDieuKienGiam(request.getDonToiThieu());
        phieuGiamGia.setSoLuong(request.getSoLuong());
        phieuGiamGia.setNgayBatDau(request.getNgayBatDau());
        phieuGiamGia.setNgayKetThuc(request.getNgayKetThuc());

        PhieuGiamGia savedPhieu = phieuGiamGiaRepository.save(phieuGiamGia);

        // Xử lý bảng trung gian: Xóa cũ - Thêm mới
        phieuGiamGiaKhachHangRepository.deleteByPhieuGiamGiaId(id);

        if (request.getKieuApDung() == 1 && !CollectionUtils.isEmpty(request.getDanhSachKhachHangIds())) {
            List<PhieuGiamGiaKhachHang> listToSave = new ArrayList<>();
            List<KhachHang> khachHangs = khachHangRepository.findAllById(request.getDanhSachKhachHangIds());

            String discountDetails;
            if (savedPhieu.getLoaiGiam() == 0) {
                discountDetails = String.format("Giảm %,.0f%% (Giảm tối đa: %,.0f đ)", 
                        savedPhieu.getGiaTri().doubleValue(), 
                        savedPhieu.getGiaGiamToiDa().doubleValue());
            } else {
                discountDetails = String.format("Giảm %,.0f đ", savedPhieu.getGiaTri().doubleValue());
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String expiryDateStr = savedPhieu.getNgayKetThuc() != null 
                    ? savedPhieu.getNgayKetThuc().format(formatter) 
                    : "Không giới hạn";

            for (KhachHang kh : khachHangs) {
                PhieuGiamGiaKhachHang pggkh = new PhieuGiamGiaKhachHang();
                pggkh.setPhieuGiamGia(savedPhieu);
                pggkh.setKhachHang(kh);
                listToSave.add(pggkh);

                // Gửi email cho khách hàng được áp dụng phiếu giảm giá cá nhân
                emailService.sendVoucherEmail(
                        kh.getEmail(),
                        kh.getHoTen(),
                        savedPhieu.getMaPhieuGiamGia(),
                        savedPhieu.getTenPhieuGiamGia(),
                        discountDetails,
                        expiryDateStr
                );
            }
            phieuGiamGiaKhachHangRepository.saveAll(listToSave);
        }

        return savedPhieu;
    }

    private PhieuGiamGiaResponseDTO convertToDto(PhieuGiamGia entity) {
        return new PhieuGiamGiaResponseDTO(
                entity.getId(),
                entity.getMaPhieuGiamGia(),
                entity.getTenPhieuGiamGia(),
                entity.getLoaiGiam(),
                entity.getGiaTri(),
                entity.getGiaGiamToiDa(),
                entity.getDieuKienGiam(),
                entity.getSoLuong(),
                0, // Tạm thời hardcode soLuongDaDung = 0
                entity.getKieuApDung(),
                entity.getNgayBatDau(),
                entity.getNgayKetThuc(),
                entity.getTrangThai(),
                null // Mặc định là null, chỉ set khi get detail
        );
    }
}

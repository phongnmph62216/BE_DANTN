package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.Request.PhieuGiamGiaCreateRequest;
import com.example.be_dantn.Dto.Request.PhieuGiamGiaUpdateRequest;
import com.example.be_dantn.Dto.Response.PhieuGiamGiaResponseDTO;
import com.example.be_dantn.Entity.KhachHang;
import com.example.be_dantn.Entity.PhieuGiamGia;
import com.example.be_dantn.Entity.PhieuGiamGiaKhachHang;
import com.example.be_dantn.Exception.ResourceNotFoundException;
import com.example.be_dantn.repository.HoaDonRepository;
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
    private final HoaDonRepository hoaDonRepository;
    private final com.example.be_dantn.Config.CodeGenerator codeGenerator;

    @Override
    @Transactional
    public Page<PhieuGiamGiaResponseDTO> getByFilters(String keyword, Integer loaiGiam, LocalDateTime tuNgay, LocalDateTime denNgay, Integer trangThai, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        phieuGiamGiaRepository.updateExpiredStatus(now);
        phieuGiamGiaRepository.updateActiveStatus(now);
        phieuGiamGiaRepository.updateUpcomingStatus(now);
        
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
            LocalDateTime now = LocalDateTime.now();
            if (phieu.getNgayKetThuc() != null && phieu.getNgayKetThuc().isAfter(now)) {
                if (phieu.getNgayBatDau() != null && now.isBefore(phieu.getNgayBatDau())) {
                    phieu.setTrangThai(0); // Sắp diễn ra
                } else {
                    phieu.setTrangThai(1); // Đang diễn ra
                }
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
        String maPhieu = request.getMaPhieu();
        if (!org.springframework.util.StringUtils.hasText(maPhieu)) {
            maPhieu = codeGenerator.generateCode("phieu_giam_gia", "ma_phieu_giam_gia", "PGG");
        }
        phieuGiamGia.setMaPhieuGiamGia(maPhieu);
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
        if (request.getNgayKetThuc().isBefore(now)) {
            phieuGiamGia.setTrangThai(2); // Đã kết thúc
        } else if (now.isBefore(request.getNgayBatDau())) {
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
    @Transactional
    public PhieuGiamGiaResponseDTO getPhieuGiamGiaById(Long id) {
        LocalDateTime now = LocalDateTime.now();
        phieuGiamGiaRepository.updateExpiredStatus(now);
        phieuGiamGiaRepository.updateActiveStatus(now);
        phieuGiamGiaRepository.updateUpcomingStatus(now);

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

        LocalDateTime now = LocalDateTime.now();
        int calculatedStatus;
        if (request.getNgayKetThuc().isBefore(now)) {
            calculatedStatus = 2; // Đã kết thúc
        } else {
            // Nếu ngày kết thúc ở tương lai
            if (phieuGiamGia.getNgayKetThuc() != null && phieuGiamGia.getNgayKetThuc().isBefore(now)) {
                // Nếu trước đó đã hết hạn (endDate cũ ở quá khứ), nay được sửa ngày kết thúc sang tương lai -> kích hoạt lại
                if (now.isBefore(request.getNgayBatDau())) {
                    calculatedStatus = 0; // Sắp diễn ra
                } else {
                    calculatedStatus = 1; // Đang diễn ra
                }
            } else {
                // Nếu trước đó đang hoạt động/sắp diễn ra, hoặc bị kết thúc thủ công nhưng ngày bắt đầu/kết thúc thay đổi
                // Nếu ngày bắt đầu/kết thúc thay đổi, ta tính lại trạng thái
                if (!phieuGiamGia.getNgayBatDau().equals(request.getNgayBatDau()) || !phieuGiamGia.getNgayKetThuc().equals(request.getNgayKetThuc())) {
                    if (now.isBefore(request.getNgayBatDau())) {
                        calculatedStatus = 0; // Sắp diễn ra
                    } else {
                        calculatedStatus = 1; // Đang diễn ra
                    }
                } else {
                    // Giữ nguyên trạng thái cũ (người dùng có thể tắt thủ công)
                    calculatedStatus = phieuGiamGia.getTrangThai();
                }
            }
        }

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
        phieuGiamGia.setTrangThai(calculatedStatus);

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
        int soLuongDaDung = hoaDonRepository.countUsedVouchers(entity.getId());
        List<Long> khachHangIds = null;
        if (entity.getKieuApDung() != null && entity.getKieuApDung() == 1) {
            khachHangIds = phieuGiamGiaKhachHangRepository.findByPhieuGiamGia_Id(entity.getId()).stream()
                    .map(pggkh -> pggkh.getKhachHang().getId())
                    .collect(Collectors.toList());
        }
        return new PhieuGiamGiaResponseDTO(
                entity.getId(),
                entity.getMaPhieuGiamGia(),
                entity.getTenPhieuGiamGia(),
                entity.getLoaiGiam(),
                entity.getGiaTri(),
                entity.getGiaGiamToiDa(),
                entity.getDieuKienGiam(),
                entity.getSoLuong(),
                soLuongDaDung,
                entity.getKieuApDung(),
                entity.getNgayBatDau(),
                entity.getNgayKetThuc(),
                entity.getTrangThai(),
                khachHangIds
        );
    }
}

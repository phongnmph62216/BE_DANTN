package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.Request.LichSuHoaDonCreateRequest;
import com.example.be_dantn.Dto.Response.LichSuHoaDonResponseDTO;
import com.example.be_dantn.Entity.HoaDon;
import com.example.be_dantn.Entity.LichSuHoaDon;
import com.example.be_dantn.Entity.NhanVien;
import com.example.be_dantn.Exception.ResourceNotFoundException;
import com.example.be_dantn.Repository.HoaDonRepository;
import com.example.be_dantn.Repository.LichSuHoaDonRepository;
import com.example.be_dantn.Repository.NhanVienRepository;
import com.example.be_dantn.sevicer.LichSuHoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LichSuHoaDonServiceImpl implements LichSuHoaDonService {

    private final LichSuHoaDonRepository lichSuHoaDonRepository;
    private final HoaDonRepository hoaDonRepository;
    private final NhanVienRepository nhanVienRepository;

    @Override
    public List<LichSuHoaDonResponseDTO> getByHoaDonId(Long hoaDonId) {
        if (!hoaDonRepository.existsById(hoaDonId)) {
            throw new ResourceNotFoundException("Không tìm thấy hóa đơn với id: " + hoaDonId);
        }

        return lichSuHoaDonRepository.findByHoaDonId(hoaDonId);
    }

    @Override
    @Transactional
    public LichSuHoaDonResponseDTO create(LichSuHoaDonCreateRequest request) {
        HoaDon hoaDon = hoaDonRepository.findById(request.getIdHoaDon())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn với id: " + request.getIdHoaDon()));

        NhanVien nhanVien = null;
        String nguoiThucHien = request.getNguoiThucHien();

        if (request.getIdNhanVien() != null) {
            nhanVien = nhanVienRepository.findById(request.getIdNhanVien())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên với id: " + request.getIdNhanVien()));
            nguoiThucHien = nhanVien.getHoVaTen();
        }

        if (nguoiThucHien == null || nguoiThucHien.trim().isEmpty()) {
            nguoiThucHien = "Hệ thống";
        }

        LichSuHoaDon entity = new LichSuHoaDon();
        entity.setHoaDon(hoaDon);
        entity.setNhanVien(nhanVien);
        entity.setTrangThai(request.getTrangThai());
        entity.setHanhDong(request.getHanhDong());
        entity.setGhiChu(request.getGhiChu());
        entity.setNguoiThucHien(nguoiThucHien);
        entity.setThoiGian(LocalDateTime.now());

        LichSuHoaDon saved = lichSuHoaDonRepository.save(entity);

        return new LichSuHoaDonResponseDTO(
                saved.getId(),
                hoaDon.getId(),
                hoaDon.getMaHoaDon(),
                saved.getTrangThai(),
                saved.getHanhDong(),
                saved.getGhiChu(),
                saved.getNguoiThucHien(),
                saved.getThoiGian()
        );
    }
}
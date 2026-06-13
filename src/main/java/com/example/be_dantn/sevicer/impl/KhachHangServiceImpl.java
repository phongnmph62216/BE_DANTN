package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.Request.DiaChiRequest;
import com.example.be_dantn.Dto.Request.KhachHangRequest;
import com.example.be_dantn.Dto.Response.DiaChiResponseDTO;
import com.example.be_dantn.Dto.Response.KhachHangDetailResponseDTO;
import com.example.be_dantn.Dto.Response.KhachHangResponseDTO;
import com.example.be_dantn.Entity.DiaChi;
import com.example.be_dantn.Entity.KhachHang;
import com.example.be_dantn.Exception.ResourceNotFoundException;
import com.example.be_dantn.Repository.DiaChiRepository;
import com.example.be_dantn.Repository.KhachHangRepository;
import com.example.be_dantn.sevicer.KhachHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KhachHangServiceImpl implements KhachHangService {

    private final KhachHangRepository khachHangRepository;
    private final DiaChiRepository diaChiRepository;

    @Override
    public Page<KhachHangResponseDTO> getByFilters(String keyword, Integer gioiTinh, Integer trangThai, Pageable pageable) {
        return khachHangRepository.findByFilters(keyword, gioiTinh, trangThai, pageable);
    }

    @Override
    public KhachHangDetailResponseDTO getKhachHangDetail(Long id) {
        KhachHang khachHang = khachHangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng với id: " + id));

        List<DiaChiResponseDTO> diaChiList = khachHang.getDanhSachDiaChi().stream()
                .sorted(Comparator.comparing(DiaChi::getKieuDiaChiLaMacDinh).reversed())
                .map(this::mapToDiaChiDTO)
                .collect(Collectors.toList());

        return mapToKhachHangDetailDTO(khachHang, diaChiList);
    }

    @Override
    public KhachHang createKhachHang(KhachHangRequest request) {
        KhachHang khachHang = new KhachHang();
        khachHang.setMaKhachHang(request.getMaKhachHang());
        khachHang.setHoTen(request.getHoTen());
        khachHang.setSdt(request.getSdt());
        khachHang.setEmail(request.getEmail());
        khachHang.setGioiTinh(request.getGioiTinh());
        khachHang.setNgaySinh(request.getNgaySinh());
        khachHang.setTrangThai(request.getTrangThai());
        return khachHangRepository.save(khachHang);
    }

    @Override
    public KhachHang updateKhachHang(Long id, KhachHangRequest request) {
        KhachHang khachHang = khachHangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng với id: " + id));

        khachHang.setHoTen(request.getHoTen());
        khachHang.setSdt(request.getSdt());
        khachHang.setEmail(request.getEmail());
        khachHang.setGioiTinh(request.getGioiTinh());
        khachHang.setNgaySinh(request.getNgaySinh());
        khachHang.setTrangThai(request.getTrangThai());

        return khachHangRepository.save(khachHang);
    }

    @Override
    @Transactional
    public void toggleStatus(Long id) {
        KhachHang khachHang = khachHangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng với id: " + id));
        khachHang.setTrangThai(khachHang.getTrangThai() == 1 ? 0 : 1);
        khachHangRepository.save(khachHang);
    }

    @Override
    public List<KhachHangResponseDTO> getAllForExcel() {
        return khachHangRepository.findAllForExcel();
    }

    @Override
    @Transactional
    public void addDiaChi(Long khachHangId, DiaChiRequest request) {
        KhachHang khachHang = khachHangRepository.findById(khachHangId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng với id: " + khachHangId));

        List<DiaChi> existingAddresses = diaChiRepository.findByKhachHang_Id(khachHangId);

        if (request.getKieuDiaChiLaMacDinh()) {
            diaChiRepository.updateAllToNotDefaultByKhachHangId(khachHangId);
        }

        DiaChi newDiaChi = new DiaChi();
        newDiaChi.setKhachHang(khachHang);
        newDiaChi.setTenNguoiNhan(request.getTenNguoiNhan());
        newDiaChi.setSdtNguoiNhan(request.getSdtNguoiNhan());
        newDiaChi.setDiaChiCuThe(request.getDiaChiCuThe());
        newDiaChi.setTinhThanhPho(request.getTinhThanhPho());
        newDiaChi.setQuanHuyen(request.getQuanHuyen());
        newDiaChi.setPhuongXa(request.getPhuongXa());

        if (existingAddresses.isEmpty()) {
            newDiaChi.setKieuDiaChiLaMacDinh(true);
        } else {
            newDiaChi.setKieuDiaChiLaMacDinh(request.getKieuDiaChiLaMacDinh());
        }

        diaChiRepository.save(newDiaChi);
    }

    @Override
    @Transactional
    public void setDiaChiMacDinh(Long khachHangId, Long diaChiId) {
        diaChiRepository.updateAllToNotDefaultByKhachHangId(khachHangId);

        DiaChi diaChi = diaChiRepository.findById(diaChiId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ với id: " + diaChiId));

        if (!diaChi.getKhachHang().getId().equals(khachHangId)) {
            throw new IllegalArgumentException("Địa chỉ không thuộc khách hàng này.");
        }

        diaChi.setKieuDiaChiLaMacDinh(true);
        diaChiRepository.save(diaChi);
    }

    // --- Private Helper Methods ---
    private DiaChiResponseDTO mapToDiaChiDTO(DiaChi diaChi) {
        return new DiaChiResponseDTO(
                diaChi.getId(),
                diaChi.getTenNguoiNhan(),
                diaChi.getSdtNguoiNhan(),
                diaChi.getDiaChiCuThe(),
                diaChi.getTinhThanhPho(),
                diaChi.getQuanHuyen(),
                diaChi.getPhuongXa(),
                diaChi.getKieuDiaChiLaMacDinh()
        );
    }

    private KhachHangDetailResponseDTO mapToKhachHangDetailDTO(KhachHang kh, List<DiaChiResponseDTO> diaChiList) {
        return new KhachHangDetailResponseDTO(
                kh.getId(),
                kh.getMaKhachHang(),
                kh.getHoTen(),
                kh.getSdt(),
                kh.getEmail(),
                kh.getGioiTinh(),
                kh.getNgaySinh(),
                kh.getTrangThai(),
                diaChiList
        );
    }
}

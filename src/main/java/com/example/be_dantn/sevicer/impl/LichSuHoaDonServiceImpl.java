package com.example.be_dantn.sevicer.impl;
import com.example.be_dantn.entity.LichSuHoaDon;
import com.example.be_dantn.repositoty.LichSuHoaDonRepository;
import com.example.be_dantn.Dto.LichSuHoaDonResponse;
import com.example.be_dantn.sevicer.LichSuHoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LichSuHoaDonServiceImpl implements LichSuHoaDonService {

    private final LichSuHoaDonRepository lichSuHoaDonRepository;

    @Override
    public void luuLichSu(Long idHoaDon, String trangThai, String hanhDong, String ghiChu) {
        LichSuHoaDon lichSu = LichSuHoaDon.builder()
                .idHoaDon(idHoaDon)
                .trangThai(trangThai)
                .hanhDong(hanhDong)
                .ghiChu(ghiChu)
                .thoiGian(LocalDateTime.now())
                .build();

        lichSuHoaDonRepository.save(lichSu);
    }

    @Override
    public List<LichSuHoaDonResponse> getByHoaDon(Long idHoaDon) {
        return lichSuHoaDonRepository.findByIdHoaDonOrderByThoiGianDesc(idHoaDon)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private LichSuHoaDonResponse toResponse(LichSuHoaDon lichSu) {
        return LichSuHoaDonResponse.builder()
                .id(lichSu.getId())
                .idHoaDon(lichSu.getIdHoaDon())
                .trangThai(lichSu.getTrangThai())
                .trangThaiText(getTrangThaiText(lichSu.getTrangThai()))
                .thoiGian(lichSu.getThoiGian())
                .ghiChu(lichSu.getGhiChu())
                .hanhDong(lichSu.getHanhDong())
                .build();
    }

    private String getTrangThaiText(String trangThai) {
        if (trangThai == null) {
            return "Không xác định";
        }

        return switch (trangThai) {
            case "CHO_XAC_NHAN" -> "Chờ xác nhận";
            case "DANG_XU_LY" -> "Đang xử lý";
            case "DANG_GIAO" -> "Đang giao";
            case "HOAN_THANH" -> "Hoàn thành";
            case "DA_HUY" -> "Đã hủy";
            default -> "Không xác định";
        };
    }
}
package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.HoaDonResponseDTO;
import com.example.be_dantn.Dto.Response.HoaDonDetailResponseDTO;
import com.example.be_dantn.Dto.Response.LichSuHoaDonResponseDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface HoaDonService {

    Page<HoaDonResponseDTO> layDanhSachHoaDon(
            String maHoaDon,
            LocalDateTime tuNgay,
            LocalDateTime denNgay,
            Integer loaiDon,
            List<Integer> trangThai,
            int page,
            int size
    );

    byte[] xuatExcelDanhSachHoaDon(
            String maHoaDon,
            LocalDateTime tuNgay,
            LocalDateTime denNgay,
            Integer loaiDon,
            List<Integer> trangThai
    );

    HoaDonDetailResponseDTO layChiTietHoaDon(Long id);

    void capNhatTrangThaiHoaDon(Long id, Integer trangThaiMoi, String ghiChu);

    List<LichSuHoaDonResponseDTO> layLichSuHoaDon(Long id);

    HoaDonDetailResponseDTO traCuuHoaDon(String maHoaDon, String email);

    List<HoaDonDetailResponseDTO> layDanhSachHoaDonTheoKhachHang(Long khachHangId);
}

package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.HoaDonResponseDTO;
import com.example.be_dantn.Dto.DonHangChoResponseDTO;
import com.example.be_dantn.Dto.Request.ThanhToanRequestDTO;
import com.example.be_dantn.Dto.Request.ThongTinNhanHangRequestDTO;
import java.util.List;

public interface BanHangService {
    HoaDonResponseDTO taoDonHangCho();
    void capNhatThongTinNhanHang(Long idHoaDon, ThongTinNhanHangRequestDTO request);
    Long thanhToanHoaDon(Long idHoaDon, ThanhToanRequestDTO request);
    List<DonHangChoResponseDTO> layDanhSachDonHangCho();
}

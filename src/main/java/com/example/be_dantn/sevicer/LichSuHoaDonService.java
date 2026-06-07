package com.example.be_dantn.sevicer;
import com.example.be_dantn.Dto.LichSuHoaDonResponse;

import java.util.List;

public interface LichSuHoaDonService {

    void luuLichSu(Long idHoaDon, String trangThai, String hanhDong, String ghiChu);

    List<LichSuHoaDonResponse> getByHoaDon(Long idHoaDon);
}
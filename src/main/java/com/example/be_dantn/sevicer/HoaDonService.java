package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.CapNhatTrangThaiHoaDonRequest;
import com.example.be_dantn.Dto.HoaDonRequest;
import com.example.be_dantn.Dto.HoaDonResponse;

import java.io.IOException;
import java.util.List;

public interface HoaDonService {

    List<HoaDonResponse> findAll();

    HoaDonResponse findById(Long id);

    HoaDonResponse findByMaHoaDon(String maHoaDon);

    HoaDonResponse add(HoaDonRequest request);

    HoaDonResponse update(HoaDonRequest request, Long id);

    HoaDonResponse capNhatTrangThai(Long id, CapNhatTrangThaiHoaDonRequest request);

    void delete(Long id);

    byte[] exportExcel() throws IOException;

    HoaDonResponse huyHoaDon(Long id, CapNhatTrangThaiHoaDonRequest request);
}
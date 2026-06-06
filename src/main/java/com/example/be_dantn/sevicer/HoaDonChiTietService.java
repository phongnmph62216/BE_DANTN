package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.HoaDonChiTietRequest;
import com.example.be_dantn.Dto.HoaDonChiTietResponse;

import java.util.List;

public interface HoaDonChiTietService {

    List<HoaDonChiTietResponse> findAll();

    List<HoaDonChiTietResponse> findByHoaDon(Long idHoaDon);

    HoaDonChiTietResponse findById(Long id);

    HoaDonChiTietResponse add(HoaDonChiTietRequest request);

    HoaDonChiTietResponse update(HoaDonChiTietRequest request, Long id);

    void delete(Long id);
}
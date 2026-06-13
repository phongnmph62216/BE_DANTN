package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.Response.HoaDonChiTietResponseDTO;

import java.util.List;

public interface HoaDonChiTietService {

    List<HoaDonChiTietResponseDTO> getByHoaDonId(Long hoaDonId);
}
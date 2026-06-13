package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.Request.LichSuHoaDonCreateRequest;
import com.example.be_dantn.Dto.Response.LichSuHoaDonResponseDTO;

import java.util.List;

public interface LichSuHoaDonService {

    List<LichSuHoaDonResponseDTO> getByHoaDonId(Long hoaDonId);

    LichSuHoaDonResponseDTO create(LichSuHoaDonCreateRequest request);
}
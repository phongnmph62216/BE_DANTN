package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.GiaoCaDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface GiaoCaService {

    List<GiaoCaDTO> findAll(String keyword, LocalDateTime fromDate, LocalDateTime toDate);

    GiaoCaDTO findById(Long id);

    com.example.be_dantn.Dto.GiaoCaStatusDTO getShiftStatus(Long employeeId);

    GiaoCaDTO moCa(com.example.be_dantn.Dto.Request.MoCaRequest request);

    GiaoCaDTO chotCa(com.example.be_dantn.Dto.Request.ChotCaRequest request);

    GiaoCaDTO doiSoat(Long id, com.example.be_dantn.Dto.Request.DoiSoatRequest request);
}

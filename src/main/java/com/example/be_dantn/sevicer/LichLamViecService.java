package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.LichLamViecDTO;

import java.time.LocalDate;
import java.util.List;

public interface LichLamViecService {

    List<LichLamViecDTO> findAll(Long idNhanVien, LocalDate startDate, LocalDate endDate);

    LichLamViecDTO findById(Long id);

    LichLamViecDTO save(LichLamViecDTO dto);

    LichLamViecDTO update(Long id, LichLamViecDTO dto);

    void delete(Long id);
}

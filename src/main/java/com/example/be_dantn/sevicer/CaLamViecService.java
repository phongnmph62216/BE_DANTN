package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.CaLamViecDTO;
import java.time.LocalTime;
import java.util.List;

public interface CaLamViecService {

    List<CaLamViecDTO> findAll(String keyword, LocalTime startTime, LocalTime endTime, Integer trangThai);

    CaLamViecDTO findById(Long id);

    CaLamViecDTO save(CaLamViecDTO caLamViecDTO);

    CaLamViecDTO update(Long id, CaLamViecDTO caLamViecDTO);

    CaLamViecDTO toggleStatus(Long id);
}

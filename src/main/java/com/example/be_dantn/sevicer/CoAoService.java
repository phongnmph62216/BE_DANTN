package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.CoAoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CoAoService {

    Page<CoAoDTO> findAll(Pageable pageable, String keyword, Integer trangThai);

    CoAoDTO findById(Long id);

    CoAoDTO save(CoAoDTO coAoDTO);

    CoAoDTO update(Long id, CoAoDTO coAoDTO);

    CoAoDTO toggleStatus(Long id);
}


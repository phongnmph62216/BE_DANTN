package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.TayAoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TayAoService {

    Page<TayAoDTO> findAll(Pageable pageable, String keyword, Integer trangThai);

    TayAoDTO findById(Long id);

    TayAoDTO save(TayAoDTO tayAoDTO);

    TayAoDTO update(Long id, TayAoDTO tayAoDTO);

    TayAoDTO toggleStatus(Long id);
}


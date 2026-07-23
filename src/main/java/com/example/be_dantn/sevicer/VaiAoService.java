package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.VaiAoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VaiAoService {

    Page<VaiAoDTO> findAll(Pageable pageable, String keyword, Integer trangThai);

    VaiAoDTO findById(Long id);

    VaiAoDTO save(VaiAoDTO vaiAoDTO);

    VaiAoDTO update(Long id, VaiAoDTO vaiAoDTO);

    VaiAoDTO toggleStatus(Long id);
}


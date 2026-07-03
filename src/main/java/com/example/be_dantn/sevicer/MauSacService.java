package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.MauSacDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MauSacService {

    Page<MauSacDTO> findAll(Pageable pageable, String keyword, Integer trangThai);

    MauSacDTO findById(Long id);

    MauSacDTO save(MauSacDTO mauSacDTO);

    MauSacDTO update(Long id, MauSacDTO mauSacDTO);

    MauSacDTO toggleStatus(Long id);
}


package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.KieuDangDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface KieuDangService {

    Page<KieuDangDTO> findAll(Pageable pageable, String keyword, Integer trangThai);

    KieuDangDTO findById(Long id);

    KieuDangDTO save(KieuDangDTO kieuDangDTO);

    KieuDangDTO update(Long id, KieuDangDTO kieuDangDTO);

    KieuDangDTO toggleStatus(Long id);
}


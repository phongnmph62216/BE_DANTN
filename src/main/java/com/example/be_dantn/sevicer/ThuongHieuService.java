package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.ThuongHieuDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ThuongHieuService {

    Page<ThuongHieuDTO> findAll(Pageable pageable, String keyword, Integer trangThai);

    ThuongHieuDTO findById(Long id);

    ThuongHieuDTO save(ThuongHieuDTO thuongHieuDTO);

    ThuongHieuDTO update(Long id, ThuongHieuDTO thuongHieuDTO);

    ThuongHieuDTO toggleStatus(Long id);
}


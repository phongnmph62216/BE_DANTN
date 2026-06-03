package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.KichThuocDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface KichThuocService {

    Page<KichThuocDTO> findAll(Pageable pageable, String keyword, Integer trangThai);

    KichThuocDTO findById(Long id);

    KichThuocDTO save(KichThuocDTO kichThuocDTO);

    KichThuocDTO update(Long id, KichThuocDTO kichThuocDTO);

    KichThuocDTO toggleStatus(Long id);
}


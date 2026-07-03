package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.XuatSuDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface XuatSuService {

    Page<XuatSuDTO> findAll(Pageable pageable, String keyword, Integer trangThai);

    XuatSuDTO findById(Long id);

    XuatSuDTO save(XuatSuDTO xuatSuDTO);

    XuatSuDTO update(Long id, XuatSuDTO xuatSuDTO);

    XuatSuDTO toggleStatus(Long id);
}


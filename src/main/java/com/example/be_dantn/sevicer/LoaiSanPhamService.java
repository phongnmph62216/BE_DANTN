package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.LoaiSanPhamDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoaiSanPhamService {

    Page<LoaiSanPhamDTO> findAll(Pageable pageable, String keyword, Integer trangThai);

    LoaiSanPhamDTO findById(Long id);

    LoaiSanPhamDTO save(LoaiSanPhamDTO loaiSanPhamDTO);

    LoaiSanPhamDTO update(Long id, LoaiSanPhamDTO loaiSanPhamDTO);

    LoaiSanPhamDTO toggleStatus(Long id);
}


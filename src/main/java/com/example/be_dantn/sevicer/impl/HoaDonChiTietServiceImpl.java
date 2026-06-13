package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.Response.HoaDonChiTietResponseDTO;
import com.example.be_dantn.Exception.ResourceNotFoundException;
import com.example.be_dantn.Repository.HoaDonChiTietRepository;
import com.example.be_dantn.Repository.HoaDonRepository;
import com.example.be_dantn.sevicer.HoaDonChiTietService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HoaDonChiTietServiceImpl implements HoaDonChiTietService {

    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final HoaDonRepository hoaDonRepository;

    @Override
    public List<HoaDonChiTietResponseDTO> getByHoaDonId(Long hoaDonId) {
        if (!hoaDonRepository.existsById(hoaDonId)) {
            throw new ResourceNotFoundException("Không tìm thấy hóa đơn với id: " + hoaDonId);
        }

        return hoaDonChiTietRepository.findByHoaDonId(hoaDonId);
    }
}
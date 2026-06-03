package com.example.be_dantn.sevicer.impl;


import com.example.be_dantn.Dto.LichchieuRepose;
import com.example.be_dantn.Dto.LichchieuRequest;
import com.example.be_dantn.Entity.Lichchieu;
import com.example.be_dantn.Entity.Phim;
import com.example.be_dantn.Repository.LichchieuRepository;
import com.example.be_dantn.Repository.PhimRepository;
import com.example.be_dantn.exception.CustomResourceotFoundException;
import com.example.be_dantn.sevicer.LichchieuService;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LichchieuServiceImpl implements LichchieuService {

    private final LichchieuRepository lichchieuRepository;

    private final PhimRepository phimRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<LichchieuRepose> findAll() {
       return lichchieuRepository
                       .findAll()
                       .stream()
                       .map(this::mapToRespone)
                       .toList();
    }

    @Override
    public List<LichchieuRepose> searchLichchieu(String keyword) {
         if (keyword == null || keyword.isBlank()) {
                     return findAll();
                 }

                 return lichchieuRepository
                         .findByPhimContainingIgnoreCase(keyword)
                         .stream()
                         .map(this::mapToRespone)
                         .toList();
    }

    @Override
    public List<LichchieuRepose> findAllDesc() {
         return lichchieuRepository
                         .findAllByOrderByDiemDanhGiaDesc()
                         .stream()
                         .map(this::mapToRespone)
                         .toList();
    }

    @Override
    public LichchieuRepose findById(Long id) {
         return lichchieuRepository
                         .findById(id)
                         .map(this::mapToRespone)
                         .orElseThrow(() -> new CustomResourceotFoundException("... not found with id: " + id));
    }

    @Override
    public LichchieuRepose save(LichchieuRequest lichchieuRequest) {
        if (lichchieuRequest.getIdPhim() == null) {
            throw new IllegalArgumentException("maphim is required");
        }

        // Tạo entity mới và đặc biệt set id = null để đảm bảo là insert mới
        Lichchieu lichchieu = new Lichchieu();
        lichchieu.setId(null);  // Đảm bảo là insert mới
        lichchieu.setPhim(lichchieuRequest.getPhim());
        lichchieu.setGiaVe(lichchieuRequest.getGiaVe());
        lichchieu.setPhongChieu(lichchieuRequest.getPhongChieu());
        lichchieu.setNgayGioChieu(lichchieuRequest.getNgayGioChieu());


        Phim phim = findphimById(lichchieuRequest.getIdPhim());

        lichchieu.setPhim12(phim);
        lichchieu = lichchieuRepository.save(lichchieu);

        return mapToRespone(lichchieu);
    }

    @Override
    public LichchieuRepose update(LichchieuRequest lichchieuRequest, Long id) {
        return lichchieuRepository.findById(id)
                .map(existing -> {
                    if  (lichchieuRequest.getPhim() != null) existing.setPhim(lichchieuRequest.getPhim());
                    if  (lichchieuRequest.getPhongChieu() != null) existing.setPhongChieu(lichchieuRequest.getPhongChieu());
                    if  (lichchieuRequest.getGiaVe() != null) existing.setGiaVe(lichchieuRequest.getGiaVe());
                    if  (lichchieuRequest.getNgayGioChieu() != null) existing.setNgayGioChieu(lichchieuRequest.getNgayGioChieu());


                    if (lichchieuRequest.getIdPhim() != null) {
                        Phim phim = findphimById(lichchieuRequest.getIdPhim());
                        existing.setPhim12(phim);
                    }
                    lichchieuRepository.save(existing);
                    return mapToRespone(existing);

                })
                .orElseThrow(() -> new CustomResourceotFoundException(" not found with id: " + id));
    }

    @Override
    public void deleteById(Long id) {

    }



    private LichchieuRepose mapToRespone(Lichchieu lichchieu){

        LichchieuRepose res = modelMapper.map(lichchieu, LichchieuRepose.class);

        if (lichchieu.getPhim12() != null) {
            res.setTenPhim(lichchieu.getPhim12().getTenPhim());
        } if (lichchieu.getPhim12() != null) {
            res.setDaoDien(lichchieu.getPhim12().getDaoDien());
        } if (lichchieu.getPhim12() != null) {
            res.setNamSanXuat(lichchieu.getPhim12().getNamSanXuat());
        }
        if (lichchieu.getPhim12() != null) {
            res.setDiemDanhGia(lichchieu.getPhim12().getDiemDanhGia());
        }

        return res;

    }

    private Phim findphimById(Long phimId) {
        return phimRepository.findById(phimId)
                .orElseThrow(() -> new CustomResourceotFoundException("PhongBan not found with id: " + phimId));
    }
}

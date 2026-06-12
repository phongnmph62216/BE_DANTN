package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.CaLamViecRespon;
import com.example.be_dantn.Dto.CaLamViecRequest;
import com.example.be_dantn.Entity.CaLamViec;
import com.example.be_dantn.Repository.CaLamViecRepository;
import com.example.be_dantn.exception.CustomResourceotFoundException;
import com.example.be_dantn.exception.DuplicateDataException;
import com.example.be_dantn.sevicer.CaLamViecService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CaLamViecServiceImpl implements CaLamViecService {
    private final CaLamViecRepository caLamViecRepository;

    private final ModelMapper modelMapper;

    @Override
    public List<CaLamViecRespon> findAll() {

        return caLamViecRepository.findAll()
                .stream()
                .map(caLamViec ->
                        modelMapper.map(
                                caLamViec,
                                CaLamViecRespon.class
                        ))
                .toList();
    }

    @Override
    public CaLamViecRespon findById(long id) {

        return caLamViecRepository.findById(id)
                .map(caLamViec ->
                        modelMapper.map(
                                caLamViec,
                                CaLamViecRespon.class
                        ))
                .orElseThrow(() ->
                        new CustomResourceotFoundException(
                                "Không tìm thấy ca làm việc với id: " + id
                        ));
    }

    @Override
    public CaLamViecRespon add(CaLamViecRequest request) {

        CaLamViec caLamViec =
                modelMapper.map(request, CaLamViec.class);

        if (request.getMaCa() != null
                && !request.getMaCa().trim().isEmpty()) {

            if (caLamViecRepository.existsByMaCa(request.getMaCa())) {
                throw new DuplicateDataException("Mã ca đã tồn tại");
            }

            CaLamViec saved =
                    caLamViecRepository.save(caLamViec);

            return modelMapper.map(
                    saved,
                    CaLamViecRespon.class
            );
        }

        caLamViec.setMaCa(null);

        CaLamViec saved =
                caLamViecRepository.save(caLamViec);

        String maTuDong = String.format(
                "CA%03d",
                saved.getId()
        );

        saved.setMaCa(maTuDong);

        saved = caLamViecRepository.save(saved);

        return modelMapper.map(
                saved,
                CaLamViecRespon.class
        );
    }

    @Override
    public CaLamViecRespon update(
            CaLamViecRequest request,
            long id
    ) {

        return caLamViecRepository.findById(id)
                .map(caLam -> {

                    if (request.getMaCa() != null
                            && !request.getMaCa().trim().isEmpty()) {
                        caLam.setMaCa(request.getMaCa());
                    }

                    if (request.getTenCa() != null) {
                        caLam.setTenCa(request.getTenCa());
                    }

                    if (request.getGioBatDau() != null) {
                        caLam.setGioBatDau(request.getGioBatDau());
                    }

                    if (request.getGioKetThuc() != null) {
                        caLam.setGioKetThuc(request.getGioKetThuc());
                    }

                    if (request.getTrangThai() != null) {
                        caLam.setTrangThai(request.getTrangThai());
                    }

                    CaLamViec updated =
                            caLamViecRepository.save(caLam);

                    return modelMapper.map(
                            updated,
                            CaLamViecRespon.class
                    );
                })
                .orElseThrow(() ->
                        new CustomResourceotFoundException(
                                "Không tìm thấy ca làm việc với id: " + id
                        ));
    }

    @Override
    public CaLamViecRespon doiTrangThai(long id) {

        CaLamViec caLamViec =
                caLamViecRepository.findById(id)
                        .orElseThrow(() ->
                                new CustomResourceotFoundException(
                                        "Không tìm thấy ca làm việc với id: " + id
                                ));

        caLamViec.setTrangThai(
                caLamViec.getTrangThai() == 1 ? 0 : 1
        );

        CaLamViec updated =
                caLamViecRepository.save(caLamViec);

        return modelMapper.map(
                updated,
                CaLamViecRespon.class
        );
    }
}

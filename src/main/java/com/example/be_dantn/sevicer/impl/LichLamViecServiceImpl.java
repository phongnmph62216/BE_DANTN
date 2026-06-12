package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.LichLamViecRequest;
import com.example.be_dantn.Dto.LichLamViecResponse;
import com.example.be_dantn.Entity.CaLamViec;
import com.example.be_dantn.Entity.LichLamViec;
import com.example.be_dantn.Entity.NhanVien;
import com.example.be_dantn.Repository.CaLamViecRepository;
import com.example.be_dantn.Repository.LichLamViecRepository;
import com.example.be_dantn.Repository.NhanVienRepository;
import com.example.be_dantn.exception.CustomResourceotFoundException;
import com.example.be_dantn.sevicer.LichLamViecService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class LichLamViecServiceImpl implements LichLamViecService {

    private final LichLamViecRepository lichLamViecRepository;

    private final NhanVienRepository nhanVienRepository;

    private final CaLamViecRepository caLamViecRepository;

    private final ModelMapper modelMapper;

    @Override
    public List<LichLamViecResponse> findAll() {
        return lichLamViecRepository.findAll()
                .stream()
                .map(lichLamViec ->
                        modelMapper.map(
                                lichLamViec,
                                LichLamViecResponse.class
                        ))
                .toList();
    }

    @Override
    public LichLamViecResponse findById(long id) {
        return lichLamViecRepository.findById(id)
                .map(lichLamViec ->
                        modelMapper.map(
                                lichLamViec,
                                LichLamViecResponse.class
                        ))
                .orElseThrow(() ->
                        new CustomResourceotFoundException(
                                "Không tìm thấy lịch làm việc với id: " + id
                        ));
    }

    @Override
    public LichLamViecResponse add(LichLamViecRequest request) {
        NhanVien nhanVien = nhanVienRepository.findById(request.getNhanVienId())
                .orElseThrow(() ->
                        new CustomResourceotFoundException(
                                "Không tìm thấy nhân viên với id: "
                                        + request.getNhanVienId()
                        ));

        NhanVien quanLy = nhanVienRepository.findById(request.getNguoiTaoQuanLyId())
                .orElseThrow(() ->
                        new CustomResourceotFoundException(
                                "Không tìm thấy quản lý với id: "
                                        + request.getNguoiTaoQuanLyId()
                        ));

        CaLamViec caLamViec = caLamViecRepository.findById(request.getCaLamViecId())
                .orElseThrow(() ->
                        new CustomResourceotFoundException(
                                "Không tìm thấy ca làm việc với id: "
                                        + request.getCaLamViecId()
                        ));

        LichLamViec lichLamViec =
                modelMapper.map(request, LichLamViec.class);

        lichLamViec.setNhanVien(nhanVien);
        lichLamViec.setNguoiTaoQuanLy(quanLy);
        lichLamViec.setCaLamViec(caLamViec);
        lichLamViec.setTrangThai(0);

        LichLamViec saved =
                lichLamViecRepository.save(lichLamViec);

        return modelMapper.map(
                saved,
                LichLamViecResponse.class
        );
    }

    @Override
    public LichLamViecResponse update(LichLamViecRequest request, long id) {
        return lichLamViecRepository.findById(id)
                .map(lichLamViec -> {

                    if (request.getNhanVienId() != null) {

                        NhanVien nhanVien =
                                nhanVienRepository.findById(
                                                request.getNhanVienId()
                                        )
                                        .orElseThrow(() ->
                                                new CustomResourceotFoundException(
                                                        "Không tìm thấy nhân viên với id: "
                                                                + request.getNhanVienId()
                                                ));

                        lichLamViec.setNhanVien(nhanVien);
                    }

                    if (request.getNguoiTaoQuanLyId() != null) {

                        NhanVien quanLy =
                                nhanVienRepository.findById(
                                                request.getNguoiTaoQuanLyId()
                                        )
                                        .orElseThrow(() ->
                                                new CustomResourceotFoundException(
                                                        "Không tìm thấy quản lý với id: "
                                                                + request.getNguoiTaoQuanLyId()
                                                ));

                        lichLamViec.setNguoiTaoQuanLy(quanLy);
                    }

                    if (request.getCaLamViecId() != null) {

                        CaLamViec caLamViec =
                                caLamViecRepository.findById(
                                                request.getCaLamViecId()
                                        )
                                        .orElseThrow(() ->
                                                new CustomResourceotFoundException(
                                                        "Không tìm thấy ca làm việc với id: "
                                                                + request.getCaLamViecId()
                                                ));

                        lichLamViec.setCaLamViec(caLamViec);
                    }

                    if (request.getNgayLamViec() != null) {
                        lichLamViec.setNgayLamViec(
                                request.getNgayLamViec()
                        );
                    }

                    if (request.getGhiChu() != null) {
                        lichLamViec.setGhiChu(
                                request.getGhiChu()
                        );
                    }

                    if (request.getTrangThai() != null) {
                        lichLamViec.setTrangThai(
                                request.getTrangThai()
                        );
                    }

                    LichLamViec updated =
                            lichLamViecRepository.save(
                                    lichLamViec
                            );

                    return modelMapper.map(
                            updated,
                            LichLamViecResponse.class
                    );
                })
                .orElseThrow(() ->
                        new CustomResourceotFoundException(
                                "Không tìm thấy lịch làm việc với id: " + id
                        ));
    }

    @Override
    public LichLamViecResponse doiTrangThai(long id) {

        LichLamViec lichLamViec =
                lichLamViecRepository.findById(id)
                        .orElseThrow(() ->
                                new CustomResourceotFoundException(
                                        "Không tìm thấy lịch làm việc với id: " + id
                                ));

        Integer trangThai = lichLamViec.getTrangThai();

        if (trangThai == 0) {
            lichLamViec.setTrangThai(1); // Chưa vào ca -> Đang làm
        } else if (trangThai == 1) {
            lichLamViec.setTrangThai(2); // Đang làm -> Hết ca
        } else {
            throw new RuntimeException("Ca làm việc đã kết thúc");
        }

        LichLamViec updated = lichLamViecRepository.save(lichLamViec);

        return modelMapper.map(updated, LichLamViecResponse.class);
    }

}

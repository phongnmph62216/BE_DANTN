package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.NhanVienRespon;
import com.example.be_dantn.Dto.NhanVienrequest;
import com.example.be_dantn.Entity.NhanVien;
import com.example.be_dantn.Repository.NhanVienRepository;
import com.example.be_dantn.exception.CustomResourceotFoundException;
import com.example.be_dantn.exception.DuplicateDataException;
import com.example.be_dantn.sevicer.EmailService;
import com.example.be_dantn.sevicer.NhanvienService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class NhanVienServiceIMPL implements NhanvienService {
    private final NhanVienRepository nhanVienRepository;
    private final ModelMapper modelMapper;
    private final EmailService emailService;


    @Override
    public List<NhanVienRespon> findAll() {
        return nhanVienRepository.findAll()
                .stream()
                .map(nhanVien -> modelMapper.map(nhanVien,NhanVienRespon.class))
                .toList();
    }

    @Override
    public NhanVienRespon findById(long id) {
        return nhanVienRepository.findById(id)
                .map(nhanVien -> modelMapper.map(nhanVien,NhanVienRespon.class))
                .orElseThrow(() -> new CustomResourceotFoundException("nhan vien not found with id " + id));
    }

    @Override
    public NhanVienRespon add(NhanVienrequest nhanVienrequest) {
        NhanVien nhanVien = modelMapper.map(nhanVienrequest,NhanVien.class);

        if (nhanVienRepository.existsByEmail(nhanVienrequest.getEmail())) {
            throw new DuplicateDataException("Email đã tồn tại");
        }

        if (nhanVienRepository.existsBySoDienThoai(nhanVienrequest.getSoDienThoai())) {
            throw new DuplicateDataException("Số điện thoại đã tồn tại");
        }

        if (nhanVienRepository.existsByCccd(nhanVienrequest.getCccd())) {
            throw new DuplicateDataException("CCCD đã tồn tại");
        }

        nhanVien.setNgayVaoLam(LocalDate.now());
        String password = taoMatKhauNgauNhien(8);

        nhanVien.setMatKhau(password);

        if (nhanVienrequest.getMaNhanVien() != null
                && !nhanVienrequest.getMaNhanVien().trim().isEmpty()) {

            if (nhanVienRepository.existsByMaNhanVien(nhanVienrequest.getMaNhanVien())) {
                throw new RuntimeException("Mã nhân viên đã tồn tại");
            }

            nhanVien.setMaNhanVien(nhanVienrequest.getMaNhanVien().trim());

            NhanVien saved = nhanVienRepository.save(nhanVien);


            return modelMapper.map(saved, NhanVienRespon.class);
        }

        // Chưa nhập mã => lưu trước để lấy ID
        nhanVien.setMaNhanVien(null);

        NhanVien saved = nhanVienRepository.save(nhanVien);

        String maTuDong = String.format("NV%05d", saved.getId());

        saved.setMaNhanVien(maTuDong);

        saved = nhanVienRepository.save(saved);

        try {
            emailService.sendNhanVienAccount(
                    saved.getEmail(),
                    saved.getHoVaTen(),
                    password
            );
            System.out.println("Gửi mail thành công");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return modelMapper.map(saved, NhanVienRespon.class);
    }
    private String taoMatKhauNgauNhien(int doDai) {
        String kyTu = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "abcdefghijklmnopqrstuvwxyz"
                + "0123456789";;

        Random random = new Random();

        StringBuilder matKhau = new StringBuilder();

        for (int i = 0; i < doDai; i++) {
            matKhau.append(
                    kyTu.charAt(random.nextInt(kyTu.length()))
            );
        }

        return matKhau.toString();
    }

    @Override
    public NhanVienRespon update(NhanVienrequest nhanVienrequest, long id) {

        return nhanVienRepository.findById(id)
                .map(nhanVien -> {

                    if (nhanVienrequest.getMaNhanVien() != null
                            && !nhanVienrequest.getMaNhanVien().trim().isEmpty()) {
                        nhanVien.setMaNhanVien(nhanVienrequest.getMaNhanVien());
                    }

                    if (nhanVienrequest.getHoVaTen() != null) {
                        nhanVien.setHoVaTen(nhanVienrequest.getHoVaTen());
                    }

                    if (nhanVienrequest.getSoDienThoai() != null) {
                        nhanVien.setSoDienThoai(nhanVienrequest.getSoDienThoai());
                    }

                    if (nhanVienrequest.getEmail() != null) {
                        nhanVien.setEmail(nhanVienrequest.getEmail());
                    }

                    if (nhanVienrequest.getMatKhau() != null) {
                        nhanVien.setMatKhau(nhanVienrequest.getMatKhau());
                    }

                    if (nhanVienrequest.getCccd() != null) {
                        nhanVien.setCccd(nhanVienrequest.getCccd());
                    }

                    if (nhanVienrequest.getGioiTinh() != null) {
                        nhanVien.setGioiTinh(nhanVienrequest.getGioiTinh());
                    }

                    if (nhanVienrequest.getNgaySinh() != null) {
                        nhanVien.setNgaySinh(nhanVienrequest.getNgaySinh());
                    }

                    if (nhanVienrequest.getDiaChi() != null) {
                        nhanVien.setDiaChi(nhanVienrequest.getDiaChi());
                    }

                    if (nhanVienrequest.getVaiTro() != null) {
                        nhanVien.setVaiTro(nhanVienrequest.getVaiTro());
                    }

                    if (nhanVienrequest.getTrangThai() != null) {
                        nhanVien.setTrangThai(nhanVienrequest.getTrangThai());
                    }

                    if (nhanVienrequest.getNgayVaoLam() != null) {
                        nhanVien.setNgayVaoLam(nhanVienrequest.getNgayVaoLam());
                    }

                    if (nhanVienrequest.getAnhDaiDien() != null) {
                        nhanVien.setAnhDaiDien(nhanVienrequest.getAnhDaiDien());
                    }

                    nhanVien.setNgaySua(LocalDateTime.now());

                    nhanVienRepository.save(nhanVien);

                    return modelMapper.map(
                            nhanVien,
                            NhanVienRespon.class
                    );
                })
                .orElseThrow(() ->
                        new CustomResourceotFoundException(
                                "Không tìm thấy nhân viên với id: " + id
                        ));
    }

    @Override
    public NhanVienRespon doiTrangThai(long id) {

        NhanVien nhanVien = nhanVienRepository.findById(id)
                .orElseThrow(() ->
                        new CustomResourceotFoundException(
                                "Không tìm thấy nhân viên với id: " + id
                        ));

        nhanVien.setTrangThai(
                nhanVien.getTrangThai() == 1 ? 0 : 1
        );

        nhanVien.setNgaySua(LocalDateTime.now());

        NhanVien updated = nhanVienRepository.save(nhanVien);

        return modelMapper.map(
                updated,
                NhanVienRespon.class
        );
    }
}

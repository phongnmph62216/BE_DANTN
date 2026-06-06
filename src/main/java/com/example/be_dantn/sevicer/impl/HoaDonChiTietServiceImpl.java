package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.HoaDonChiTietRequest;
import com.example.be_dantn.Dto.HoaDonChiTietResponse;
import com.example.be_dantn.entity.ChiTietSanPham;
import com.example.be_dantn.entity.HoaDon;
import com.example.be_dantn.entity.HoaDonChiTiet;
import com.example.be_dantn.exception.CustomResourceotFoundException;
import com.example.be_dantn.repositoty.ChiTietSanPhamRepository;
import com.example.be_dantn.repositoty.HoaDonChiTietRepository;
import com.example.be_dantn.repositoty.HoaDonRepository;
import com.example.be_dantn.sevicer.HoaDonChiTietService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HoaDonChiTietServiceImpl implements HoaDonChiTietService {

    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final HoaDonRepository hoaDonRepository;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;

    private HoaDonChiTietResponse toResponse(HoaDonChiTiet hoaDonChiTiet) {
        HoaDonChiTietResponse response = new HoaDonChiTietResponse();

        response.setId(hoaDonChiTiet.getId());

        if (hoaDonChiTiet.getHoaDon() != null) {
            response.setIdHoaDon(hoaDonChiTiet.getHoaDon().getId());
        }

        if (hoaDonChiTiet.getChiTietSanPham() != null) {
            response.setIdChiTietSanPham(hoaDonChiTiet.getChiTietSanPham().getId());
            response.setMaChiTietSanPham(hoaDonChiTiet.getChiTietSanPham().getMaChiTietSanPham());
        }

        response.setDonGia(hoaDonChiTiet.getDonGia());
        response.setSoLuong(hoaDonChiTiet.getSoLuong());
        response.setThanhTien(hoaDonChiTiet.getThanhTien());
        response.setNgaySua(hoaDonChiTiet.getNgaySua());
        response.setNgayTao(hoaDonChiTiet.getNgayTao());
        response.setNguoiTao(hoaDonChiTiet.getNguoiTao());
        response.setNguoiSua(hoaDonChiTiet.getNguoiSua());
        response.setTrangThai(hoaDonChiTiet.getTrangThai());

        return response;
    }

    private BigDecimal getOrZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private void kiemTraHoaDonDuocCapNhat(HoaDon hoaDon) {
        if ("HOAN_THANH".equals(hoaDon.getTrangThai())) {
            throw new IllegalArgumentException("Hoa don da hoan thanh khong duoc cap nhat chi tiet");
        }

        if ("DA_HUY".equals(hoaDon.getTrangThai())) {
            throw new IllegalArgumentException("Hoa don da huy khong duoc cap nhat chi tiet");
        }
    }

    private void tinhLaiTienHoaDon(HoaDon hoaDon) {
        List<HoaDonChiTiet> chiTietList =
                hoaDonChiTietRepository.findByHoaDon_IdAndTrangThai(hoaDon.getId(), "HOAT_DONG");

        BigDecimal soTienGoc = chiTietList.stream()
                .map(HoaDonChiTiet::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        hoaDon.setSoTienGoc(soTienGoc);

        BigDecimal soTienGiam = getOrZero(hoaDon.getSoTienGiam());
        BigDecimal phiVanChuyen = getOrZero(hoaDon.getPhiVanChuyen());

        if (soTienGiam.compareTo(soTienGoc) > 0) {
            soTienGiam = soTienGoc;
            hoaDon.setSoTienGiam(soTienGiam);
        }

        BigDecimal tongTienThanhToan = soTienGoc
                .subtract(soTienGiam)
                .add(phiVanChuyen);

        if (tongTienThanhToan.compareTo(BigDecimal.ZERO) < 0) {
            tongTienThanhToan = BigDecimal.ZERO;
        }

        hoaDon.setTongTienThanhToan(tongTienThanhToan);
        hoaDon.setNgaySua(LocalDateTime.now());

        hoaDonRepository.save(hoaDon);
    }

    @Override
    public List<HoaDonChiTietResponse> findAll() {
        return hoaDonChiTietRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<HoaDonChiTietResponse> findByHoaDon(Long idHoaDon) {
        return hoaDonChiTietRepository.findByHoaDon_Id(idHoaDon)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public HoaDonChiTietResponse findById(Long id) {
        return hoaDonChiTietRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new CustomResourceotFoundException("Hoa don chi tiet voi id " + id + " khong ton tai"));
    }

    @Override
    @Transactional
    public HoaDonChiTietResponse add(HoaDonChiTietRequest request) {
        HoaDon hoaDon = hoaDonRepository.findById(request.getIdHoaDon())
                .orElseThrow(() -> new CustomResourceotFoundException("Hoa don voi id " + request.getIdHoaDon() + " khong ton tai"));

        kiemTraHoaDonDuocCapNhat(hoaDon);

        ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(request.getIdChiTietSanPham())
                .orElseThrow(() -> new CustomResourceotFoundException("Chi tiet san pham voi id " + request.getIdChiTietSanPham() + " khong ton tai"));

        if (chiTietSanPham.getSoLuongTon() == null || chiTietSanPham.getSoLuongTon() < request.getSoLuong()) {
            throw new IllegalArgumentException("So luong ton khong du");
        }

        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietRepository
                .findByHoaDon_IdAndChiTietSanPham_IdAndTrangThai(
                        request.getIdHoaDon(),
                        request.getIdChiTietSanPham(),
                        "HOAT_DONG"
                )
                .orElse(null);

        if (hoaDonChiTiet == null) {
            hoaDonChiTiet = new HoaDonChiTiet();
            hoaDonChiTiet.setHoaDon(hoaDon);
            hoaDonChiTiet.setChiTietSanPham(chiTietSanPham);
            hoaDonChiTiet.setDonGia(chiTietSanPham.getGiaBan());
            hoaDonChiTiet.setSoLuong(request.getSoLuong());
            hoaDonChiTiet.setThanhTien(chiTietSanPham.getGiaBan().multiply(BigDecimal.valueOf(request.getSoLuong())));
            hoaDonChiTiet.setNguoiTao(request.getNguoiTao());
            hoaDonChiTiet.setTrangThai("HOAT_DONG");
            hoaDonChiTiet.setNgayTao(LocalDateTime.now());
        } else {
            int soLuongMoi = hoaDonChiTiet.getSoLuong() + request.getSoLuong();
            hoaDonChiTiet.setSoLuong(soLuongMoi);
            hoaDonChiTiet.setThanhTien(hoaDonChiTiet.getDonGia().multiply(BigDecimal.valueOf(soLuongMoi)));
            hoaDonChiTiet.setNguoiSua(request.getNguoiSua());
            hoaDonChiTiet.setNgaySua(LocalDateTime.now());
        }

        chiTietSanPham.setSoLuongTon(chiTietSanPham.getSoLuongTon() - request.getSoLuong());
        chiTietSanPhamRepository.save(chiTietSanPham);

        HoaDonChiTiet saved = hoaDonChiTietRepository.save(hoaDonChiTiet);

        tinhLaiTienHoaDon(hoaDon);

        return toResponse(saved);
    }

    @Override
    @Transactional
    public HoaDonChiTietResponse update(HoaDonChiTietRequest request, Long id) {
        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("Hoa don chi tiet voi id " + id + " khong ton tai"));

        HoaDon hoaDon = hoaDonChiTiet.getHoaDon();
        kiemTraHoaDonDuocCapNhat(hoaDon);

        ChiTietSanPham chiTietSanPham = hoaDonChiTiet.getChiTietSanPham();

        int soLuongCu = hoaDonChiTiet.getSoLuong();
        int soLuongMoi = request.getSoLuong();
        int chenhlech = soLuongMoi - soLuongCu;

        if (chenhlech > 0) {
            if (chiTietSanPham.getSoLuongTon() == null || chiTietSanPham.getSoLuongTon() < chenhlech) {
                throw new IllegalArgumentException("So luong ton khong du");
            }

            chiTietSanPham.setSoLuongTon(chiTietSanPham.getSoLuongTon() - chenhlech);
        } else if (chenhlech < 0) {
            chiTietSanPham.setSoLuongTon(chiTietSanPham.getSoLuongTon() + Math.abs(chenhlech));
        }

        hoaDonChiTiet.setSoLuong(soLuongMoi);
        hoaDonChiTiet.setThanhTien(hoaDonChiTiet.getDonGia().multiply(BigDecimal.valueOf(soLuongMoi)));
        hoaDonChiTiet.setNguoiSua(request.getNguoiSua());
        hoaDonChiTiet.setNgaySua(LocalDateTime.now());

        chiTietSanPhamRepository.save(chiTietSanPham);

        HoaDonChiTiet saved = hoaDonChiTietRepository.save(hoaDonChiTiet);

        tinhLaiTienHoaDon(hoaDon);

        return toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("Hoa don chi tiet voi id " + id + " khong ton tai"));

        HoaDon hoaDon = hoaDonChiTiet.getHoaDon();
        kiemTraHoaDonDuocCapNhat(hoaDon);

        ChiTietSanPham chiTietSanPham = hoaDonChiTiet.getChiTietSanPham();
        chiTietSanPham.setSoLuongTon(chiTietSanPham.getSoLuongTon() + hoaDonChiTiet.getSoLuong());

        hoaDonChiTiet.setTrangThai("DA_XOA");
        hoaDonChiTiet.setNgaySua(LocalDateTime.now());

        chiTietSanPhamRepository.save(chiTietSanPham);
        hoaDonChiTietRepository.save(hoaDonChiTiet);

        tinhLaiTienHoaDon(hoaDon);
    }
}
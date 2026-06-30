package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.LichLamViecDTO;
import com.example.be_dantn.Entity.CaLamViec;
import com.example.be_dantn.Entity.LichLamViec;
import com.example.be_dantn.Entity.NhanVien;
import com.example.be_dantn.Repository.CaLamViecRepository;
import com.example.be_dantn.Repository.LichLamViecRepository;
import com.example.be_dantn.Repository.NhanVienRepository;
import com.example.be_dantn.exception.CustomResourceotFoundException;
import com.example.be_dantn.sevicer.LichLamViecService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LichLamViecServiceImpl implements LichLamViecService {

    private final LichLamViecRepository lichLamViecRepository;
    private final NhanVienRepository nhanVienRepository;
    private final CaLamViecRepository caLamViecRepository;

    @Override
    public List<LichLamViecDTO> findAll(Long idNhanVien, LocalDate startDate, LocalDate endDate) {
        Specification<LichLamViec> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (idNhanVien != null) {
                predicates.add(criteriaBuilder.equal(root.get("nhanVien").get("id"), idNhanVien));
            }

            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("ngayLamViec"), startDate));
            }

            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("ngayLamViec"), endDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return lichLamViecRepository.findAll(specification, Sort.by(Sort.Direction.ASC, "ngayLamViec", "id")).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LichLamViecDTO findById(Long id) {
        LichLamViec entity = lichLamViecRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("Không tìm thấy lịch làm việc với ID: " + id));
        return toDTO(entity);
    }

    @Override
    public LichLamViecDTO save(LichLamViecDTO dto) {
        NhanVien nv = nhanVienRepository.findById(dto.getIdNhanVien())
                .orElseThrow(() -> new CustomResourceotFoundException("Không tìm thấy nhân viên với ID: " + dto.getIdNhanVien()));

        CaLamViec ca = caLamViecRepository.findById(dto.getIdCaLamViec())
                .orElseThrow(() -> new CustomResourceotFoundException("Không tìm thấy ca làm việc với ID: " + dto.getIdCaLamViec()));

        // Prevent duplicate scheduling
        if (lichLamViecRepository.existsByNhanVienIdAndNgayLamViecAndCaLamViecId(dto.getIdNhanVien(), dto.getNgayLamViec(), dto.getIdCaLamViec())) {
            throw new IllegalArgumentException("Nhân viên này đã được phân ca " + ca.getTenCa() + " vào ngày " + dto.getNgayLamViec());
        }

        LichLamViec entity = new LichLamViec();
        entity.setNhanVien(nv);
        entity.setCaLamViec(ca);
        entity.setNgayLamViec(dto.getNgayLamViec());
        entity.setGhiChu(dto.getGhiChu() != null ? dto.getGhiChu().trim() : null);
        entity.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : 1);
        entity.setNguoiTaoQuanLy(dto.getNguoiTaoQuanLy());
        entity.setNguoiTao(resolveNguoi(dto.getNguoiTao()));
        entity.setNguoiSua(resolveNguoi(dto.getNguoiSua()));

        return toDTO(lichLamViecRepository.save(entity));
    }

    @Override
    public LichLamViecDTO update(Long id, LichLamViecDTO dto) {
        LichLamViec entity = lichLamViecRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("Không tìm thấy lịch làm việc với ID: " + id));

        NhanVien nv = nhanVienRepository.findById(dto.getIdNhanVien())
                .orElseThrow(() -> new CustomResourceotFoundException("Không tìm thấy nhân viên với ID: " + dto.getIdNhanVien()));

        CaLamViec ca = caLamViecRepository.findById(dto.getIdCaLamViec())
                .orElseThrow(() -> new CustomResourceotFoundException("Không tìm thấy ca làm việc với ID: " + dto.getIdCaLamViec()));

        // Prevent duplicate scheduling for other assignments
        if (lichLamViecRepository.existsByNhanVienIdAndNgayLamViecAndCaLamViecIdAndIdNot(dto.getIdNhanVien(), dto.getNgayLamViec(), dto.getIdCaLamViec(), id)) {
            throw new IllegalArgumentException("Nhân viên này đã được phân ca " + ca.getTenCa() + " vào ngày " + dto.getNgayLamViec());
        }

        entity.setNhanVien(nv);
        entity.setCaLamViec(ca);
        entity.setNgayLamViec(dto.getNgayLamViec());
        entity.setGhiChu(dto.getGhiChu() != null ? dto.getGhiChu().trim() : null);
        if (dto.getTrangThai() != null) {
            entity.setTrangThai(dto.getTrangThai());
        }
        if (StringUtils.hasText(dto.getNguoiTaoQuanLy())) {
            entity.setNguoiTaoQuanLy(dto.getNguoiTaoQuanLy());
        }
        entity.setNguoiSua(resolveNguoi(dto.getNguoiSua()));
        entity.setNgaySua(LocalDateTime.now());

        return toDTO(lichLamViecRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        LichLamViec entity = lichLamViecRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("Không tìm thấy lịch làm việc với ID: " + id));
        lichLamViecRepository.delete(entity);
    }

    private String resolveNguoi(String name) {
        return StringUtils.hasText(name) ? name.trim() : "system";
    }

    private LichLamViecDTO toDTO(LichLamViec entity) {
        return LichLamViecDTO.builder()
                .id(entity.getId())
                .idNhanVien(entity.getNhanVien().getId())
                .idCaLamViec(entity.getCaLamViec().getId())
                .ngayLamViec(entity.getNgayLamViec())
                .ghiChu(entity.getGhiChu())
                .trangThai(entity.getTrangThai())
                .nguoiTaoQuanLy(entity.getNguoiTaoQuanLy())
                .ngayTao(entity.getNgayTao())
                .ngaySua(entity.getNgaySua())
                .nguoiTao(entity.getNguoiTao())
                .nguoiSua(entity.getNguoiSua())
                .build();
    }
}

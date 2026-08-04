package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.CaLamViecDTO;
import com.example.be_dantn.Entity.CaLamViec;
import com.example.be_dantn.Repository.CaLamViecRepository;
import com.example.be_dantn.exception.CustomResourceotFoundException;
import com.example.be_dantn.sevicer.CaLamViecService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CaLamViecServiceImpl implements CaLamViecService {

    private final CaLamViecRepository caLamViecRepository;
    private final com.example.be_dantn.Config.CodeGenerator codeGenerator;

    @Override
    public List<CaLamViecDTO> findAll(String keyword, LocalTime startTime, LocalTime endTime, Integer trangThai) {
        Specification<CaLamViec> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                String likeKeyword = "%" + keyword.trim().toLowerCase() + "%";
                Predicate keywordPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("maCa")), likeKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("tenCa")), likeKeyword)
                );
                predicates.add(keywordPredicate);
            }

            if (startTime != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("gioBatDau"), startTime));
            }

            if (endTime != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("gioKetThuc"), endTime));
            }

            if (trangThai != null) {
                predicates.add(criteriaBuilder.equal(root.get("trangThai"), trangThai));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return caLamViecRepository.findAll(specification, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "id")).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CaLamViecDTO findById(Long id) {
        CaLamViec entity = caLamViecRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("Không tìm thấy ca làm việc với ID: " + id));
        return toDTO(entity);
    }

    @Override
    public CaLamViecDTO save(CaLamViecDTO dto) {
        CaLamViec entity = new CaLamViec();
        entity.setTenCa(dto.getTen().trim());
        entity.setGioBatDau(dto.getGioBatDau());
        entity.setGioKetThuc(dto.getGioKetThuc());
        entity.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : 1);
        entity.setNguoiTao(resolveNguoi(dto.getNguoiTao()));
        entity.setNguoiSua(resolveNguoi(dto.getNguoiSua()));

        String code = StringUtils.hasText(dto.getMa()) ? dto.getMa().trim() : null;
        if (code != null) {
            if (caLamViecRepository.existsByMaCa(code)) {
                throw new IllegalArgumentException("Mã ca đã tồn tại: " + code);
            }
            entity.setMaCa(code);
        } else {
            entity.setMaCa(codeGenerator.generateCode("ca_lam_viec", "ma_ca", "CA"));
        }

        return toDTO(caLamViecRepository.save(entity));
    }

    @Override
    public CaLamViecDTO update(Long id, CaLamViecDTO dto) {
        CaLamViec entity = caLamViecRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("Không tìm thấy ca làm việc với ID: " + id));

        String code = StringUtils.hasText(dto.getMa()) ? dto.getMa().trim() : entity.getMaCa();
        if (!code.equals(entity.getMaCa()) && caLamViecRepository.existsByMaCaAndIdNot(code, id)) {
            throw new IllegalArgumentException("Mã ca đã tồn tại: " + code);
        }

        entity.setMaCa(code);
        entity.setTenCa(dto.getTen().trim());
        entity.setGioBatDau(dto.getGioBatDau());
        entity.setGioKetThuc(dto.getGioKetThuc());
        if (dto.getTrangThai() != null) {
            entity.setTrangThai(dto.getTrangThai());
        }
        entity.setNguoiSua(resolveNguoi(dto.getNguoiSua()));
        entity.setNgaySua(LocalDateTime.now());

        return toDTO(caLamViecRepository.save(entity));
    }

    @Override
    public CaLamViecDTO toggleStatus(Long id) {
        CaLamViec entity = caLamViecRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("Không tìm thấy ca làm việc với ID: " + id));

        entity.setTrangThai(entity.getTrangThai() != null && entity.getTrangThai() == 1 ? 0 : 1);
        entity.setNgaySua(LocalDateTime.now());
        return toDTO(caLamViecRepository.save(entity));
    }

    private String resolveNguoi(String name) {
        return StringUtils.hasText(name) ? name.trim() : "system";
    }

    private CaLamViecDTO toDTO(CaLamViec entity) {
        return CaLamViecDTO.builder()
                .id(entity.getId())
                .ma(entity.getMaCa())
                .ten(entity.getTenCa())
                .gioBatDau(entity.getGioBatDau())
                .gioKetThuc(entity.getGioKetThuc())
                .trangThai(entity.getTrangThai())
                .ngayTao(entity.getNgayTao())
                .ngaySua(entity.getNgaySua())
                .nguoiTao(entity.getNguoiTao())
                .nguoiSua(entity.getNguoiSua())
                .build();
    }
}

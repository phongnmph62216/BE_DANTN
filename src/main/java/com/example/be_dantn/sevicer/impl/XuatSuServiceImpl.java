package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.XuatSuDTO;
import com.example.be_dantn.Entity.XuatSu;
import com.example.be_dantn.Repository.XuatSuRepository;
import com.example.be_dantn.exception.CustomResourceotFoundException;
import com.example.be_dantn.sevicer.XuatSuService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class XuatSuServiceImpl implements XuatSuService {

    private static final String MA_PREFIX = "TTXX";

    private final XuatSuRepository xuatSuRepository;

    @Override
    public Page<XuatSuDTO> findAll(Pageable pageable, String keyword, Integer trangThai) {
        Specification<XuatSu> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                String likeKeyword = "%" + keyword.trim().toLowerCase() + "%";
                Predicate keywordPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("maXuatSu")), likeKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("tenXuatSu")), likeKeyword)
                );
                predicates.add(keywordPredicate);
            }

            if (trangThai != null) {
                predicates.add(criteriaBuilder.equal(root.get("trangThai"), trangThai));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return xuatSuRepository.findAll(specification, pageable).map(this::toDTO);
    }

    @Override
    public XuatSuDTO findById(Long id) {
        XuatSu entity = xuatSuRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("XuatSu not found with id: " + id));
        return toDTO(entity);
    }

    @Override
    public XuatSuDTO save(XuatSuDTO xuatSuDTO) {
        XuatSu entity = new XuatSu();
        applyUpsertFields(entity, xuatSuDTO, true);
        entity.setMaXuatSu(resolveMaForCreate(xuatSuDTO.getMa()));
        entity.setNguoiTao(resolveNguoi(xuatSuDTO.getNguoiTao()));
        entity.setNguoiSua(resolveNguoi(xuatSuDTO.getNguoiSua()));
        entity.setTrangThai(xuatSuDTO.getTrangThai() != null ? xuatSuDTO.getTrangThai() : 1);

        return toDTO(xuatSuRepository.save(entity));
    }

    @Override
    public XuatSuDTO update(Long id, XuatSuDTO xuatSuDTO) {
        XuatSu entity = xuatSuRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("XuatSu not found with id: " + id));

        String updatedMa = StringUtils.hasText(xuatSuDTO.getMa()) ? xuatSuDTO.getMa().trim() : entity.getMaXuatSu();
        if (!updatedMa.equals(entity.getMaXuatSu()) && xuatSuRepository.existsByMaXuatSuAndIdNot(updatedMa, id)) {
            throw new IllegalArgumentException("Mã xuất sứ đã tồn tại: " + updatedMa);
        }

        entity.setMaXuatSu(updatedMa);
        applyUpsertFields(entity, xuatSuDTO, false);

        if (StringUtils.hasText(xuatSuDTO.getNguoiSua())) {
            entity.setNguoiSua(xuatSuDTO.getNguoiSua().trim());
        }
        if (xuatSuDTO.getTrangThai() != null) {
            entity.setTrangThai(xuatSuDTO.getTrangThai());
        }

        entity.setNgaySua(LocalDateTime.now());
        return toDTO(xuatSuRepository.save(entity));
    }

    @Override
    public XuatSuDTO toggleStatus(Long id) {
        XuatSu entity = xuatSuRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("XuatSu not found with id: " + id));

        entity.setTrangThai(entity.getTrangThai() != null && entity.getTrangThai() == 1 ? 0 : 1);
        entity.setNgaySua(LocalDateTime.now());
        return toDTO(xuatSuRepository.save(entity));
    }

    private void applyUpsertFields(XuatSu entity, XuatSuDTO dto, boolean isCreate) {
        if (StringUtils.hasText(dto.getTen())) {
            entity.setTenXuatSu(dto.getTen().trim());
        } else if (isCreate) {
            throw new IllegalArgumentException("Tên xuất sứ không được để trống");
        }

        if (StringUtils.hasText(dto.getNguoiTao()) && isCreate) {
            entity.setNguoiTao(dto.getNguoiTao().trim());
        }

        if (StringUtils.hasText(dto.getNguoiSua()) && !isCreate) {
            entity.setNguoiSua(dto.getNguoiSua().trim());
        }
    }

    private String resolveMaForCreate(String ma) {
        if (StringUtils.hasText(ma)) {
            String normalizedMa = ma.trim();
            if (xuatSuRepository.existsByMaXuatSu(normalizedMa)) {
                throw new IllegalArgumentException("Mã xuất sứ đã tồn tại: " + normalizedMa);
            }
            return normalizedMa;
        }

        String generatedMa;
        do {
            generatedMa = MA_PREFIX + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        } while (xuatSuRepository.existsByMaXuatSu(generatedMa));

        return generatedMa;
    }

    private String resolveNguoi(String nguoi) {
        return StringUtils.hasText(nguoi) ? nguoi.trim() : "system";
    }

    private XuatSuDTO toDTO(XuatSu entity) {
        return XuatSuDTO.builder()
                .id(entity.getId())
                .ma(entity.getMaXuatSu())
                .ten(entity.getTenXuatSu())
                .trangThai(entity.getTrangThai())
                .ngayTao(entity.getNgayTao())
                .ngaySua(entity.getNgaySua())
                .nguoiTao(entity.getNguoiTao())
                .nguoiSua(entity.getNguoiSua())
                .build();
    }
}


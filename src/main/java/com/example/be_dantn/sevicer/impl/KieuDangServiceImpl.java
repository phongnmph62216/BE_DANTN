package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.KieuDangDTO;
import com.example.be_dantn.Entity.KieuDang;
import com.example.be_dantn.Repository.KieuDangRepository;
import com.example.be_dantn.exception.CustomResourceotFoundException;
import com.example.be_dantn.sevicer.KieuDangService;
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
public class KieuDangServiceImpl implements KieuDangService {

    private static final String MA_PREFIX = "TTKD";

    private final KieuDangRepository kieuDangRepository;
    private final com.example.be_dantn.Config.CodeGenerator codeGenerator;

    @Override
    public Page<KieuDangDTO> findAll(Pageable pageable, String keyword, Integer trangThai) {
        Specification<KieuDang> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                String likeKeyword = "%" + keyword.trim().toLowerCase() + "%";
                Predicate keywordPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("maKieuDang")), likeKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("tenKieuDang")), likeKeyword)
                );
                predicates.add(keywordPredicate);
            }

            if (trangThai != null) {
                predicates.add(criteriaBuilder.equal(root.get("trangThai"), trangThai));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return kieuDangRepository.findAll(specification, pageable).map(this::toDTO);
    }

    @Override
    public KieuDangDTO findById(Long id) {
        KieuDang entity = kieuDangRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("KieuDang not found with id: " + id));
        return toDTO(entity);
    }

    @Override
    public KieuDangDTO save(KieuDangDTO kieuDangDTO) {
        KieuDang entity = new KieuDang();
        applyUpsertFields(entity, kieuDangDTO, true);
        entity.setMaKieuDang(resolveMaForCreate(kieuDangDTO.getMa()));
        entity.setNguoiTao(resolveNguoi(kieuDangDTO.getNguoiTao()));
        entity.setNguoiSua(resolveNguoi(kieuDangDTO.getNguoiSua()));
        entity.setTrangThai(kieuDangDTO.getTrangThai() != null ? kieuDangDTO.getTrangThai() : 1);

        return toDTO(kieuDangRepository.save(entity));
    }

    @Override
    public KieuDangDTO update(Long id, KieuDangDTO kieuDangDTO) {
        KieuDang entity = kieuDangRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("KieuDang not found with id: " + id));

        String updatedMa = StringUtils.hasText(kieuDangDTO.getMa()) ? kieuDangDTO.getMa().trim() : entity.getMaKieuDang();
        if (!updatedMa.equals(entity.getMaKieuDang()) && kieuDangRepository.existsByMaKieuDangAndIdNot(updatedMa, id)) {
            throw new IllegalArgumentException("Ma kieu dang da ton tai: " + updatedMa);
        }

        entity.setMaKieuDang(updatedMa);
        applyUpsertFields(entity, kieuDangDTO, false);

        if (StringUtils.hasText(kieuDangDTO.getNguoiSua())) {
            entity.setNguoiSua(kieuDangDTO.getNguoiSua().trim());
        }
        if (kieuDangDTO.getTrangThai() != null) {
            entity.setTrangThai(kieuDangDTO.getTrangThai());
        }

        entity.setNgaySua(LocalDateTime.now());
        return toDTO(kieuDangRepository.save(entity));
    }

    @Override
    public KieuDangDTO toggleStatus(Long id) {
        KieuDang entity = kieuDangRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("KieuDang not found with id: " + id));

        entity.setTrangThai(entity.getTrangThai() != null && entity.getTrangThai() == 1 ? 0 : 1);
        entity.setNgaySua(LocalDateTime.now());
        return toDTO(kieuDangRepository.save(entity));
    }

    private void applyUpsertFields(KieuDang entity, KieuDangDTO dto, boolean isCreate) {
        if (StringUtils.hasText(dto.getTen())) {
            entity.setTenKieuDang(dto.getTen().trim());
        } else if (isCreate) {
            throw new IllegalArgumentException("Ten kieu dang khong duoc de trong");
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
            if (kieuDangRepository.existsByMaKieuDang(normalizedMa)) {
                throw new IllegalArgumentException("Ma kieu dang da ton tai: " + normalizedMa);
            }
            return normalizedMa;
        }
        return codeGenerator.generateCode("kieu_dang", "ma_kieu_dang", "KD");
    }

    private String resolveNguoi(String nguoi) {
        return StringUtils.hasText(nguoi) ? nguoi.trim() : "system";
    }

    private KieuDangDTO toDTO(KieuDang entity) {
        return KieuDangDTO.builder()
                .id(entity.getId())
                .ma(entity.getMaKieuDang())
                .ten(entity.getTenKieuDang())
                .trangThai(entity.getTrangThai())
                .ngayTao(entity.getNgayTao())
                .ngaySua(entity.getNgaySua())
                .nguoiTao(entity.getNguoiTao())
                .nguoiSua(entity.getNguoiSua())
                .build();
    }
}


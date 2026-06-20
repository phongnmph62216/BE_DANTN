package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.KichThuocDTO;
import com.example.be_dantn.Entity.KichThuoc;
import com.example.be_dantn.Repository.KichThuocRepository;
import com.example.be_dantn.exception.CustomResourceotFoundException;
import com.example.be_dantn.sevicer.KichThuocService;
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
public class KichThuocServiceImpl implements KichThuocService {

    private static final String MA_PREFIX = "TTKT";

    private final KichThuocRepository kichThuocRepository;
    private final com.example.be_dantn.Config.CodeGenerator codeGenerator;

    @Override
    public Page<KichThuocDTO> findAll(Pageable pageable, String keyword, Integer trangThai) {
        Specification<KichThuoc> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                String likeKeyword = "%" + keyword.trim().toLowerCase() + "%";
                Predicate keywordPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("maKichThuoc")), likeKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("tenKichThuoc")), likeKeyword)
                );
                predicates.add(keywordPredicate);
            }

            if (trangThai != null) {
                predicates.add(criteriaBuilder.equal(root.get("trangThai"), trangThai));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return kichThuocRepository.findAll(specification, pageable).map(this::toDTO);
    }

    @Override
    public KichThuocDTO findById(Long id) {
        KichThuoc entity = kichThuocRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("KichThuoc not found with id: " + id));
        return toDTO(entity);
    }

    @Override
    public KichThuocDTO save(KichThuocDTO kichThuocDTO) {
        KichThuoc entity = new KichThuoc();
        applyUpsertFields(entity, kichThuocDTO, true);
        entity.setMaKichThuoc(resolveMaForCreate(kichThuocDTO.getMa()));
        entity.setNguoiTao(resolveNguoi(kichThuocDTO.getNguoiTao()));
        entity.setNguoiSua(resolveNguoi(kichThuocDTO.getNguoiSua()));
        entity.setTrangThai(kichThuocDTO.getTrangThai() != null ? kichThuocDTO.getTrangThai() : 1);

        return toDTO(kichThuocRepository.save(entity));
    }

    @Override
    public KichThuocDTO update(Long id, KichThuocDTO kichThuocDTO) {
        KichThuoc entity = kichThuocRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("KichThuoc not found with id: " + id));

        String updatedMa = StringUtils.hasText(kichThuocDTO.getMa()) ? kichThuocDTO.getMa().trim() : entity.getMaKichThuoc();
        if (!updatedMa.equals(entity.getMaKichThuoc()) && kichThuocRepository.existsByMaKichThuocAndIdNot(updatedMa, id)) {
            throw new IllegalArgumentException("Ma kich thuoc da ton tai: " + updatedMa);
        }

        entity.setMaKichThuoc(updatedMa);
        applyUpsertFields(entity, kichThuocDTO, false);

        if (StringUtils.hasText(kichThuocDTO.getNguoiSua())) {
            entity.setNguoiSua(kichThuocDTO.getNguoiSua().trim());
        }
        if (kichThuocDTO.getTrangThai() != null) {
            entity.setTrangThai(kichThuocDTO.getTrangThai());
        }

        entity.setNgaySua(LocalDateTime.now());
        return toDTO(kichThuocRepository.save(entity));
    }

    @Override
    public KichThuocDTO toggleStatus(Long id) {
        KichThuoc entity = kichThuocRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("KichThuoc not found with id: " + id));

        entity.setTrangThai(entity.getTrangThai() != null && entity.getTrangThai() == 1 ? 0 : 1);
        entity.setNgaySua(LocalDateTime.now());
        return toDTO(kichThuocRepository.save(entity));
    }

    private void applyUpsertFields(KichThuoc entity, KichThuocDTO dto, boolean isCreate) {
        if (StringUtils.hasText(dto.getTen())) {
            entity.setTenKichThuoc(dto.getTen().trim());
        } else if (isCreate) {
            throw new IllegalArgumentException("Ten kich thuoc khong duoc de trong");
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
            if (kichThuocRepository.existsByMaKichThuoc(normalizedMa)) {
                throw new IllegalArgumentException("Ma kich thuoc da ton tai: " + normalizedMa);
            }
            return normalizedMa;
        }
        return codeGenerator.generateCode("kich_thuoc", "ma_kich_thuoc", "KT");
    }

    private String resolveNguoi(String nguoi) {
        return StringUtils.hasText(nguoi) ? nguoi.trim() : "system";
    }

    private KichThuocDTO toDTO(KichThuoc entity) {
        return KichThuocDTO.builder()
                .id(entity.getId())
                .ma(entity.getMaKichThuoc())
                .ten(entity.getTenKichThuoc())
                .trangThai(entity.getTrangThai())
                .ngayTao(entity.getNgayTao())
                .ngaySua(entity.getNgaySua())
                .nguoiTao(entity.getNguoiTao())
                .nguoiSua(entity.getNguoiSua())
                .build();
    }
}


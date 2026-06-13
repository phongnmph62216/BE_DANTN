package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.CoAoDTO;
import com.example.be_dantn.Entity.CoAo;
import com.example.be_dantn.Repository.CoAoRepository;
import com.example.be_dantn.exception.CustomResourceotFoundException;
import com.example.be_dantn.sevicer.CoAoService;
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
public class CoAoServiceImpl implements CoAoService {

    private static final String MA_PREFIX = "TTCA";

    private final CoAoRepository coAoRepository;

    @Override
    public Page<CoAoDTO> findAll(Pageable pageable, String keyword, Integer trangThai) {
        Specification<CoAo> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                String likeKeyword = "%" + keyword.trim().toLowerCase() + "%";
                Predicate keywordPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("maCoAo")), likeKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("tenCoAo")), likeKeyword)
                );
                predicates.add(keywordPredicate);
            }

            if (trangThai != null) {
                predicates.add(criteriaBuilder.equal(root.get("trangThai"), trangThai));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return coAoRepository.findAll(specification, pageable).map(this::toDTO);
    }

    @Override
    public CoAoDTO findById(Long id) {
        CoAo entity = coAoRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("CoAo not found with id: " + id));
        return toDTO(entity);
    }

    @Override
    public CoAoDTO save(CoAoDTO coAoDTO) {
        CoAo entity = new CoAo();
        applyUpsertFields(entity, coAoDTO, true);
        entity.setMaCoAo(resolveMaForCreate(coAoDTO.getMa()));
        entity.setNguoiTao(resolveNguoi(coAoDTO.getNguoiTao()));
        entity.setNguoiSua(resolveNguoi(coAoDTO.getNguoiSua()));
        entity.setTrangThai(coAoDTO.getTrangThai() != null ? coAoDTO.getTrangThai() : 1);

        return toDTO(coAoRepository.save(entity));
    }

    @Override
    public CoAoDTO update(Long id, CoAoDTO coAoDTO) {
        CoAo entity = coAoRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("CoAo not found with id: " + id));

        String updatedMa = StringUtils.hasText(coAoDTO.getMa()) ? coAoDTO.getMa().trim() : entity.getMaCoAo();
        if (!updatedMa.equals(entity.getMaCoAo()) && coAoRepository.existsByMaCoAoAndIdNot(updatedMa, id)) {
            throw new IllegalArgumentException("Ma co ao da ton tai: " + updatedMa);
        }

        entity.setMaCoAo(updatedMa);
        applyUpsertFields(entity, coAoDTO, false);

        if (StringUtils.hasText(coAoDTO.getNguoiSua())) {
            entity.setNguoiSua(coAoDTO.getNguoiSua().trim());
        }
        if (coAoDTO.getTrangThai() != null) {
            entity.setTrangThai(coAoDTO.getTrangThai());
        }

        entity.setNgaySua(LocalDateTime.now());
        return toDTO(coAoRepository.save(entity));
    }

    @Override
    public CoAoDTO toggleStatus(Long id) {
        CoAo entity = coAoRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("CoAo not found with id: " + id));

        entity.setTrangThai(entity.getTrangThai() != null && entity.getTrangThai() == 1 ? 0 : 1);
        entity.setNgaySua(LocalDateTime.now());
        return toDTO(coAoRepository.save(entity));
    }

    private void applyUpsertFields(CoAo entity, CoAoDTO dto, boolean isCreate) {
        if (StringUtils.hasText(dto.getTen())) {
            entity.setTenCoAo(dto.getTen().trim());
        } else if (isCreate) {
            throw new IllegalArgumentException("Ten co ao khong duoc de trong");
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
            if (coAoRepository.existsByMaCoAo(normalizedMa)) {
                throw new IllegalArgumentException("Ma co ao da ton tai: " + normalizedMa);
            }
            return normalizedMa;
        }

        String generatedMa;
        do {
            generatedMa = MA_PREFIX + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        } while (coAoRepository.existsByMaCoAo(generatedMa));

        return generatedMa;
    }

    private String resolveNguoi(String nguoi) {
        return StringUtils.hasText(nguoi) ? nguoi.trim() : "system";
    }

    private CoAoDTO toDTO(CoAo entity) {
        return CoAoDTO.builder()
                .id(entity.getId())
                .ma(entity.getMaCoAo())
                .ten(entity.getTenCoAo())
                .trangThai(entity.getTrangThai())
                .ngayTao(entity.getNgayTao())
                .ngaySua(entity.getNgaySua())
                .nguoiTao(entity.getNguoiTao())
                .nguoiSua(entity.getNguoiSua())
                .build();
    }
}


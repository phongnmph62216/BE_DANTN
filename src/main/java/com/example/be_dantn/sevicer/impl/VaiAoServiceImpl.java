package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.VaiAoDTO;
import com.example.be_dantn.Entity.VaiAo;
import com.example.be_dantn.Repository.VaiAoRepository;
import com.example.be_dantn.exception.CustomResourceotFoundException;
import com.example.be_dantn.sevicer.VaiAoService;
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
public class VaiAoServiceImpl implements VaiAoService {

    private static final String MA_PREFIX = "TTVA";

    private final VaiAoRepository vaiAoRepository;
    private final com.example.be_dantn.Config.CodeGenerator codeGenerator;

    @Override
    public Page<VaiAoDTO> findAll(Pageable pageable, String keyword, Integer trangThai) {
        Specification<VaiAo> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                String likeKeyword = "%" + keyword.trim().toLowerCase() + "%";
                Predicate keywordPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("maVaiAo")), likeKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("tenVaiAo")), likeKeyword)
                );
                predicates.add(keywordPredicate);
            }

            if (trangThai != null) {
                predicates.add(criteriaBuilder.equal(root.get("trangThai"), trangThai));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return vaiAoRepository.findAll(specification, pageable).map(this::toDTO);
    }

    @Override
    public VaiAoDTO findById(Long id) {
        VaiAo entity = vaiAoRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("VaiAo not found with id: " + id));
        return toDTO(entity);
    }

    @Override
    public VaiAoDTO save(VaiAoDTO vaiAoDTO) {
        VaiAo entity = new VaiAo();
        applyUpsertFields(entity, vaiAoDTO, true);
        entity.setMaVaiAo(resolveMaForCreate(vaiAoDTO.getMa()));
        entity.setNguoiTao(resolveNguoi(vaiAoDTO.getNguoiTao()));
        entity.setNguoiSua(resolveNguoi(vaiAoDTO.getNguoiSua()));
        entity.setTrangThai(vaiAoDTO.getTrangThai() != null ? vaiAoDTO.getTrangThai() : 1);

        return toDTO(vaiAoRepository.save(entity));
    }

    @Override
    public VaiAoDTO update(Long id, VaiAoDTO vaiAoDTO) {
        VaiAo entity = vaiAoRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("VaiAo not found with id: " + id));

        String updatedMa = StringUtils.hasText(vaiAoDTO.getMa()) ? vaiAoDTO.getMa().trim() : entity.getMaVaiAo();
        if (!updatedMa.equals(entity.getMaVaiAo()) && vaiAoRepository.existsByMaVaiAoAndIdNot(updatedMa, id)) {
            throw new IllegalArgumentException("Ma vai ao da ton tai: " + updatedMa);
        }

        entity.setMaVaiAo(updatedMa);
        applyUpsertFields(entity, vaiAoDTO, false);

        if (StringUtils.hasText(vaiAoDTO.getNguoiSua())) {
            entity.setNguoiSua(vaiAoDTO.getNguoiSua().trim());
        }
        if (vaiAoDTO.getTrangThai() != null) {
            entity.setTrangThai(vaiAoDTO.getTrangThai());
        }

        entity.setNgaySua(LocalDateTime.now());
        return toDTO(vaiAoRepository.save(entity));
    }

    @Override
    public VaiAoDTO toggleStatus(Long id) {
        VaiAo entity = vaiAoRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("VaiAo not found with id: " + id));

        entity.setTrangThai(entity.getTrangThai() != null && entity.getTrangThai() == 1 ? 0 : 1);
        entity.setNgaySua(LocalDateTime.now());
        return toDTO(vaiAoRepository.save(entity));
    }

    private void applyUpsertFields(VaiAo entity, VaiAoDTO dto, boolean isCreate) {
        if (StringUtils.hasText(dto.getTen())) {
            entity.setTenVaiAo(dto.getTen().trim());
        } else if (isCreate) {
            throw new IllegalArgumentException("Ten vai ao khong duoc de trong");
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
            if (vaiAoRepository.existsByMaVaiAo(normalizedMa)) {
                throw new IllegalArgumentException("Ma vai ao da ton tai: " + normalizedMa);
            }
            return normalizedMa;
        }
        return codeGenerator.generateCode("vai_ao", "ma_vai_ao", "VA");
    }

    private String resolveNguoi(String nguoi) {
        return StringUtils.hasText(nguoi) ? nguoi.trim() : "system";
    }

    private VaiAoDTO toDTO(VaiAo entity) {
        return VaiAoDTO.builder()
                .id(entity.getId())
                .ma(entity.getMaVaiAo())
                .ten(entity.getTenVaiAo())
                .trangThai(entity.getTrangThai())
                .ngayTao(entity.getNgayTao())
                .ngaySua(entity.getNgaySua())
                .nguoiTao(entity.getNguoiTao())
                .nguoiSua(entity.getNguoiSua())
                .build();
    }
}


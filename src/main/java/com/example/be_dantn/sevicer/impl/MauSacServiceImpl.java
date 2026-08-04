package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.MauSacDTO;
import com.example.be_dantn.Entity.MauSac;
import com.example.be_dantn.Repository.MauSacRepository;
import com.example.be_dantn.exception.CustomResourceotFoundException;
import com.example.be_dantn.sevicer.MauSacService;
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
public class MauSacServiceImpl implements MauSacService {

    private static final String MA_PREFIX = "TTMS";

    private final MauSacRepository mauSacRepository;
    private final com.example.be_dantn.Config.CodeGenerator codeGenerator;

    @Override
    public Page<MauSacDTO> findAll(Pageable pageable, String keyword, Integer trangThai) {
        Specification<MauSac> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                String likeKeyword = "%" + keyword.trim().toLowerCase() + "%";
                Predicate keywordPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("maMauSac")), likeKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("tenMauSac")), likeKeyword)
                );
                predicates.add(keywordPredicate);
            }

            if (trangThai != null) {
                predicates.add(criteriaBuilder.equal(root.get("trangThai"), trangThai));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return mauSacRepository.findAll(specification, pageable).map(this::toDTO);
    }

    @Override
    public MauSacDTO findById(Long id) {
        MauSac entity = mauSacRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("MauSac not found with id: " + id));
        return toDTO(entity);
    }

    @Override
    public MauSacDTO save(MauSacDTO mauSacDTO) {
        MauSac entity = new MauSac();
        applyUpsertFields(entity, mauSacDTO, true);
        entity.setMaMauSac(resolveMaForCreate(mauSacDTO.getMa()));
        entity.setNguoiTao(resolveNguoi(mauSacDTO.getNguoiTao()));
        entity.setNguoiSua(resolveNguoi(mauSacDTO.getNguoiSua()));
        entity.setTrangThai(mauSacDTO.getTrangThai() != null ? mauSacDTO.getTrangThai() : 1);

        return toDTO(mauSacRepository.save(entity));
    }

    @Override
    public MauSacDTO update(Long id, MauSacDTO mauSacDTO) {
        MauSac entity = mauSacRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("MauSac not found with id: " + id));

        String updatedMa = StringUtils.hasText(mauSacDTO.getMa()) ? mauSacDTO.getMa().trim() : entity.getMaMauSac();
        if (!updatedMa.equals(entity.getMaMauSac()) && mauSacRepository.existsByMaMauSacAndIdNot(updatedMa, id)) {
            throw new IllegalArgumentException("Ma mau sac da ton tai: " + updatedMa);
        }

        entity.setMaMauSac(updatedMa);
        applyUpsertFields(entity, mauSacDTO, false);

        if (StringUtils.hasText(mauSacDTO.getNguoiSua())) {
            entity.setNguoiSua(mauSacDTO.getNguoiSua().trim());
        }
        if (mauSacDTO.getTrangThai() != null) {
            entity.setTrangThai(mauSacDTO.getTrangThai());
        }

        entity.setNgaySua(LocalDateTime.now());
        return toDTO(mauSacRepository.save(entity));
    }

    @Override
    public MauSacDTO toggleStatus(Long id) {
        MauSac entity = mauSacRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("MauSac not found with id: " + id));

        entity.setTrangThai(entity.getTrangThai() != null && entity.getTrangThai() == 1 ? 0 : 1);
        entity.setNgaySua(LocalDateTime.now());
        return toDTO(mauSacRepository.save(entity));
    }

    private void applyUpsertFields(MauSac entity, MauSacDTO dto, boolean isCreate) {
        if (StringUtils.hasText(dto.getTen())) {
            entity.setTenMauSac(dto.getTen().trim());
        } else if (isCreate) {
            throw new IllegalArgumentException("Ten mau sac khong duoc de trong");
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
            if (mauSacRepository.existsByMaMauSac(normalizedMa)) {
                throw new IllegalArgumentException("Ma mau sac da ton tai: " + normalizedMa);
            }
            return normalizedMa;
        }
        return codeGenerator.generateCode("mau_sac", "ma_mau_sac", "MS");
    }

    private String resolveNguoi(String nguoi) {
        return StringUtils.hasText(nguoi) ? nguoi.trim() : "system";
    }

    private MauSacDTO toDTO(MauSac entity) {
        return MauSacDTO.builder()
                .id(entity.getId())
                .ma(entity.getMaMauSac())
                .ten(entity.getTenMauSac())
                .trangThai(entity.getTrangThai())
                .ngayTao(entity.getNgayTao())
                .ngaySua(entity.getNgaySua())
                .nguoiTao(entity.getNguoiTao())
                .nguoiSua(entity.getNguoiSua())
                .build();
    }
}


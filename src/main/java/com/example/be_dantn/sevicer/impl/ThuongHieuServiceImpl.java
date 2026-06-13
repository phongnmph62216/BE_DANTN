package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.ThuongHieuDTO;
import com.example.be_dantn.Entity.ThuongHieu;
import com.example.be_dantn.Repository.ThuongHieuRepository;
import com.example.be_dantn.exception.CustomResourceotFoundException;
import com.example.be_dantn.sevicer.ThuongHieuService;
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
public class ThuongHieuServiceImpl implements ThuongHieuService {

    private static final String MA_PREFIX = "TTTH";

    private final ThuongHieuRepository thuongHieuRepository;

    @Override
    public Page<ThuongHieuDTO> findAll(Pageable pageable, String keyword, Integer trangThai) {
        Specification<ThuongHieu> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                String likeKeyword = "%" + keyword.trim().toLowerCase() + "%";
                Predicate keywordPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("maThuongHieu")), likeKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("tenThuongHieu")), likeKeyword)
                );
                predicates.add(keywordPredicate);
            }

            if (trangThai != null) {
                predicates.add(criteriaBuilder.equal(root.get("trangThai"), trangThai));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return thuongHieuRepository.findAll(specification, pageable).map(this::toDTO);
    }

    @Override
    public ThuongHieuDTO findById(Long id) {
        ThuongHieu entity = thuongHieuRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("ThuongHieu not found with id: " + id));
        return toDTO(entity);
    }

    @Override
    public ThuongHieuDTO save(ThuongHieuDTO thuongHieuDTO) {
        ThuongHieu entity = new ThuongHieu();
        applyUpsertFields(entity, thuongHieuDTO, true);
        entity.setMaThuongHieu(resolveMaForCreate(thuongHieuDTO.getMa()));
        entity.setNguoiTao(resolveNguoi(thuongHieuDTO.getNguoiTao()));
        entity.setNguoiSua(resolveNguoi(thuongHieuDTO.getNguoiSua()));
        entity.setTrangThai(thuongHieuDTO.getTrangThai() != null ? thuongHieuDTO.getTrangThai() : 1);

        return toDTO(thuongHieuRepository.save(entity));
    }

    @Override
    public ThuongHieuDTO update(Long id, ThuongHieuDTO thuongHieuDTO) {
        ThuongHieu entity = thuongHieuRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("ThuongHieu not found with id: " + id));

        String updatedMa = StringUtils.hasText(thuongHieuDTO.getMa()) ? thuongHieuDTO.getMa().trim() : entity.getMaThuongHieu();
        if (!updatedMa.equals(entity.getMaThuongHieu()) && thuongHieuRepository.existsByMaThuongHieuAndIdNot(updatedMa, id)) {
            throw new IllegalArgumentException("Ma thuong hieu da ton tai: " + updatedMa);
        }

        entity.setMaThuongHieu(updatedMa);
        applyUpsertFields(entity, thuongHieuDTO, false);

        if (StringUtils.hasText(thuongHieuDTO.getNguoiSua())) {
            entity.setNguoiSua(thuongHieuDTO.getNguoiSua().trim());
        }
        if (thuongHieuDTO.getTrangThai() != null) {
            entity.setTrangThai(thuongHieuDTO.getTrangThai());
        }

        entity.setNgaySua(LocalDateTime.now());
        return toDTO(thuongHieuRepository.save(entity));
    }

    @Override
    public ThuongHieuDTO toggleStatus(Long id) {
        ThuongHieu entity = thuongHieuRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("ThuongHieu not found with id: " + id));

        entity.setTrangThai(entity.getTrangThai() != null && entity.getTrangThai() == 1 ? 0 : 1);
        entity.setNgaySua(LocalDateTime.now());
        return toDTO(thuongHieuRepository.save(entity));
    }

    private void applyUpsertFields(ThuongHieu entity, ThuongHieuDTO dto, boolean isCreate) {
        if (StringUtils.hasText(dto.getTen())) {
            entity.setTenThuongHieu(dto.getTen().trim());
        } else if (isCreate) {
            throw new IllegalArgumentException("Ten thuong hieu khong duoc de trong");
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
            if (thuongHieuRepository.existsByMaThuongHieu(normalizedMa)) {
                throw new IllegalArgumentException("Ma thuong hieu da ton tai: " + normalizedMa);
            }
            return normalizedMa;
        }

        String generatedMa;
        do {
            generatedMa = MA_PREFIX + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        } while (thuongHieuRepository.existsByMaThuongHieu(generatedMa));

        return generatedMa;
    }

    private String resolveNguoi(String nguoi) {
        return StringUtils.hasText(nguoi) ? nguoi.trim() : "system";
    }

    private ThuongHieuDTO toDTO(ThuongHieu entity) {
        return ThuongHieuDTO.builder()
                .id(entity.getId())
                .ma(entity.getMaThuongHieu())
                .ten(entity.getTenThuongHieu())
                .trangThai(entity.getTrangThai())
                .ngayTao(entity.getNgayTao())
                .ngaySua(entity.getNgaySua())
                .nguoiTao(entity.getNguoiTao())
                .nguoiSua(entity.getNguoiSua())
                .build();
    }
}


package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.LoaiSanPhamDTO;
import com.example.be_dantn.Entity.LoaiSanPham;
import com.example.be_dantn.Repository.LoaiSanPhamRepository;
import com.example.be_dantn.exception.CustomResourceotFoundException;
import com.example.be_dantn.sevicer.LoaiSanPhamService;
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
public class LoaiSanPhamServiceImpl implements LoaiSanPhamService {

    private static final String MA_PREFIX = "TTLSP";

    private final LoaiSanPhamRepository loaiSanPhamRepository;
    private final com.example.be_dantn.Config.CodeGenerator codeGenerator;

    @Override
    public Page<LoaiSanPhamDTO> findAll(Pageable pageable, String keyword, Integer trangThai) {
        Specification<LoaiSanPham> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                String likeKeyword = "%" + keyword.trim().toLowerCase() + "%";
                Predicate keywordPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("maLoaiSanPham")), likeKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("tenLoaiSanPham")), likeKeyword)
                );
                predicates.add(keywordPredicate);
            }

            if (trangThai != null) {
                predicates.add(criteriaBuilder.equal(root.get("trangThai"), trangThai));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return loaiSanPhamRepository.findAll(specification, pageable).map(this::toDTO);
    }

    @Override
    public LoaiSanPhamDTO findById(Long id) {
        LoaiSanPham entity = loaiSanPhamRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("LoaiSanPham not found with id: " + id));
        return toDTO(entity);
    }

    @Override
    public LoaiSanPhamDTO save(LoaiSanPhamDTO loaiSanPhamDTO) {
        LoaiSanPham entity = new LoaiSanPham();
        applyUpsertFields(entity, loaiSanPhamDTO, true);
        entity.setMaLoaiSanPham(resolveMaForCreate(loaiSanPhamDTO.getMa()));
        entity.setNguoiTao(resolveNguoi(loaiSanPhamDTO.getNguoiTao()));
        entity.setNguoiSua(resolveNguoi(loaiSanPhamDTO.getNguoiSua()));
        entity.setTrangThai(loaiSanPhamDTO.getTrangThai() != null ? loaiSanPhamDTO.getTrangThai() : 1);

        return toDTO(loaiSanPhamRepository.save(entity));
    }

    @Override
    public LoaiSanPhamDTO update(Long id, LoaiSanPhamDTO loaiSanPhamDTO) {
        LoaiSanPham entity = loaiSanPhamRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("LoaiSanPham not found with id: " + id));

        String updatedMa = StringUtils.hasText(loaiSanPhamDTO.getMa()) ? loaiSanPhamDTO.getMa().trim() : entity.getMaLoaiSanPham();
        if (!updatedMa.equals(entity.getMaLoaiSanPham()) && loaiSanPhamRepository.existsByMaLoaiSanPhamAndIdNot(updatedMa, id)) {
            throw new IllegalArgumentException("Ma loai san pham da ton tai: " + updatedMa);
        }

        entity.setMaLoaiSanPham(updatedMa);
        applyUpsertFields(entity, loaiSanPhamDTO, false);

        if (StringUtils.hasText(loaiSanPhamDTO.getNguoiSua())) {
            entity.setNguoiSua(loaiSanPhamDTO.getNguoiSua().trim());
        }
        if (loaiSanPhamDTO.getTrangThai() != null) {
            entity.setTrangThai(loaiSanPhamDTO.getTrangThai());
        }

        entity.setNgaySua(LocalDateTime.now());
        return toDTO(loaiSanPhamRepository.save(entity));
    }

    @Override
    public LoaiSanPhamDTO toggleStatus(Long id) {
        LoaiSanPham entity = loaiSanPhamRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("LoaiSanPham not found with id: " + id));

        entity.setTrangThai(entity.getTrangThai() != null && entity.getTrangThai() == 1 ? 0 : 1);
        entity.setNgaySua(LocalDateTime.now());
        return toDTO(loaiSanPhamRepository.save(entity));
    }

    private void applyUpsertFields(LoaiSanPham entity, LoaiSanPhamDTO dto, boolean isCreate) {
        if (StringUtils.hasText(dto.getTen())) {
            entity.setTenLoaiSanPham(dto.getTen().trim());
        } else if (isCreate) {
            throw new IllegalArgumentException("Ten loai san pham khong duoc de trong");
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
            if (loaiSanPhamRepository.existsByMaLoaiSanPham(normalizedMa)) {
                throw new IllegalArgumentException("Ma loai san pham da ton tai: " + normalizedMa);
            }
            return normalizedMa;
        }
        return codeGenerator.generateCode("loai_san_pham", "ma_loai_san_pham", "LSP");
    }

    private String resolveNguoi(String nguoi) {
        return StringUtils.hasText(nguoi) ? nguoi.trim() : "system";
    }

    private LoaiSanPhamDTO toDTO(LoaiSanPham entity) {
        return LoaiSanPhamDTO.builder()
                .id(entity.getId())
                .ma(entity.getMaLoaiSanPham())
                .ten(entity.getTenLoaiSanPham())
                .trangThai(entity.getTrangThai())
                .ngayTao(entity.getNgayTao())
                .ngaySua(entity.getNgaySua())
                .nguoiTao(entity.getNguoiTao())
                .nguoiSua(entity.getNguoiSua())
                .build();
    }
}


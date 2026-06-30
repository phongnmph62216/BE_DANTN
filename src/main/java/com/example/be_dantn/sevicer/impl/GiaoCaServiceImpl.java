package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.GiaoCaDTO;
import com.example.be_dantn.Entity.GiaoCa;
import com.example.be_dantn.Repository.GiaoCaRepository;
import com.example.be_dantn.exception.CustomResourceotFoundException;
import com.example.be_dantn.sevicer.GiaoCaService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GiaoCaServiceImpl implements GiaoCaService {

    private final GiaoCaRepository giaoCaRepository;

    @Override
    public List<GiaoCaDTO> findAll(String keyword, LocalDateTime fromDate, LocalDateTime toDate) {
        Specification<GiaoCa> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                String likeKeyword = "%" + keyword.trim().toLowerCase() + "%";
                
                Join<Object, Object> lichLamViecJoin = root.join("lichLamViec");
                Join<Object, Object> nhanVienJoin = lichLamViecJoin.join("nhanVien");
                Join<Object, Object> caLamViecJoin = lichLamViecJoin.join("caLamViec");

                Predicate pEmpName = criteriaBuilder.like(criteriaBuilder.lower(nhanVienJoin.get("hoVaTen")), likeKeyword);
                Predicate pEmpCode = criteriaBuilder.like(criteriaBuilder.lower(nhanVienJoin.get("maNhanVien")), likeKeyword);
                Predicate pShiftName = criteriaBuilder.like(criteriaBuilder.lower(caLamViecJoin.get("tenCa")), likeKeyword);

                Join<Object, Object> closeUserJoin = root.join("nguoiDongCa", jakarta.persistence.criteria.JoinType.LEFT);
                Predicate pCloseName = criteriaBuilder.like(criteriaBuilder.lower(closeUserJoin.get("hoVaTen")), likeKeyword);
                Predicate pCloseCode = criteriaBuilder.like(criteriaBuilder.lower(closeUserJoin.get("maNhanVien")), likeKeyword);

                predicates.add(criteriaBuilder.or(pEmpName, pEmpCode, pShiftName, pCloseName, pCloseCode));
            }

            if (fromDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("thoiGianMoCa"), fromDate));
            }

            if (toDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("thoiGianMoCa"), toDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return giaoCaRepository.findAll(specification, Sort.by(Sort.Direction.DESC, "thoiGianMoCa", "id")).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public GiaoCaDTO findById(Long id) {
        GiaoCa entity = giaoCaRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("Không tìm thấy lịch sử giao ca với ID: " + id));
        return toDTO(entity);
    }

    private GiaoCaDTO toDTO(GiaoCa entity) {
        GiaoCaDTO.GiaoCaDTOBuilder builder = GiaoCaDTO.builder()
                .id(entity.getId())
                .thoiGianMoCa(entity.getThoiGianMoCa())
                .thoiGianDongCa(entity.getThoiGianDongCa())
                .tienMatDauCa(entity.getTienMatDauCa())
                .tienMatThuTrongCa(entity.getTienMatThuTrongCa())
                .tienChuyenKhoanTrongCa(entity.getTienChuyenKhoanTrongCa())
                .tienMatThucTeChotCa(entity.getTienMatThucTeChotCa())
                .tienChenhLech(entity.getTienChenhLech())
                .trangThai(entity.getTrangThai());

        if (entity.getLichLamViec() != null) {
            builder.idLichLamViec(entity.getLichLamViec().getId());
            if (entity.getLichLamViec().getNhanVien() != null) {
                builder.maNhanVienNhanCa(entity.getLichLamViec().getNhanVien().getMaNhanVien());
                builder.tenNhanVienNhanCa(entity.getLichLamViec().getNhanVien().getHoVaTen());
            }
            if (entity.getLichLamViec().getCaLamViec() != null) {
                builder.tenCa(entity.getLichLamViec().getCaLamViec().getTenCa());
            }
        }

        if (entity.getNguoiDongCa() != null) {
            builder.idNguoiDongCa(entity.getNguoiDongCa().getId());
            builder.maNguoiDongCa(entity.getNguoiDongCa().getMaNhanVien());
            builder.tenNguoiDongCa(entity.getNguoiDongCa().getHoVaTen());
        }

        return builder.build();
    }
}

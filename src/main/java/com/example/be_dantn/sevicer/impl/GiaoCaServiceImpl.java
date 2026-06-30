package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.GiaoCaDTO;
import com.example.be_dantn.Dto.GiaoCaStatusDTO;
import com.example.be_dantn.Dto.Request.MoCaRequest;
import com.example.be_dantn.Entity.GiaoCa;
import com.example.be_dantn.Entity.LichLamViec;
import com.example.be_dantn.Repository.GiaoCaRepository;
import com.example.be_dantn.Repository.LichLamViecRepository;
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
    private final LichLamViecRepository lichLamViecRepository;

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

    @Override
    public GiaoCaStatusDTO getShiftStatus(Long employeeId) {
        if (employeeId == null) {
            return GiaoCaStatusDTO.builder()
                    .hasScheduleToday(false)
                    .status("NO_SCHEDULE")
                    .build();
        }

        java.time.LocalDate today = java.time.LocalDate.now();
        List<LichLamViec> schedules = lichLamViecRepository.findByNhanVienIdAndNgayLamViecAndTrangThai(employeeId, today, 1);

        if (schedules.isEmpty()) {
            return GiaoCaStatusDTO.builder()
                    .hasScheduleToday(false)
                    .status("NO_SCHEDULE")
                    .build();
        }

        // Pick the first schedule
        LichLamViec schedule = schedules.get(0);
        String employeeName = schedule.getNhanVien() != null ? schedule.getNhanVien().getHoVaTen() : "Nhân viên";
        String shiftName = "";
        if (schedule.getCaLamViec() != null) {
            shiftName = schedule.getCaLamViec().getTenCa() + " (" + schedule.getCaLamViec().getGioBatDau() + " - " + schedule.getCaLamViec().getGioKetThuc() + ")";
        }

        java.util.Optional<GiaoCa> existingGiaoCa = giaoCaRepository.findByLichLamViecId(schedule.getId());
        String status = "NOT_OPENED";
        Long giaoCaId = null;

        if (existingGiaoCa.isPresent()) {
            GiaoCa gc = existingGiaoCa.get();
            giaoCaId = gc.getId();
            status = gc.getTrangThai() == 0 ? "ACTIVE" : "CLOSED";
        }

        java.math.BigDecimal prevCash = java.math.BigDecimal.ZERO;
        java.math.BigDecimal prevBank = java.math.BigDecimal.ZERO;

        if ("NOT_OPENED".equals(status)) {
            // Find the most recently closed shift
            java.util.Optional<GiaoCa> lastClosed = giaoCaRepository.findFirstByTrangThaiOrderByThoiGianDongCaDesc(1);
            if (lastClosed.isPresent()) {
                java.math.BigDecimal cash = lastClosed.get().getTienMatThucTeChotCa();
                if (cash != null) {
                    prevCash = cash;
                }
                java.math.BigDecimal bank = lastClosed.get().getTienChuyenKhoanTrongCa();
                if (bank != null) {
                    prevBank = bank;
                }
            }
        }

        return GiaoCaStatusDTO.builder()
                .hasScheduleToday(true)
                .scheduleId(schedule.getId())
                .shiftName(shiftName)
                .employeeName(employeeName)
                .status(status)
                .previousShiftCash(prevCash)
                .previousShiftBank(prevBank)
                .giaoCaId(giaoCaId)
                .build();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public GiaoCaDTO moCa(MoCaRequest request) {
        if (request.getIdLichLamViec() == null) {
            throw new IllegalArgumentException("ID lịch làm việc không được để trống");
        }
        LichLamViec schedule = lichLamViecRepository.findById(request.getIdLichLamViec())
                .orElseThrow(() -> new CustomResourceotFoundException("Không tìm thấy lịch làm việc với ID: " + request.getIdLichLamViec()));

        // Check if already exists to prevent duplicate open shifts
        java.util.Optional<GiaoCa> existing = giaoCaRepository.findByLichLamViecId(schedule.getId());
        if (existing.isPresent()) {
            return toDTO(existing.get());
        }

        GiaoCa gc = GiaoCa.builder()
                .lichLamViec(schedule)
                .thoiGianMoCa(LocalDateTime.now())
                .tienMatDauCa(request.getTienMatDauCa() != null ? request.getTienMatDauCa() : java.math.BigDecimal.ZERO)
                .tienMatThuTrongCa(java.math.BigDecimal.ZERO)
                .tienChuyenKhoanTrongCa(java.math.BigDecimal.ZERO)
                .trangThai(0) // 0 means active/open
                .build();

        GiaoCa saved = giaoCaRepository.save(gc);
        return toDTO(saved);
    }
}

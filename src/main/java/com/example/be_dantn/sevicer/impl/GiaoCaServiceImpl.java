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

import com.example.be_dantn.Dto.Request.ChotCaRequest;
import com.example.be_dantn.Dto.Request.DoiSoatRequest;
import com.example.be_dantn.Entity.NhanVien;
import com.example.be_dantn.Entity.ThanhToan;
import com.example.be_dantn.Repository.NhanVienRepository;
import com.example.be_dantn.repository.ThanhToanRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GiaoCaServiceImpl implements GiaoCaService {

    private final GiaoCaRepository giaoCaRepository;
    private final LichLamViecRepository lichLamViecRepository;
    private final NhanVienRepository nhanVienRepository;
    private final PasswordEncoder passwordEncoder;
    private final ThanhToanRepository thanhToanRepository;
    private final com.example.be_dantn.Repository.PhieuChiRepository phieuChiRepository;

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
        java.math.BigDecimal tienMatChiRa = java.math.BigDecimal.ZERO;
        if (entity.getId() != null) {
            tienMatChiRa = phieuChiRepository.findByGiaoCaId(entity.getId()).stream()
                    .map(pc -> pc.getSoTien() != null ? pc.getSoTien() : java.math.BigDecimal.ZERO)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        }

        GiaoCaDTO.GiaoCaDTOBuilder builder = GiaoCaDTO.builder()
                .id(entity.getId())
                .thoiGianMoCa(entity.getThoiGianMoCa())
                .thoiGianDongCa(entity.getThoiGianDongCa())
                .tienMatDauCa(entity.getTienMatDauCa())
                .tienMatThuTrongCa(entity.getTienMatThuTrongCa())
                .tienChuyenKhoanTrongCa(entity.getTienChuyenKhoanTrongCa())
                .tienMatThucTeChotCa(entity.getTienMatThucTeChotCa())
                .tienChenhLech(entity.getTienChenhLech())
                .trangThai(entity.getTrangThai())
                .tienGiaoCaSau(entity.getTienGiaoCaSau())
                .trangThaiDoiSoat(entity.getTrangThaiDoiSoat())
                .phuongAnXuLy(entity.getPhuongAnXuLy())
                .ghiChuDoiSoat(entity.getGhiChuDoiSoat())
                .ghiChu(entity.getGhiChu())
                .tienMatChiRa(tienMatChiRa);

        if (entity.getLichLamViec() != null) {
            builder.idLichLamViec(entity.getLichLamViec().getId());
            if (entity.getLichLamViec().getNhanVien() != null) {
                builder.maNhanVienNhanCa(entity.getLichLamViec().getNhanVien().getMaNhanVien());
                builder.tenNhanVienNhanCa(entity.getLichLamViec().getNhanVien().getHoVaTen());
            }
            if (entity.getLichLamViec().getCaLamViec() != null) {
                builder.idCaLamViec(entity.getLichLamViec().getCaLamViec().getId());
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

        // Pick the most relevant schedule for today (active shift has highest priority, then not opened, then closed)
        LichLamViec schedule = null;
        java.util.Optional<GiaoCa> existingGiaoCa = java.util.Optional.empty();
        
        GiaoCa activeGc = null;
        LichLamViec activeSchedule = null;
        GiaoCa closedGc = null;
        LichLamViec closedSchedule = null;
        LichLamViec notOpenedSchedule = null;

        for (LichLamViec s : schedules) {
            java.util.Optional<GiaoCa> gcOpt = giaoCaRepository.findByLichLamViecId(s.getId());
            if (gcOpt.isPresent()) {
                GiaoCa gc = gcOpt.get();
                if (gc.getTrangThai() == 0) {
                    activeGc = gc;
                    activeSchedule = s;
                    break; // Found active shift, stop searching
                } else {
                    closedGc = gc;
                    closedSchedule = s;
                }
            } else {
                if (notOpenedSchedule == null) {
                    notOpenedSchedule = s;
                }
            }
        }

        if (activeGc != null) {
            schedule = activeSchedule;
            existingGiaoCa = java.util.Optional.of(activeGc);
        } else if (notOpenedSchedule != null) {
            schedule = notOpenedSchedule;
            existingGiaoCa = java.util.Optional.empty();
        } else if (closedSchedule != null) {
            schedule = closedSchedule;
            existingGiaoCa = java.util.Optional.of(closedGc);
        } else {
            schedule = schedules.get(0);
            existingGiaoCa = java.util.Optional.empty();
        }

        String employeeName = schedule.getNhanVien() != null ? schedule.getNhanVien().getHoVaTen() : "Nhân viên";
        String shiftName = "";
        if (schedule.getCaLamViec() != null) {
            shiftName = schedule.getCaLamViec().getTenCa() + " (" + schedule.getCaLamViec().getGioBatDau() + " - " + schedule.getCaLamViec().getGioKetThuc() + ")";
        }
        String status = "NOT_OPENED";
        Long giaoCaId = null;
        java.math.BigDecimal prevCash = java.math.BigDecimal.ZERO;
        java.math.BigDecimal prevBank = java.math.BigDecimal.ZERO;
        java.math.BigDecimal cashRevenue = java.math.BigDecimal.ZERO;
        java.math.BigDecimal bankRevenue = java.math.BigDecimal.ZERO;

        if (existingGiaoCa.isPresent()) {
            GiaoCa gc = existingGiaoCa.get();
            giaoCaId = gc.getId();
            status = gc.getTrangThai() == 0 ? "ACTIVE" : "CLOSED";
            
            if ("ACTIVE".equals(status)) {
                String hoVaTen = "";
                if (schedule.getNhanVien() != null) {
                    hoVaTen = schedule.getNhanVien().getHoVaTen();
                } else {
                    NhanVien nv = nhanVienRepository.findById(employeeId).orElse(null);
                    if (nv != null) {
                        hoVaTen = nv.getHoVaTen();
                    }
                }
                if (hoVaTen == null) {
                    hoVaTen = "";
                }

                // Calculate dynamic revenues from ThanhToan records since open time
                List<ThanhToan> payments = thanhToanRepository.findByNhanVienAndThoiGian(
                        employeeId, hoVaTen, gc.getThoiGianMoCa(), java.time.LocalDateTime.now()
                );
                for (ThanhToan pt : payments) {
                    String method = pt.getPhuongThuc() != null ? pt.getPhuongThuc().trim() : "";
                    if ("1".equals(method)) {
                        cashRevenue = cashRevenue.add(pt.getSoTien() != null ? pt.getSoTien() : java.math.BigDecimal.ZERO);
                    } else if ("2".equals(method)) {
                        bankRevenue = bankRevenue.add(pt.getSoTien() != null ? pt.getSoTien() : java.math.BigDecimal.ZERO);
                    }
                }
                gc.setTienMatThuTrongCa(cashRevenue);
                gc.setTienChuyenKhoanTrongCa(bankRevenue);
                giaoCaRepository.save(gc);
                
                prevCash = gc.getTienMatDauCa() != null ? gc.getTienMatDauCa() : java.math.BigDecimal.ZERO;
                prevBank = gc.getTienChuyenKhoanTrongCa() != null ? gc.getTienChuyenKhoanTrongCa() : java.math.BigDecimal.ZERO;
            } else {
                prevCash = gc.getTienMatDauCa() != null ? gc.getTienMatDauCa() : java.math.BigDecimal.ZERO;
                prevBank = gc.getTienChuyenKhoanTrongCa() != null ? gc.getTienChuyenKhoanTrongCa() : java.math.BigDecimal.ZERO;
                cashRevenue = gc.getTienMatThuTrongCa() != null ? gc.getTienMatThuTrongCa() : java.math.BigDecimal.ZERO;
                bankRevenue = gc.getTienChuyenKhoanTrongCa() != null ? gc.getTienChuyenKhoanTrongCa() : java.math.BigDecimal.ZERO;
            }
        } else {
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

        java.math.BigDecimal expenses = java.math.BigDecimal.ZERO;
        if (giaoCaId != null) {
            expenses = phieuChiRepository.findByGiaoCaId(giaoCaId).stream()
                    .map(pc -> pc.getSoTien() != null ? pc.getSoTien() : java.math.BigDecimal.ZERO)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
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
                .tienMatThuTrongCa(cashRevenue)
                .tienChuyenKhoanTrongCa(bankRevenue)
                .ngayLamViec(schedule.getNgayLamViec())
                .gioBatDau(schedule.getCaLamViec() != null ? schedule.getCaLamViec().getGioBatDau() : null)
                .gioKetThuc(schedule.getCaLamViec() != null ? schedule.getCaLamViec().getGioKetThuc() : null)
                .tienMatChiRa(expenses)
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

    @Override
    @org.springframework.transaction.annotation.Transactional
    public GiaoCaDTO chotCa(ChotCaRequest request) {
        if (request.getIdGiaoCa() == null) {
            throw new IllegalArgumentException("ID giao ca không được để trống");
        }
        GiaoCa gc = giaoCaRepository.findById(request.getIdGiaoCa())
                .orElseThrow(() -> new CustomResourceotFoundException("Không tìm thấy giao ca với ID: " + request.getIdGiaoCa()));

        if (gc.getTrangThai() == 1) {
            throw new IllegalStateException("Ca làm việc này đã được chốt trước đó.");
        }

        // Validate that the shift end time has passed
        LichLamViec schedule = gc.getLichLamViec();
        if (schedule != null && schedule.getCaLamViec() != null) {
            com.example.be_dantn.Entity.CaLamViec ca = schedule.getCaLamViec();
            java.time.LocalDate date = schedule.getNgayLamViec();
            java.time.LocalTime start = ca.getGioBatDau();
            java.time.LocalTime end = ca.getGioKetThuc();
            if (date != null && end != null) {
                LocalDateTime endDateTime = LocalDateTime.of(date, end);
                if (start != null && end.isBefore(start)) {
                    // Overnight shift ends on the next day
                    endDateTime = endDateTime.plusDays(1);
                }
                if (LocalDateTime.now().isBefore(endDateTime)) {
                    String endStr = end.toString().substring(0, 5);
                    throw new IllegalStateException("Không thể chốt ca trước giờ kết thúc ca (" + endStr + ").");
                }
            }
        }

        gc.setThoiGianDongCa(LocalDateTime.now());
        gc.setTienMatThucTeChotCa(request.getTienMatThucTeChotCa() != null ? request.getTienMatThucTeChotCa() : java.math.BigDecimal.ZERO);
        gc.setTienChuyenKhoanTrongCa(request.getTienChuyenKhoanTrongCa() != null ? request.getTienChuyenKhoanTrongCa() : java.math.BigDecimal.ZERO);
        gc.setTienChenhLech(request.getTienChenhLech() != null ? request.getTienChenhLech() : java.math.BigDecimal.ZERO);
        gc.setTienGiaoCaSau(request.getTienGiaoCaSau() != null ? request.getTienGiaoCaSau() : java.math.BigDecimal.ZERO);
        gc.setGhiChu(request.getGhiChu());
        gc.setTrangThai(1); // Closed
        gc.setTrangThaiDoiSoat(0); // Awaiting Audit

        GiaoCa saved = giaoCaRepository.save(gc);
        return toDTO(saved);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public GiaoCaDTO doiSoat(Long id, DoiSoatRequest request) {
        GiaoCa gc = giaoCaRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("Không tìm thấy ca trực với ID: " + id));

        if (gc.getTrangThai() == 0) {
            throw new IllegalStateException("Ca trực này chưa đóng, không thể thực hiện đối soát.");
        }

        gc.setTrangThaiDoiSoat(request.getTrangThaiDoiSoat() != null ? request.getTrangThaiDoiSoat() : 1);
        gc.setPhuongAnXuLy(request.getPhuongAnXuLy());
        gc.setGhiChuDoiSoat(request.getGhiChuDoiSoat());

        GiaoCa saved = giaoCaRepository.save(gc);
        return toDTO(saved);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public com.example.be_dantn.Dto.PhieuChiDTO createPhieuChi(com.example.be_dantn.Dto.Request.CreatePhieuChiRequest request) {
        if (request.getIdGiaoCa() == null) {
            throw new IllegalArgumentException("ID giao ca không được để trống");
        }
        if (request.getSoTien() == null || request.getSoTien().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền chi phải lớn hơn 0");
        }
        if (request.getLyDo() == null || request.getLyDo().trim().isEmpty()) {
            throw new IllegalArgumentException("Lý do chi không được để trống");
        }

        GiaoCa gc = giaoCaRepository.findById(request.getIdGiaoCa())
                .orElseThrow(() -> new CustomResourceotFoundException("Không tìm thấy ca giao ca với ID: " + request.getIdGiaoCa()));

        if (gc.getTrangThai() == 1) {
            throw new IllegalStateException("Ca giao ca này đã đóng, không thể chi thêm tiền.");
        }

        // Dynamic calculation of latest cash revenue to ensure correctness
        java.math.BigDecimal cashRevenue = java.math.BigDecimal.ZERO;
        LichLamViec schedule = gc.getLichLamViec();
        if (schedule != null) {
            NhanVien nv = schedule.getNhanVien();
            Long empId = nv != null ? nv.getId() : null;
            String hoVaTen = nv != null ? nv.getHoVaTen() : "";
            if (empId != null) {
                List<ThanhToan> payments = thanhToanRepository.findByNhanVienAndThoiGian(
                        empId, hoVaTen, gc.getThoiGianMoCa(), java.time.LocalDateTime.now()
                );
                for (ThanhToan pt : payments) {
                    String method = pt.getPhuongThuc() != null ? pt.getPhuongThuc().trim() : "";
                    if ("1".equals(method)) {
                        cashRevenue = cashRevenue.add(pt.getSoTien() != null ? pt.getSoTien() : java.math.BigDecimal.ZERO);
                    }
                }
                gc.setTienMatThuTrongCa(cashRevenue);
                giaoCaRepository.save(gc);
            }
        }

        java.math.BigDecimal tienMatDauCa = gc.getTienMatDauCa() != null ? gc.getTienMatDauCa() : java.math.BigDecimal.ZERO;

        java.math.BigDecimal currentExpenses = phieuChiRepository.findByGiaoCaId(gc.getId()).stream()
                .map(pc -> pc.getSoTien() != null ? pc.getSoTien() : java.math.BigDecimal.ZERO)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        java.math.BigDecimal maxAllowedExpense = tienMatDauCa.subtract(currentExpenses);

        if (request.getSoTien().compareTo(maxAllowedExpense) > 0) {
            throw new IllegalStateException("Số tiền chi vượt quá số tiền cốp hiện có. Số tiền có thể chi tối đa là: " + 
                new java.text.DecimalFormat("#,###").format(maxAllowedExpense) + " đ");
        }

        com.example.be_dantn.Entity.PhieuChi pc = com.example.be_dantn.Entity.PhieuChi.builder()
                .giaoCa(gc)
                .soTien(request.getSoTien())
                .lyDo(request.getLyDo())
                .nguoiTao(request.getNguoiTao() != null ? request.getNguoiTao() : "Hệ thống")
                .build();

        com.example.be_dantn.Entity.PhieuChi saved = phieuChiRepository.save(pc);

        return com.example.be_dantn.Dto.PhieuChiDTO.builder()
                .id(saved.getId())
                .idGiaoCa(gc.getId())
                .maPhieu(saved.getMaPhieu())
                .ngayTao(saved.getNgayTao())
                .soTien(saved.getSoTien())
                .lyDo(saved.getLyDo())
                .nguoiTao(saved.getNguoiTao())
                .build();
    }

    @Override
    public List<com.example.be_dantn.Dto.PhieuChiDTO> getPhieuChis(Long idGiaoCa) {
        return phieuChiRepository.findByGiaoCaId(idGiaoCa).stream()
                .map(pc -> com.example.be_dantn.Dto.PhieuChiDTO.builder()
                        .id(pc.getId())
                        .idGiaoCa(idGiaoCa)
                        .maPhieu(pc.getMaPhieu())
                        .ngayTao(pc.getNgayTao())
                        .soTien(pc.getSoTien())
                        .lyDo(pc.getLyDo())
                        .nguoiTao(pc.getNguoiTao())
                        .build())
                .collect(Collectors.toList());
    }
}

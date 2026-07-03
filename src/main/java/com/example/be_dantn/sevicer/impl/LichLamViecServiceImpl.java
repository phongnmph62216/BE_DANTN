package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.LichLamViecDTO;
import com.example.be_dantn.Entity.CaLamViec;
import com.example.be_dantn.Entity.GiaoCa;
import com.example.be_dantn.Entity.LichLamViec;
import com.example.be_dantn.Entity.NhanVien;
import com.example.be_dantn.Repository.CaLamViecRepository;
import com.example.be_dantn.Repository.GiaoCaRepository;
import com.example.be_dantn.Repository.LichLamViecRepository;
import com.example.be_dantn.Repository.NhanVienRepository;
import com.example.be_dantn.exception.CustomResourceotFoundException;
import com.example.be_dantn.sevicer.LichLamViecService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LichLamViecServiceImpl implements LichLamViecService {

    private final LichLamViecRepository lichLamViecRepository;
    private final NhanVienRepository nhanVienRepository;
    private final CaLamViecRepository caLamViecRepository;
    private final GiaoCaRepository giaoCaRepository;

    @Override
    public List<LichLamViecDTO> findAll(Long idNhanVien, LocalDate startDate, LocalDate endDate) {
        Specification<LichLamViec> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (idNhanVien != null) {
                predicates.add(criteriaBuilder.equal(root.get("nhanVien").get("id"), idNhanVien));
            }

            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("ngayLamViec"), startDate));
            }

            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("ngayLamViec"), endDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return lichLamViecRepository.findAll(specification, Sort.by(Sort.Direction.ASC, "ngayLamViec", "id")).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LichLamViecDTO findById(Long id) {
        LichLamViec entity = lichLamViecRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("Không tìm thấy lịch làm việc với ID: " + id));
        return toDTO(entity);
    }

    @Override
    public LichLamViecDTO save(LichLamViecDTO dto) {
        NhanVien nv = nhanVienRepository.findById(dto.getIdNhanVien())
                .orElseThrow(() -> new CustomResourceotFoundException("Không tìm thấy nhân viên với ID: " + dto.getIdNhanVien()));

        validateNotManagerOrAdmin(nv);

        CaLamViec ca = caLamViecRepository.findById(dto.getIdCaLamViec())
                .orElseThrow(() -> new CustomResourceotFoundException("Không tìm thấy ca làm việc với ID: " + dto.getIdCaLamViec()));

        // Prevent duplicate scheduling
        if (lichLamViecRepository.existsByNhanVienIdAndNgayLamViecAndCaLamViecId(dto.getIdNhanVien(), dto.getNgayLamViec(), dto.getIdCaLamViec())) {
            throw new IllegalArgumentException("Nhân viên này đã được phân ca " + ca.getTenCa() + " vào ngày " + dto.getNgayLamViec());
        }

        LichLamViec entity = new LichLamViec();
        entity.setNhanVien(nv);
        entity.setCaLamViec(ca);
        entity.setNgayLamViec(dto.getNgayLamViec());
        entity.setGhiChu(dto.getGhiChu() != null ? dto.getGhiChu().trim() : null);
        entity.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : 1);
        entity.setNguoiTaoQuanLy(dto.getNguoiTaoQuanLy());
        entity.setNguoiTao(resolveNguoi(dto.getNguoiTao()));
        entity.setNguoiSua(resolveNguoi(dto.getNguoiSua()));

        return toDTO(lichLamViecRepository.save(entity));
    }

    @Override
    public LichLamViecDTO update(Long id, LichLamViecDTO dto) {
        LichLamViec entity = lichLamViecRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("Không tìm thấy lịch làm việc với ID: " + id));

        NhanVien nv = nhanVienRepository.findById(dto.getIdNhanVien())
                .orElseThrow(() -> new CustomResourceotFoundException("Không tìm thấy nhân viên với ID: " + dto.getIdNhanVien()));

        validateNotManagerOrAdmin(nv);

        CaLamViec ca = caLamViecRepository.findById(dto.getIdCaLamViec())
                .orElseThrow(() -> new CustomResourceotFoundException("Không tìm thấy ca làm việc với ID: " + dto.getIdCaLamViec()));

        // Prevent duplicate scheduling for other assignments
        if (lichLamViecRepository.existsByNhanVienIdAndNgayLamViecAndCaLamViecIdAndIdNot(dto.getIdNhanVien(), dto.getNgayLamViec(), dto.getIdCaLamViec(), id)) {
            throw new IllegalArgumentException("Nhân viên này đã được phân ca " + ca.getTenCa() + " vào ngày " + dto.getNgayLamViec());
        }

        entity.setNhanVien(nv);
        entity.setCaLamViec(ca);
        entity.setNgayLamViec(dto.getNgayLamViec());
        entity.setGhiChu(dto.getGhiChu() != null ? dto.getGhiChu().trim() : null);
        if (dto.getTrangThai() != null) {
            entity.setTrangThai(dto.getTrangThai());
        }
        if (StringUtils.hasText(dto.getNguoiTaoQuanLy())) {
            entity.setNguoiTaoQuanLy(dto.getNguoiTaoQuanLy());
        }
        entity.setNguoiSua(resolveNguoi(dto.getNguoiSua()));
        entity.setNgaySua(LocalDateTime.now());

        return toDTO(lichLamViecRepository.save(entity));
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void delete(Long id) {
        LichLamViec entity = lichLamViecRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("Không tìm thấy lịch làm việc với ID: " + id));
        
        // Delete associated GiaoCa record first to prevent reference constraint conflicts
        java.util.Optional<GiaoCa> gcOpt = giaoCaRepository.findByLichLamViecId(id);
        if (gcOpt.isPresent()) {
            giaoCaRepository.delete(gcOpt.get());
        }
        
        lichLamViecRepository.delete(entity);
    }

    private void validateNotManagerOrAdmin(NhanVien nv) {
        if (nv.getVaiTro() != null) {
            String roleName = nv.getVaiTro().getTen() != null ? nv.getVaiTro().getTen().toLowerCase() : "";
            String roleCode = nv.getVaiTro().getMa() != null ? nv.getVaiTro().getMa().toLowerCase() : "";
            if (roleName.contains("quan") || roleName.contains("admin") || roleName.contains("quản") ||
                roleCode.contains("quan") || roleCode.contains("admin") || roleCode.contains("quản")) {
                throw new IllegalArgumentException("Không thể xếp lịch làm việc cho Quản lý / Admin.");
            }
        }
    }

    private String resolveNguoi(String name) {
        return StringUtils.hasText(name) ? name.trim() : "system";
    }

    private LichLamViecDTO toDTO(LichLamViec entity) {
        return LichLamViecDTO.builder()
                .id(entity.getId())
                .idNhanVien(entity.getNhanVien().getId())
                .idCaLamViec(entity.getCaLamViec().getId())
                .ngayLamViec(entity.getNgayLamViec())
                .ghiChu(entity.getGhiChu())
                .trangThai(entity.getTrangThai())
                .nguoiTaoQuanLy(entity.getNguoiTaoQuanLy())
                .ngayTao(entity.getNgayTao())
                .ngaySua(entity.getNgaySua())
                .nguoiTao(entity.getNguoiTao())
                .nguoiSua(entity.getNguoiSua())
                .build();
    }

    @Override
    public byte[] downloadTemplate() throws java.io.IOException {
        try (org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
             java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream()) {
            
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Template");
            
            // Header Style
            org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

            org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
            String[] columns = {"Mã Nhân Viên", "Mã Ca", "Ngày Làm Việc (YYYY-MM-DD)", "Ghi Chú"};
            for (int i = 0; i < columns.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Write mock data helper row
            org.apache.poi.ss.usermodel.Row sampleRow1 = sheet.createRow(1);
            sampleRow1.createCell(0).setCellValue("NV001");
            sampleRow1.createCell(1).setCellValue("CA001");
            sampleRow1.createCell(2).setCellValue("2026-07-02");
            sampleRow1.createCell(3).setCellValue("Trực quầy chính");

            org.apache.poi.ss.usermodel.Row sampleRow2 = sheet.createRow(2);
            sampleRow2.createCell(0).setCellValue("NV002");
            sampleRow2.createCell(1).setCellValue("CA002");
            sampleRow2.createCell(2).setCellValue("2026-07-02");
            sampleRow2.createCell(3).setCellValue("Hỗ trợ thu ngân");

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public String importExcel(org.springframework.web.multipart.MultipartFile file) throws java.io.IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn tệp Excel để nhập");
        }

        int successCount = 0;
        int skipCount = 0;
        java.util.List<String> errors = new java.util.ArrayList<>();
        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (org.apache.poi.ss.usermodel.Workbook workbook = org.apache.poi.ss.usermodel.WorkbookFactory.create(file.getInputStream())) {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();

            for (int i = 1; i < rowCount; i++) {
                org.apache.poi.ss.usermodel.Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                // Check if row is empty
                boolean isEmptyRow = true;
                for (int c = 0; c < 4; c++) {
                    org.apache.poi.ss.usermodel.Cell cell = row.getCell(c);
                    if (cell != null && cell.getCellType() != org.apache.poi.ss.usermodel.CellType.BLANK) {
                        isEmptyRow = false;
                        break;
                    }
                }
                if (isEmptyRow) {
                    continue;
                }

                String maNhanVien = getCellValueAsString(row.getCell(0));
                String maCa = getCellValueAsString(row.getCell(1));
                String ngayLamViecStr = getCellValueAsString(row.getCell(2));
                String ghiChu = getCellValueAsString(row.getCell(3));

                if (maNhanVien.isEmpty() || maCa.isEmpty() || ngayLamViecStr.isEmpty()) {
                    skipCount++;
                    errors.add("Dòng " + (i + 1) + ": Thiếu thông tin bắt buộc (Mã nhân viên, Mã ca, Ngày làm việc)");
                    continue;
                }

                // Clean string values
                maNhanVien = maNhanVien.trim();
                maCa = maCa.trim();
                ngayLamViecStr = ngayLamViecStr.trim();
                ghiChu = ghiChu.trim();

                // Validate NhanVien
                java.util.Optional<NhanVien> nhanVienOpt = nhanVienRepository.findByMaNhanVien(maNhanVien);
                if (nhanVienOpt.isEmpty()) {
                    skipCount++;
                    errors.add("Dòng " + (i + 1) + ": Không tìm thấy nhân viên với mã " + maNhanVien);
                    continue;
                }
                NhanVien nhanVien = nhanVienOpt.get();

                // Validate Not Manager Or Admin
                try {
                    validateNotManagerOrAdmin(nhanVien);
                } catch (IllegalArgumentException e) {
                    skipCount++;
                    errors.add("Dòng " + (i + 1) + ": " + e.getMessage());
                    continue;
                }

                // Validate CaLamViec
                java.util.Optional<CaLamViec> caLamViecOpt = caLamViecRepository.findByMaCa(maCa);
                if (caLamViecOpt.isEmpty()) {
                    skipCount++;
                    errors.add("Dòng " + (i + 1) + ": Không tìm thấy ca làm việc với mã " + maCa);
                    continue;
                }
                CaLamViec caLamViec = caLamViecOpt.get();
                if (caLamViec.getTrangThai() != null && caLamViec.getTrangThai() != 1) {
                    skipCount++;
                    errors.add("Dòng " + (i + 1) + ": Ca làm việc " + maCa + " đang ngưng hoạt động");
                    continue;
                }

                // Parse Date
                java.time.LocalDate ngayLamViec = null;
                try {
                    // Try parsing from string first
                    ngayLamViec = java.time.LocalDate.parse(ngayLamViecStr, dateFormatter);
                } catch (java.time.format.DateTimeParseException e) {
                    // Fallback to numeric cell date representation if cell formatted as Date
                    org.apache.poi.ss.usermodel.Cell dateCell = row.getCell(2);
                    if (dateCell != null && org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(dateCell)) {
                        java.util.Date d = dateCell.getDateCellValue();
                        if (d != null) {
                            ngayLamViec = d.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                        }
                    }
                }

                if (ngayLamViec == null) {
                    skipCount++;
                    errors.add("Dòng " + (i + 1) + ": Định dạng ngày không hợp lệ. Vui lòng sử dụng định dạng YYYY-MM-DD");
                    continue;
                }

                // Check duplicate schedule
                if (lichLamViecRepository.existsByNhanVienIdAndNgayLamViecAndCaLamViecId(nhanVien.getId(), ngayLamViec, caLamViec.getId())) {
                    skipCount++;
                    errors.add("Dòng " + (i + 1) + ": Nhân viên " + maNhanVien + " đã có lịch làm việc cho ca " + maCa + " ngày " + ngayLamViec);
                    continue;
                }

                // Create schedule
                LichLamViec schedule = new LichLamViec();
                schedule.setNhanVien(nhanVien);
                schedule.setCaLamViec(caLamViec);
                schedule.setNgayLamViec(ngayLamViec);
                schedule.setGhiChu(ghiChu);
                schedule.setTrangThai(1);
                schedule.setNgayTao(java.time.LocalDateTime.now());
                schedule.setNguoiTao("Quản lý (Excel Import)");

                lichLamViecRepository.save(schedule);
                successCount++;
            }
        }

        StringBuilder message = new StringBuilder();
        message.append("Nhập dữ liệu thành công: ").append(successCount).append(" bản ghi.");
        if (skipCount > 0) {
            message.append(" Bỏ qua: ").append(skipCount).append(" bản ghi.");
        }
        if (!errors.isEmpty()) {
            message.append("\nChi tiết lỗi:\n").append(String.join("\n", errors));
        }
        return message.toString();
    }

    private String getCellValueAsString(org.apache.poi.ss.usermodel.Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    java.util.Date d = cell.getDateCellValue();
                    if (d != null) {
                        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(d);
                    }
                }
                double val = cell.getNumericCellValue();
                if (val == (long) val) {
                    return String.valueOf((long) val);
                }
                return String.valueOf(val);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return "";
        }
    }
}

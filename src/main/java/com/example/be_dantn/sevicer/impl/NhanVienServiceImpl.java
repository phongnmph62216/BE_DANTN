package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.Request.NhanVienCreateRequest;
import com.example.be_dantn.Dto.Request.NhanVienUpdateRequest;
import com.example.be_dantn.Dto.Response.NhanVienResponseDTO;
import com.example.be_dantn.Entity.NhanVien;
import com.example.be_dantn.Entity.VaiTro;
import com.example.be_dantn.Exception.ResourceNotFoundException;
import com.example.be_dantn.Repository.NhanVienRepository;
import com.example.be_dantn.Repository.VaiTroRepository;
import com.example.be_dantn.sevicer.NhanVienService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.be_dantn.sevicer.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NhanVienServiceImpl implements NhanVienService {

    private final NhanVienRepository nhanVienRepository;
    private final VaiTroRepository vaiTroRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public Page<NhanVienResponseDTO> getByFilters(String keyword, Integer trangThai, Pageable pageable) {
        return nhanVienRepository.findByFilters(keyword, trangThai, pageable);
    }

    @Override
    @Transactional
    public void toggleStatus(Long id) {
        NhanVien nhanVien = nhanVienRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên với id: " + id));
        nhanVien.setTrangThai(nhanVien.getTrangThai() == 1 ? 0 : 1);
        nhanVienRepository.save(nhanVien);
    }

    @Override
    public byte[] exportNhanVienToExcel(String keyword, Integer trangThai) throws IOException {
        List<NhanVienResponseDTO> nhanViens = nhanVienRepository.findAllForExcel(keyword, trangThai);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("DanhSachNhanVien");

        // Header
        Row headerRow = sheet.createRow(0);
        String[] columns = {"STT", "Mã NV", "Họ và Tên", "Email", "Số Điện Thoại", "Địa Chỉ", "Chức Vụ", "Trạng Thái"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        // Data
        int rowNum = 1;
        for (NhanVienResponseDTO nv : nhanViens) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum - 1);
            row.createCell(1).setCellValue(nv.getMaNhanVien());
            row.createCell(2).setCellValue(nv.getHoVaTen());
            row.createCell(3).setCellValue(nv.getEmail());
            row.createCell(4).setCellValue(nv.getSoDienThoai());
            row.createCell(5).setCellValue(nv.getDiaChi());
            row.createCell(6).setCellValue(nv.getTenVaiTro());
            row.createCell(7).setCellValue(nv.getTrangThai() != null && nv.getTrangThai() == 1 ? "Đang làm" : "Đã nghỉ");
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    @Override
    public NhanVien getNhanVienById(Long id) {
        return nhanVienRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên với id: " + id));
    }

    @Override
    @Transactional
    public NhanVien updateNhanVien(Long id, NhanVienUpdateRequest request) {
        // Validate email and phone number uniqueness
        nhanVienRepository.findByEmailAndIdNot(request.getEmail(), id)
                .ifPresent(nv -> { throw new IllegalArgumentException("Email đã được sử dụng bởi nhân viên khác."); });
        nhanVienRepository.findBySoDienThoaiAndIdNot(request.getSoDienThoai(), id)
                .ifPresent(nv -> { throw new IllegalArgumentException("Số điện thoại đã được sử dụng bởi nhân viên khác."); });

        NhanVien nhanVien = getNhanVienById(id);
        VaiTro vaiTro = vaiTroRepository.findById(request.getIdVaiTro())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vai trò với id: " + request.getIdVaiTro()));

        // Update fields
        nhanVien.setVaiTro(vaiTro);
        nhanVien.setHoVaTen(request.getHoVaTen());
        nhanVien.setSoDienThoai(request.getSoDienThoai());
        nhanVien.setEmail(request.getEmail());
        nhanVien.setCccd(request.getCccd());
        nhanVien.setGioiTinh(request.getGioiTinh());
        nhanVien.setNgaySinh(request.getNgaySinh());
        nhanVien.setDiaChi(request.getDiaChi());
        nhanVien.setAnh(request.getAnh());

        return nhanVienRepository.save(nhanVien);
    }

    @Override
    @Transactional
    public NhanVien createNhanVien(NhanVienCreateRequest request) {
        // 1. Validate tính duy nhất
        if (nhanVienRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại trong hệ thống.");
        }
        if (nhanVienRepository.existsBySoDienThoai(request.getSoDienThoai())) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại trong hệ thống.");
        }
        if (nhanVienRepository.existsByCccd(request.getCccd())) {
            throw new IllegalArgumentException("CCCD đã tồn tại trong hệ thống.");
        }

        VaiTro vaiTro = vaiTroRepository.findById(request.getIdVaiTro())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vai trò với id: " + request.getIdVaiTro()));

        // 2. Tạo Entity và set dữ liệu
        NhanVien nhanVien = new NhanVien();
        nhanVien.setHoVaTen(request.getHoVaTen());
        nhanVien.setEmail(request.getEmail());
        nhanVien.setSoDienThoai(request.getSoDienThoai());
        nhanVien.setCccd(request.getCccd());
        nhanVien.setNgaySinh(request.getNgaySinh());
        nhanVien.setGioiTinh(request.getGioiTinh());
        nhanVien.setVaiTro(vaiTro);
        nhanVien.setDiaChi(request.getDiaChi());
        nhanVien.setAnh(request.getAnh());

        // 3. Logic tự sinh dữ liệu mặc định
        nhanVien.setMaNhanVien("NV" + System.currentTimeMillis());
        nhanVien.setMatKhau(passwordEncoder.encode(request.getSoDienThoai())); // Mã hóa SĐT làm mật khẩu
        nhanVien.setTrangThai(1); // Đang làm
        nhanVien.setNgayVaoLam(LocalDate.now());

        // 4. Lưu vào DB
        NhanVien saved = nhanVienRepository.save(nhanVien);

        // 5. Gửi email tài khoản cho nhân viên
        emailService.sendEmployeeAccountEmail(
                saved.getEmail(),
                saved.getHoVaTen(),
                saved.getEmail(),
                request.getSoDienThoai()
        );

        return saved;
    }
}

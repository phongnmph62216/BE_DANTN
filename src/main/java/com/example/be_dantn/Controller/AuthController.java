package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.Request.LoginRequestDTO;
import com.example.be_dantn.Dto.Request.RegisterRequestDTO;
import com.example.be_dantn.Dto.Response.AuthResponseDTO;
import com.example.be_dantn.Dto.ResponseObject;
import com.example.be_dantn.Entity.KhachHang;
import com.example.be_dantn.Entity.NhanVien;
import com.example.be_dantn.Repository.KhachHangRepository;
import com.example.be_dantn.Repository.NhanVienRepository;
import com.example.be_dantn.Config.CodeGenerator;
import com.example.be_dantn.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CodeGenerator codeGenerator;

    @PostMapping("/login")
    public ResponseEntity<ResponseObject<AuthResponseDTO>> login(@RequestBody LoginRequestDTO request) {
        String username = request.getUsername();
        String password = request.getPassword();

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new BadRequestException("Tên đăng nhập và mật khẩu không được để trống.");
        }

        username = username.trim();

        // 1. Tìm trong bảng nhân viên trước
        Optional<NhanVien> nvOpt = nhanVienRepository.findBySoDienThoai(username);
        if (nvOpt.isEmpty()) {
            nvOpt = nhanVienRepository.findByEmail(username);
        }

        if (nvOpt.isPresent()) {
            NhanVien nv = nvOpt.get();
            if (passwordEncoder.matches(password, nv.getMatKhau())) {
                if (nv.getTrangThai() != null && nv.getTrangThai() == 0) {
                    throw new BadRequestException("Tài khoản nhân viên này đã ngừng hoạt động.");
                }
                
                String role = null;
                if (nv.getVaiTro() != null) {
                    String roleName = nv.getVaiTro().getTen() != null ? nv.getVaiTro().getTen().toLowerCase() : "";
                    String roleCode = nv.getVaiTro().getMa() != null ? nv.getVaiTro().getMa().toLowerCase() : "";

                    if (roleName.contains("quan") || roleName.contains("admin") || roleName.contains("quản") ||
                        roleCode.contains("quan") || roleCode.contains("admin") || roleCode.contains("quản")) {
                        role = "ROLE_QUAN_LY";
                    } else if (roleName.contains("nhan") || roleName.contains("staff") || roleName.contains("viên") ||
                               roleCode.contains("nhan") || roleCode.contains("staff") || roleCode.contains("viên")) {
                        role = "ROLE_NHAN_VIEN";
                    }
                }

                // Nếu là nhân viên/quản lý hợp lệ thì trả về ngay
                if (role != null) {
                    AuthResponseDTO responseData = AuthResponseDTO.builder()
                            .id(nv.getId())
                            .ma(nv.getMaNhanVien())
                            .hoTen(nv.getHoVaTen())
                            .sdt(nv.getSoDienThoai())
                            .email(nv.getEmail())
                            .role(role)
                            .build();
                    return ResponseEntity.ok(new ResponseObject<>("success", "Đăng nhập thành công", responseData));
                }
            }
        }

        // 2. Nếu không phải nhân viên/quản lý (hoặc không khớp quyền), tìm trong bảng khách hàng
        Optional<KhachHang> khOpt = khachHangRepository.findBySdt(username);
        if (khOpt.isEmpty()) {
            khOpt = khachHangRepository.findByEmail(username);
        }
        if (khOpt.isEmpty()) {
            khOpt = khachHangRepository.findByTenTaiKhoan(username);
        }

        if (khOpt.isPresent()) {
            KhachHang kh = khOpt.get();
            if (passwordEncoder.matches(password, kh.getMatKhau())) {
                if (kh.getTrangThai() != null && kh.getTrangThai() == 0) {
                    throw new BadRequestException("Tài khoản của quý khách đã ngừng hoạt động.");
                }

                AuthResponseDTO responseData = AuthResponseDTO.builder()
                        .id(kh.getId())
                        .ma(kh.getMaKhachHang())
                        .hoTen(kh.getHoTen())
                        .sdt(kh.getSdt())
                        .email(kh.getEmail())
                        .role("ROLE_KHACH_HANG")
                        .build();
                return ResponseEntity.ok(new ResponseObject<>("success", "Đăng nhập thành công", responseData));
            }
        }

        throw new BadRequestException("Tên đăng nhập hoặc mật khẩu không chính xác.");
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseObject<Void>> register(@RequestBody RegisterRequestDTO request) {
        if (request.getSdt() == null || request.getSdt().trim().isEmpty()) {
            throw new BadRequestException("Số điện thoại không được để trống.");
        }
        if (request.getMatKhau() == null || request.getMatKhau().trim().isEmpty()) {
            throw new BadRequestException("Mật khẩu không được để trống.");
        }

        String sdt = request.getSdt().trim();
        String email = request.getEmail() != null ? request.getEmail().trim() : "";

        if (khachHangRepository.existsBySdt(sdt)) {
            throw new BadRequestException("Số điện thoại đã được đăng ký.");
        }

        if (!email.isEmpty() && khachHangRepository.existsByEmail(email)) {
            throw new BadRequestException("Email đã được đăng ký.");
        }

        String hoTen = "";
        if (request.getHo() != null) hoTen += request.getHo().trim();
        if (request.getTen() != null) {
            if (!hoTen.isEmpty()) hoTen += " ";
            hoTen += request.getTen().trim();
        }

        String maKhachHang = codeGenerator.generateCode("khach_hang", "ma_khach_hang", "KH");
        KhachHang kh = KhachHang.builder()
                .maKhachHang(maKhachHang)
                .hoTen(hoTen)
                .sdt(sdt)
                .email(email.isEmpty() ? null : email)
                .gioiTinh(request.getGioiTinh())
                .ngaySinh(request.getNgaySinh())
                .tenTaiKhoan(sdt)
                .matKhau(passwordEncoder.encode(request.getMatKhau()))
                .trangThai(1)
                .build();

        khachHangRepository.save(kh);

        return ResponseEntity.ok(new ResponseObject<>("success", "Đăng ký tài khoản thành công", null));
    }
}

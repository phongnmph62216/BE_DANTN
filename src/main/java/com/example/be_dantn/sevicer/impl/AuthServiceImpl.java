package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Entity.KhachHang;
import com.example.be_dantn.Entity.NhanVien;
import com.example.be_dantn.Repository.KhachHangRepository;
import com.example.be_dantn.Repository.NhanVienRepository;
import com.example.be_dantn.exception.BadRequestException;
import com.example.be_dantn.sevicer.AuthService;
import com.example.be_dantn.sevicer.EmailService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Cache to store active OTP tokens in-memory (Key: identifier lowercased -> OtpInfo)
    private static final Map<String, OtpInfo> otpCache = new ConcurrentHashMap<>();

    @Data
    @AllArgsConstructor
    private static class OtpInfo {
        private String otpCode;
        private LocalDateTime expiryTime;
        private String targetEmail;
        private String userName;
        private String accountType; // "CUSTOMER" or "STAFF"
        private Long accountId;
    }

    @Override
    public void sendForgotPasswordOtp(String emailOrPhone) {
        if (emailOrPhone == null || emailOrPhone.trim().isEmpty()) {
            throw new BadRequestException("Vui lòng nhập Email hoặc Số điện thoại của tài khoản.");
        }

        String input = emailOrPhone.trim();
        String targetEmail = null;
        String userName = null;
        String accountType = null;
        Long accountId = null;

        // 1. Search in KhachHang table first
        Optional<KhachHang> khOpt = input.contains("@")
                ? khachHangRepository.findByEmail(input)
                : khachHangRepository.findBySdt(input);

        if (khOpt.isPresent()) {
            KhachHang kh = khOpt.get();
            targetEmail = kh.getEmail();
            userName = kh.getHoTen();
            accountType = "CUSTOMER";
            accountId = kh.getId();
        } else {
            // 2. Search in NhanVien table
            Optional<NhanVien> nvOpt = input.contains("@")
                    ? nhanVienRepository.findByEmail(input)
                    : nhanVienRepository.findBySoDienThoai(input);

            if (nvOpt.isPresent()) {
                NhanVien nv = nvOpt.get();
                targetEmail = nv.getEmail();
                userName = nv.getHoVaTen();
                accountType = "STAFF";
                accountId = nv.getId();
            }
        }

        if (accountId == null) {
            throw new BadRequestException("Không tìm thấy tài khoản tương ứng với Email/SĐT đã nhập.");
        }

        if (targetEmail == null || targetEmail.trim().isEmpty() || !targetEmail.contains("@")) {
            throw new BadRequestException("Tài khoản này chưa đăng ký địa chỉ Email hợp lệ để nhận mã OTP.");
        }

        // Generate 6-digit OTP code
        String otpCode = String.format("%06d", new Random().nextInt(1000000));
        OtpInfo otpInfo = new OtpInfo(otpCode, LocalDateTime.now().plusMinutes(10), targetEmail, userName, accountType, accountId);

        // Put in cache under both input identifier and target email
        otpCache.put(input.toLowerCase(), otpInfo);
        otpCache.put(targetEmail.toLowerCase(), otpInfo);

        // Send OTP email
        emailService.sendResetPasswordOtpEmail(targetEmail, userName, otpCode);
    }

    @Override
    @Transactional
    public void resetPasswordWithOtp(String emailOrPhone, String otpCode, String newPassword) {
        if (emailOrPhone == null || emailOrPhone.trim().isEmpty()) {
            throw new BadRequestException("Vui lòng nhập Email hoặc Số điện thoại.");
        }
        if (otpCode == null || otpCode.trim().isEmpty()) {
            throw new BadRequestException("Vui lòng nhập mã xác thực OTP.");
        }
        if (newPassword == null || newPassword.trim().length() < 6) {
            throw new BadRequestException("Mật khẩu mới phải chứa ít nhất 6 ký tự.");
        }

        String key = emailOrPhone.trim().toLowerCase();
        OtpInfo otpInfo = otpCache.get(key);

        if (otpInfo == null) {
            throw new BadRequestException("Mã OTP không tồn tại hoặc đã hết hạn. Vui lòng yêu cầu mã mới.");
        }

        if (otpInfo.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpCache.remove(key);
            throw new BadRequestException("Mã OTP đã hết hạn (chỉ có hiệu lực trong 10 phút). Vui lòng gửi lại yêu cầu.");
        }

        if (!otpInfo.getOtpCode().equalsIgnoreCase(otpCode.trim())) {
            throw new BadRequestException("Mã OTP không chính xác. Vui lòng kiểm tra lại.");
        }

        // Update encoded password in DB
        String encodedPassword = passwordEncoder.encode(newPassword);

        if ("CUSTOMER".equals(otpInfo.getAccountType())) {
            KhachHang khachHang = khachHangRepository.findById(otpInfo.getAccountId())
                    .orElseThrow(() -> new BadRequestException("Không tìm thấy thông tin tài khoản khách hàng."));
            khachHang.setMatKhau(encodedPassword);
            khachHangRepository.save(khachHang);
        } else if ("STAFF".equals(otpInfo.getAccountType())) {
            NhanVien nhanVien = nhanVienRepository.findById(otpInfo.getAccountId())
                    .orElseThrow(() -> new BadRequestException("Không tìm thấy thông tin tài khoản nhân viên."));
            nhanVien.setMatKhau(encodedPassword);
            nhanVienRepository.save(nhanVien);
        }

        // Clear OTP from cache after successful reset
        otpCache.remove(key);
        otpCache.remove(otpInfo.getTargetEmail().toLowerCase());
    }
}

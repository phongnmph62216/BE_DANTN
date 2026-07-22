package com.example.be_dantn.sevicer;

public interface AuthService {
    void sendForgotPasswordOtp(String emailOrPhone);
    void resetPasswordWithOtp(String emailOrPhone, String otpCode, String newPassword);
}

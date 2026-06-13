package com.example.be_dantn.sevicer;

public interface EmailService {
    void sendVoucherEmail(
            String toEmail,
            String customerName,
            String voucherCode,
            String voucherName,
            String discountDetails,
            String expiryDate
    );
}

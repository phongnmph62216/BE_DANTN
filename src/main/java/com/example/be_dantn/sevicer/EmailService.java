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

    void sendEmployeeAccountEmail(
            String toEmail,
            String employeeName,
            String emailAccount,
            String rawPassword
    );

    void sendOrderSuccessEmail(
            String toEmail,
            String customerName,
            String orderCode,
            String totalAmount,
            String paymentMethod,
            String listProductsHtml,
            String trackingLink
    );

}

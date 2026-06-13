package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.sevicer.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@bestylish.com}")
    private String senderEmail;

    @Override
    @Async // Send emails asynchronously so it doesn't block the HTTP request thread
    public void sendVoucherEmail(
            String toEmail,
            String customerName,
            String voucherCode,
            String voucherName,
            String discountDetails,
            String expiryDate
    ) {
        if (toEmail == null || toEmail.trim().isEmpty() || !toEmail.contains("@")) {
            log.warn("Invalid email address: {}. Skipping email delivery.", toEmail);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(toEmail);
            helper.setSubject("[Bee Stylish] Tặng Bạn Phiếu Giảm Giá Cá Nhân Đặc Biệt: " + voucherCode);

            // Constructing an elegant HTML email template
            String htmlContent = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #f0f0f0; border-radius: 10px; background-color: #ffffff;">
                    <div style="text-align: center; border-bottom: 2px solid #ef972d; padding-bottom: 15px; margin-bottom: 20px;">
                        <h2 style="color: #ef972d; margin: 0; font-size: 24px;">BEE STYLISH</h2>
                        <p style="color: #666; margin: 5px 0 0 0; font-size: 14px;">Thời Trang Phong Cách & Hiện Đại</p>
                    </div>
                    
                    <div style="margin-bottom: 25px;">
                        <p style="font-size: 16px; color: #333; line-height: 1.5;">Chào <strong>%s</strong>,</p>
                        <p style="font-size: 15px; color: #555; line-height: 1.6;">
                            Chúng tôi xin gửi tặng bạn một phiếu giảm giá cá nhân đặc biệt dành riêng cho tài khoản của bạn. Hãy sử dụng mã này trong lần mua hàng tiếp theo để nhận ưu đãi tuyệt vời nhất từ Bee Stylish!
                        </p>
                    </div>
                    
                    <div style="background-color: #fffaf0; border: 1px dashed #ef972d; border-radius: 8px; padding: 20px; text-align: center; margin-bottom: 25px;">
                        <div style="font-size: 13px; color: #888; text-transform: uppercase; letter-spacing: 1px; margin-bottom: 5px;">Mã phiếu giảm giá của bạn</div>
                        <div style="font-size: 26px; font-weight: bold; color: #ef972d; letter-spacing: 2px; margin-bottom: 10px;">%s</div>
                        <div style="font-size: 16px; font-weight: bold; color: #333; margin-bottom: 5px;">Chương trình: %s</div>
                        <div style="font-size: 15px; color: #e07a00; font-weight: bold;">Chi tiết ưu đãi: %s</div>
                    </div>
                    
                    <div style="font-size: 14px; color: #666; margin-bottom: 25px; line-height: 1.5; background-color: #fcfcfc; padding: 12px; border-radius: 6px;">
                        <strong>Thời hạn áp dụng:</strong> Đến hết ngày <strong>%s</strong>
                    </div>
                    
                    <div style="text-align: center; margin-bottom: 20px;">
                        <a href="http://localhost:5173" style="background-color: #ef972d; color: #ffffff; text-decoration: none; padding: 12px 30px; font-size: 15px; font-weight: bold; border-radius: 5px; display: inline-block;">Mua sắm ngay</a>
                    </div>
                    
                    <div style="border-top: 1px solid #eeeeee; padding-top: 15px; text-align: center; font-size: 12px; color: #999;">
                        Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với bộ phận hỗ trợ khách hàng của chúng tôi.<br/>
                        &copy; 2026 Bee Stylish. All rights reserved.
                    </div>
                </div>
                """.formatted(customerName, voucherCode, voucherName, discountDetails, expiryDate);

            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Voucher email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send voucher email to {}: {}. Voucher saved successfully anyway.", toEmail, e.getMessage());
        }
    }

    @Override
    @Async
    public void sendEmployeeAccountEmail(
            String toEmail,
            String employeeName,
            String emailAccount,
            String rawPassword
    ) {
        if (toEmail == null || toEmail.trim().isEmpty() || !toEmail.contains("@")) {
            log.warn("Invalid email address: {}. Skipping account email delivery.", toEmail);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(toEmail);
            helper.setSubject("[Bee Stylish] Thông Báo Cấp Tài Khoản Nhân Viên Mới");

            String htmlContent = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #f0f0f0; border-radius: 10px; background-color: #ffffff;">
                    <div style="text-align: center; border-bottom: 2px solid #ef972d; padding-bottom: 15px; margin-bottom: 20px;">
                        <h2 style="color: #ef972d; margin: 0; font-size: 24px;">BEE STYLISH</h2>
                        <p style="color: #666; margin: 5px 0 0 0; font-size: 14px;">Hệ Thống Quản Lý Nhân Sự</p>
                    </div>
                    
                    <div style="margin-bottom: 25px;">
                        <p style="font-size: 16px; color: #333; line-height: 1.5;">Chào <strong>%s</strong>,</p>
                        <p style="font-size: 15px; color: #555; line-height: 1.6;">
                            Tài khoản nhân viên của bạn đã được tạo thành công trên hệ thống quản trị <strong>Bee Stylish</strong>. Dưới đây là thông tin đăng nhập cá nhân của bạn:
                        </p>
                    </div>
                    
                    <div style="background-color: #fffaf0; border: 1px dashed #ef972d; border-radius: 8px; padding: 20px; margin-bottom: 25px;">
                        <div style="margin-bottom: 10px; font-size: 15px; color: #333;">
                            <strong>Tài khoản (Email):</strong> <span style="color: #2a6496; font-weight: bold;">%s</span>
                        </div>
                        <div style="font-size: 15px; color: #333;">
                            <strong>Mật khẩu mặc định (SĐT):</strong> <span style="color: #ef972d; font-weight: bold;">%s</span>
                        </div>
                    </div>
                    
                    <div style="font-size: 14px; color: #8a6d3b; background-color: #fcf8e3; border: 1px solid #faebcc; padding: 12px; border-radius: 6px; margin-bottom: 25px; line-height: 1.5;">
                        * <strong>Lưu ý:</strong> Vui lòng đăng nhập và tiến hành đổi mật khẩu ngay trong lần đăng nhập đầu tiên để bảo vệ an toàn thông tin cá nhân.
                    </div>
                    
                    <div style="text-align: center; margin-bottom: 20px;">
                        <a href="http://localhost:5173" style="background-color: #ef972d; color: #ffffff; text-decoration: none; padding: 12px 30px; font-size: 15px; font-weight: bold; border-radius: 5px; display: inline-block;">Đăng nhập hệ thống</a>
                    </div>
                    
                    <div style="border-top: 1px solid #eeeeee; padding-top: 15px; text-align: center; font-size: 12px; color: #999;">
                        Nếu bạn không phải là người nhận thư này, vui lòng bỏ qua hoặc thông báo cho quản trị viên hệ thống.<br/>
                        &copy; 2026 Bee Stylish. All rights reserved.
                    </div>
                </div>
                """.formatted(employeeName, emailAccount, rawPassword);

            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Employee account email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send employee account email to {}: {}", toEmail, e.getMessage());
        }
    }
}

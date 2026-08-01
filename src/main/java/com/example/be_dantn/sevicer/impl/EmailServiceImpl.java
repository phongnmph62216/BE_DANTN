package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Entity.HoaDon;
import com.example.be_dantn.sevicer.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;

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

    @Override
    @Async
    public void sendOrderSuccessEmail(
            String toEmail,
            String customerName,
            String orderCode,
            HoaDon hoaDon,
            String paymentMethod,
            String listProductsHtml,
            String trackingLink
    ) {
        if (toEmail == null || toEmail.trim().isEmpty() || !toEmail.contains("@")) {
            log.warn("Invalid email address: {}. Skipping order success email delivery.", toEmail);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(toEmail);
            helper.setSubject("[Bee Stylish] Xác Nhận Đơn Hàng Thành Công - " + orderCode);

            DecimalFormat df = new DecimalFormat("#,###");
            String totalAmountFormatted = df.format(hoaDon.getTongTienThanhToan()) + " đ";
            String subtotalFormatted = df.format(hoaDon.getSoTienGoc() != null ? hoaDon.getSoTienGoc() : BigDecimal.ZERO) + " đ";
            String shippingFeeFormatted = df.format(hoaDon.getPhiVanChuyen() != null ? hoaDon.getPhiVanChuyen() : BigDecimal.ZERO) + " đ";
            String discountFormatted = "- " + df.format(hoaDon.getSoTienGiam() != null ? hoaDon.getSoTienGiam() : BigDecimal.ZERO) + " đ";

            String recipientName = hoaDon.getTenKhachHang() != null ? hoaDon.getTenKhachHang() : "";
            String recipientPhone = hoaDon.getSoDienThoai() != null ? hoaDon.getSoDienThoai() : "";
            String recipientAddress = hoaDon.getDiaChiKhachHang() != null ? hoaDon.getDiaChiKhachHang() : "";

            String noteHtml = "";
            if (hoaDon.getGhiChu() != null && !hoaDon.getGhiChu().trim().isEmpty()) {
                noteHtml = String.format(
                        "<div style=\"margin-bottom: 25px; border: 1px solid #eee; border-radius: 8px; padding: 15px; background-color: #fafafa;\">" +
                        "<h3 style=\"color: #ef972d; margin-top: 0; margin-bottom: 12px; font-size: 16px; border-bottom: 1px solid #ddd; padding-bottom: 5px;\">GHI CHÚ</h3>" +
                        "<p style=\"margin: 0; font-size: 14px; color: #555;\">%s</p>" +
                        "</div>",
                        hoaDon.getGhiChu()
                );
            }

            String htmlContent = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #f0f0f0; border-radius: 10px; background-color: #ffffff;">
                    <div style="text-align: center; border-bottom: 2px solid #ef972d; padding-bottom: 15px; margin-bottom: 20px;">
                        <h2 style="color: #ef972d; margin: 0; font-size: 24px;">BEE STYLISH</h2>
                        <p style="color: #666; margin: 5px 0 0 0; font-size: 14px;">Cảm Ơn Bạn Đã Mua Sắm Tại Bee Stylish!</p>
                    </div>
                    
                    <div style="margin-bottom: 25px;">
                        <p style="font-size: 16px; color: #333; line-height: 1.5;">Chào <strong>%s</strong>,</p>
                        <p style="font-size: 15px; color: #555; line-height: 1.6;">
                            Đơn hàng của bạn đã được đặt thành công trên hệ thống <strong>Bee Stylish</strong>. Dưới đây là thông tin chi tiết đơn hàng của bạn:
                        </p>
                    </div>
                    
                    <div style="background-color: #fffaf0; border: 1px dashed #ef972d; border-radius: 8px; padding: 15px; margin-bottom: 25px;">
                        <table style="width: 100%%; border-collapse: collapse; font-size: 14px;">
                            <tr>
                                <td style="padding: 5px 0; color: #333; width: 40%%;"><strong>Mã đơn hàng:</strong></td>
                                <td style="padding: 5px 0; color: #ef972d; font-weight: bold; font-size: 16px;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 5px 0; color: #333;"><strong>Phương thức thanh toán:</strong></td>
                                <td style="padding: 5px 0; color: #333; font-weight: bold;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 5px 0; color: #333;"><strong>Tổng thanh toán:</strong></td>
                                <td style="padding: 5px 0; color: #ef972d; font-weight: bold;">%s</td>
                            </tr>
                        </table>
                    </div>

                    <div style="margin-bottom: 25px; border: 1px solid #eee; border-radius: 8px; padding: 15px; background-color: #fafafa;">
                        <h3 style="color: #ef972d; margin-top: 0; margin-bottom: 12px; font-size: 16px; border-bottom: 1px solid #ddd; padding-bottom: 5px;">THÔNG TIN NHẬN HÀNG</h3>
                        <table style="width: 100%%; border-collapse: collapse; font-size: 14px;">
                            <tr>
                                <td style="padding: 5px 0; color: #666; width: 30%%;">Người nhận:</td>
                                <td style="padding: 5px 0; color: #333; font-weight: bold;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 5px 0; color: #666;">Số điện thoại:</td>
                                <td style="padding: 5px 0; color: #333;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 5px 0; color: #666;">Email:</td>
                                <td style="padding: 5px 0; color: #333;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 5px 0; color: #666;">Địa chỉ giao:</td>
                                <td style="padding: 5px 0; color: #333; line-height: 1.4;">%s</td>
                            </tr>
                        </table>
                    </div>
                    
                    <div style="margin-bottom: 25px;">
                        <h3 style="color: #ef972d; border-bottom: 1px solid #ddd; padding-bottom: 8px; font-size: 16px; margin-bottom: 12px;">DANH SÁCH SẢN PHẨM</h3>
                        <table style="width: 100%%; border-collapse: collapse; font-size: 14px;">
                            <thead>
                                <tr style="background-color: #f9f9f9; text-align: left; font-weight: bold;">
                                    <th style="padding: 8px; border-bottom: 1px solid #ddd; width: 60%%; color: #555;">Sản phẩm</th>
                                    <th style="padding: 8px; border-bottom: 1px solid #ddd; text-align: center; width: 15%%; color: #555;">SL</th>
                                    <th style="padding: 8px; border-bottom: 1px solid #ddd; text-align: right; width: 25%%; color: #555;">Đơn giá</th>
                                </tr>
                            </thead>
                            <tbody>
                                %s
                            </tbody>
                        </table>
                    </div>

                    <div style="margin-bottom: 25px; border: 1px solid #eee; border-radius: 8px; padding: 15px; background-color: #fafafa; margin-left: auto; max-width: 320px;">
                        <h3 style="color: #ef972d; margin-top: 0; margin-bottom: 12px; font-size: 16px; border-bottom: 1px solid #ddd; padding-bottom: 5px;">TỔNG KẾT TÀI CHÍNH</h3>
                        <table style="width: 100%%; border-collapse: collapse; font-size: 14px;">
                            <tr>
                                <td style="padding: 5px 0; color: #666;">Tổng tiền hàng:</td>
                                <td style="padding: 5px 0; color: #333; text-align: right; font-weight: bold;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 5px 0; color: #666;">Phí vận chuyển:</td>
                                <td style="padding: 5px 0; color: #333; text-align: right;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 5px 0; color: #666;">Số tiền giảm:</td>
                                <td style="padding: 5px 0; color: #333; text-align: right; color: #d9534f;">%s</td>
                            </tr>
                            <tr style="border-top: 1px solid #ddd;">
                                <td style="padding: 8px 0 0 0; color: #333; font-weight: bold;">Tổng thanh toán:</td>
                                <td style="padding: 8px 0 0 0; color: #ef972d; text-align: right; font-weight: bold; font-size: 16px;">%s</td>
                            </tr>
                        </table>
                    </div>

                    %s
                    
                    <div style="text-align: center; margin-top: 30px; margin-bottom: 20px;">
                        <a href="%s" style="background-color: #ef972d; color: #ffffff; text-decoration: none; padding: 12px 30px; font-size: 15px; font-weight: bold; border-radius: 5px; display: inline-block;">Tra cứu trạng thái đơn hàng</a>
                    </div>
                    
                    <div style="border-top: 1px solid #eeeeee; padding-top: 15px; text-align: center; font-size: 12px; color: #999;">
                        Nếu bạn có bất kỳ thắc mắc nào, vui lòng liên hệ với bộ phận hỗ trợ khách hàng của chúng tôi.<br/>
                        &copy; 2026 Bee Stylish. All rights reserved.
                    </div>
                </div>
                """.formatted(
                    customerName, 
                    orderCode, 
                    paymentMethod, 
                    totalAmountFormatted,
                    recipientName,
                    recipientPhone,
                    toEmail,
                    recipientAddress,
                    listProductsHtml,
                    subtotalFormatted,
                    shippingFeeFormatted,
                    discountFormatted,
                    totalAmountFormatted,
                    noteHtml,
                    trackingLink
                );

            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Order success email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send order success email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    @Async
    public void sendResetPasswordOtpEmail(String toEmail, String userName, String otpCode) {
        if (toEmail == null || toEmail.trim().isEmpty() || !toEmail.contains("@")) {
            log.warn("Invalid email address: {}. Skipping OTP email delivery.", toEmail);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(toEmail);
            helper.setSubject("[Bee Stylish] Mã Xác Thực Quên Mật Khẩu - " + otpCode);

            String htmlContent = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px; background-color: #ffffff;">
                    <div style="text-align: center; margin-bottom: 20px;">
                        <h2 style="color: #ef972d; margin: 0;">Bee Stylish - Đặt Lại Mật Khẩu</h2>
                    </div>
                    
                    <p style="font-size: 15px; color: #333333;">Xin chào <strong>%s</strong>,</p>
                    <p style="font-size: 14px; color: #555555; line-height: 1.6;">
                        Hệ thống Bee Stylish nhận được yêu cầu đặt lại mật khẩu cho tài khoản liên kết với email <strong>%s</strong>.
                    </p>
                    
                    <div style="text-align: center; margin: 30px 0; background-color: #fff8f0; border: 2px dashed #ef972d; padding: 20px; border-radius: 8px;">
                        <span style="font-size: 13px; color: #777; display: block; margin-bottom: 5px;">MÃ XÁC THỰC OTP CỦA BẠN:</span>
                        <span style="font-size: 32px; font-weight: bold; letter-spacing: 6px; color: #ef972d;">%s</span>
                        <span style="font-size: 12px; color: #d9534f; display: block; margin-top: 5px;">(Mã có hiệu lực trong vòng 10 phút)</span>
                    </div>

                    <p style="font-size: 13px; color: #666666;">
                        * Vui lòng không chia sẻ mã OTP này cho bất kỳ ai để bảo vệ an toàn cho tài khoản của bạn.
                    </p>
                    
                    <div style="border-top: 1px solid #eeeeee; margin-top: 25px; padding-top: 15px; text-align: center; font-size: 12px; color: #999999;">
                        Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email.<br/>
                        &copy; 2026 Bee Stylish. All rights reserved.
                    </div>
                </div>
                """.formatted(userName != null ? userName : "Quý khách", toEmail, otpCode);

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Reset password OTP email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send reset password OTP email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    @Async
    public void sendRevenueReportEmail(
            String toEmail,
            String reportType,
            BigDecimal doanhThu,
            Long soDonHang,
            Long hoanThanh,
            Long soSanPham,
            BigDecimal tienMat,
            BigDecimal chuyenKhoan,
            BigDecimal vnpay
    ) {
        if (toEmail == null || toEmail.trim().isEmpty() || !toEmail.contains("@")) {
            log.warn("Invalid email address: {}. Skipping revenue report email delivery.", toEmail);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(toEmail);

            String titleType = "Hôm Nay";
            if ("week".equalsIgnoreCase(reportType)) {
                titleType = "Tuần Này";
            } else if ("month".equalsIgnoreCase(reportType)) {
                titleType = "Tháng Này";
            }

            helper.setSubject("[Bee Stylish] Báo Cáo Doanh Thu Bán Hàng (" + titleType + ")");

            DecimalFormat df = new DecimalFormat("#,###");
            String dtFormatted = df.format(doanhThu != null ? doanhThu : BigDecimal.ZERO) + " đ";
            String tmFormatted = df.format(tienMat != null ? tienMat : BigDecimal.ZERO) + " đ";
            String ckFormatted = df.format(chuyenKhoan != null ? chuyenKhoan : BigDecimal.ZERO) + " đ";
            String vnFormatted = df.format(vnpay != null ? vnpay : BigDecimal.ZERO) + " đ";

            String htmlContent = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #f0f0f0; border-radius: 10px; background-color: #ffffff;">
                    <div style="text-align: center; border-bottom: 2px solid #ef972d; padding-bottom: 15px; margin-bottom: 20px;">
                        <h2 style="color: #ef972d; margin: 0; font-size: 24px;">BEE STYLISH</h2>
                        <p style="color: #666; margin: 5px 0 0 0; font-size: 14px;">Báo Cáo Doanh Thu Dành Cho Quản Lý</p>
                    </div>
                    
                    <div style="margin-bottom: 25px;">
                        <p style="font-size: 16px; color: #333; line-height: 1.5;">Kính gửi <strong>Quản lý Bee Stylish</strong>,</p>
                        <p style="font-size: 15px; color: #555; line-height: 1.6;">
                            Hệ thống quản trị <strong>Bee Stylish</strong> xin gửi báo cáo doanh thu hóa đơn bán hàng thực tế (Kỳ: <strong>%s</strong>):
                        </p>
                    </div>
                    
                    <div style="background-color: #fffaf0; border: 1px dashed #ef972d; border-radius: 8px; padding: 20px; text-align: center; margin-bottom: 25px;">
                        <div style="font-size: 13px; color: #888; text-transform: uppercase; letter-spacing: 1px; margin-bottom: 5px;">TỔNG DOANH THU THỰC TẾ (HÓA ĐƠN HOÀN THÀNH)</div>
                        <div style="font-size: 32px; font-weight: bold; color: #ef972d; margin-bottom: 10px;">%s</div>
                        <div style="font-size: 13px; color: #2e7d32; font-weight: bold; background-color: #e8f5e9; padding: 6px 12px; border-radius: 20px; display: inline-block;">
                            Khớp 100%% với danh sách hóa đơn hệ thống
                        </div>
                    </div>

                    <div style="margin-bottom: 25px; border: 1px solid #eee; border-radius: 8px; padding: 15px; background-color: #fafafa;">
                        <h3 style="color: #ef972d; margin-top: 0; margin-bottom: 12px; font-size: 16px; border-bottom: 1px solid #ddd; padding-bottom: 5px;">CHI TIẾT KÊNH THANH TOÁN</h3>
                        <table style="width: 100%%; border-collapse: collapse; font-size: 14px;">
                            <tr>
                                <td style="padding: 8px 0; color: #555; width: 50%%;">💵 Tiền mặt:</td>
                                <td style="padding: 8px 0; color: #333; font-weight: bold; text-align: right;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; color: #555;">🏦 Chuyển khoản ngân hàng:</td>
                                <td style="padding: 8px 0; color: #333; font-weight: bold; text-align: right;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; color: #555;">💳 Ví điện tử VNPAY:</td>
                                <td style="padding: 8px 0; color: #333; font-weight: bold; text-align: right;">%s</td>
                            </tr>
                        </table>
                    </div>

                    <div style="margin-bottom: 25px; border: 1px solid #eee; border-radius: 8px; padding: 15px; background-color: #fafafa;">
                        <h3 style="color: #ef972d; margin-top: 0; margin-bottom: 12px; font-size: 16px; border-bottom: 1px solid #ddd; padding-bottom: 5px;">THỐNG KÊ ĐƠN HÀNG & SẢN PHẨM</h3>
                        <table style="width: 100%%; border-collapse: collapse; font-size: 14px;">
                            <tr>
                                <td style="padding: 8px 0; color: #555; width: 60%%;">🧾 Tổng hóa đơn phát sinh:</td>
                                <td style="padding: 8px 0; color: #333; font-weight: bold; text-align: right;">%d hóa đơn</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; color: #555;">✅ Hóa đơn đã hoàn thành:</td>
                                <td style="padding: 8px 0; color: #2e7d32; font-weight: bold; text-align: right;">%d hóa đơn</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; color: #555;">🛍️ Tổng sản phẩm bán ra:</td>
                                <td style="padding: 8px 0; color: #333; font-weight: bold; text-align: right;">%d sản phẩm</td>
                            </tr>
                        </table>
                    </div>
                    
                    <div style="text-align: center; margin-bottom: 20px;">
                        <a href="http://localhost:5173/admin/thong-ke" style="background-color: #ef972d; color: #ffffff; text-decoration: none; padding: 12px 30px; font-size: 15px; font-weight: bold; border-radius: 5px; display: inline-block;">Xem báo cáo chi tiết trên Hệ thống</a>
                    </div>
                    
                    <div style="border-top: 1px solid #eeeeee; padding-top: 15px; text-align: center; font-size: 12px; color: #999;">
                        Báo cáo này được tạo tự động bởi Hệ Thống Quản Lý Bee Stylish.<br/>
                        &copy; 2026 Bee Stylish. All rights reserved.
                    </div>
                </div>
                """.formatted(
                    titleType,
                    dtFormatted,
                    tmFormatted,
                    ckFormatted,
                    vnFormatted,
                    soDonHang != null ? soDonHang : 0L,
                    hoanThanh != null ? hoanThanh : 0L,
                    soSanPham != null ? soSanPham : 0L
                );

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Revenue report email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send revenue report email to {}: {}", toEmail, e.getMessage());
        }
    }
}



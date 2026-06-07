package com.example.be_dantn.sevicer;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendNhanVienAccount(
            String email,
            String hoTen,
            String matKhau
    ) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);

        message.setSubject("Thông tin tài khoản nhân viên");

        message.setText(
                "Xin chào " + hoTen + "\n\n" +
                        "Tài khoản của bạn đã được tạo thành công.\n\n" +
                        "Email: " + email + "\n" +
                        "Mật khẩu: " + matKhau + "\n\n" +
                        "Cảm ơn bạn đã đồng hành cùng công ty."
        );
        System.out.println("===== BAT DAU GUI MAIL =====");
        System.out.println("Email: " + email);
        System.out.println("Ho ten: " + hoTen);
        try {

            mailSender.send(message);

            System.out.println("GUI MAIL THANH CONG");

        } catch (Exception e) {

            System.out.println("LOI GUI MAIL");

            e.printStackTrace();
        }
    }
}
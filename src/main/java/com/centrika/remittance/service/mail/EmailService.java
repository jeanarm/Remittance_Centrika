package com.centrika.remittance.service.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${FRONTEND_URL}")
    private String frontendUrl;
    private final JavaMailSender mailSender;

    public void sendOtpEmail(String email, String otpCode) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Your OTP Code");
        mailMessage.setText("Your OTP code is: " + otpCode);
        mailSender.send(mailMessage);
    }
   public void sendPasswordResetEmail(String email, String resetToken) {
       String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;

       SimpleMailMessage mailMessage = new SimpleMailMessage();
       mailMessage.setTo(email);
       mailMessage.setSubject("Reset Your Password");
       mailMessage.setText("Click the link below to reset your password:\n\n" + resetUrl +
               "\n\nThis link expires in 1 hour for security reasons.");

       mailSender.send(mailMessage);

    }
}

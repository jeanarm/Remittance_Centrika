package com.centrika.remittance.service;

import com.centrika.remittance.dto.*;
import com.centrika.remittance.model.Role;
import com.centrika.remittance.model.User;
import com.centrika.remittance.repository.RoleRepository;
import com.centrika.remittance.repository.UserRepository;
import com.centrika.remittance.util.OtpUtil;
import com.centrika.remittance.util.PasswordUtil;
import com.centrika.remittance.util.JwtUtil;
import com.centrika.remittance.exception.ValidationExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.centrika.remittance.model.PasswordResetToken;
import com.centrika.remittance.service.mail.EmailService;
import com.centrika.remittance.repository.PasswordResetTokenRepository;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JavaMailSender mailSender;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Transactional
    public UserResponse registerUser(RegisterUserRequest request) {
        validatePhoneNumber(request.getTelephoneNo());
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationExceptionHandler("Email already exists!");
        }

        Role role = roleRepository.findByName("Business_Owner")
                .orElseThrow(() -> new ValidationExceptionHandler("Default role not found!"));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .dateOfBirth(request.getDateOfBirth())
                .telephoneNo(request.getTelephoneNo())
                .isVerified(false)
                .role(role)
                .build();

        generateAndSendOTP(user);
        userRepository.save(user);
        return new UserResponse(user.getEmail(), "User registered successfully. OTP sent.");
    }

    public UserResponse verifyOtp(VerifyOtpRequest request) {
        User user = userRepository.findValidOtpUser(request.getEmail(), request.getOtpCode())
                .orElseThrow(() -> new ValidationExceptionHandler("Invalid OTP or expired!"));

        user.setVerified(true);
        user.setOtpCode(null);
        user.setOtpExpiryTime(null);

        userRepository.save(user);
        return new UserResponse(user.getEmail(), "User verified successfully.");
    }

    public UserResponse setPassword(SetPasswordRequest request) {
        validatePassword(request.getPassword(), request.getConfirmPassword());

        User user = userRepository.findVerifiedUser(request.getEmail())
                .orElseThrow(() -> new ValidationExceptionHandler("User is not verified!"));

        user.setPassword(PasswordUtil.hashPassword(request.getPassword()));
        userRepository.save(user);
        return new UserResponse(user.getEmail(), "Password created successfully.");
    }

    public UserResponse initiateLogin(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ValidationExceptionHandler("Invalid email or password"));

        if (!PasswordUtil.matches(request.getPassword(), user.getPassword())) {
            throw new ValidationExceptionHandler("Invalid email or password");
        }

        generateAndSendOTP(user);

        return new UserResponse(user.getEmail(), "OTP sent for login verification.");
    }

    public LoginResponse completeLogin(VerifyOtpRequest request) {
        User user = userRepository.findValidOtpUser(request.getEmail(), request.getOtpCode())
                .orElseThrow(() -> new ValidationExceptionHandler("Invalid OTP or expired!"));

        user.setOtpCode(null);
        user.setOtpExpiryTime(null);
        userRepository.save(user);

        String token = jwtUtil.generateToken(user);
        return new LoginResponse(user.getEmail(), token);
    }

    @Transactional
    public UserResponse forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ValidationExceptionHandler("User not found!"));

        // ✅ Generate a unique token
        String resetToken = UUID.randomUUID().toString();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(60); // Token valid for 60 minutes

        // ✅ Remove old tokens
        passwordResetTokenRepository.findByUser(user)
                .ifPresent(passwordResetTokenRepository::delete);

        // ✅ Save new reset token
        PasswordResetToken token = PasswordResetToken.builder()
                .token(resetToken)
                .expiryTime(expiryTime)
                .user(user)
                .build();
        passwordResetTokenRepository.save(token);

        // ✅ Send the email
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);

        return new UserResponse(user.getEmail(), "Password reset token sent to email.");
    }

    @Transactional
    public UserResponse resetPassword(ResetPasswordRequest request) {
        PasswordResetToken token = passwordResetTokenRepository.findByToken(request.getResetToken())
                .orElseThrow(() -> new ValidationExceptionHandler("Invalid or expired reset token!"));

        // ✅ Ensure token is not expired
        if (token.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new ValidationExceptionHandler("Reset token has expired!");
        }

        // ✅ Validate password
        validatePassword(request.getPassword(), request.getConfirmPassword());

        // ✅ Update user's password
        User user = token.getUser();
        user.setPassword(PasswordUtil.hashPassword(request.getPassword()));
        userRepository.save(user);

        // ✅ Delete the token after successful reset
        passwordResetTokenRepository.delete(token);

        return new UserResponse(user.getEmail(), "Password has been reset successfully.");
    }
    private void generateAndSendOTP(User user) {
        String otpCode = OtpUtil.generateOtp();
        user.setOtpCode(otpCode);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(60));
        userRepository.save(user);

        emailService.sendOtpEmail(user.getEmail(), otpCode);
    }


    private void validatePassword(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new ValidationExceptionHandler("Passwords do not match!");
        }

        // ✅ Password must have at least 8 characters, 1 uppercase, 1 lowercase, 1 number, and 1 special character
        if (!password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            throw new ValidationExceptionHandler(
                    "Password must be at least 8 characters long, include at least one uppercase letter, one lowercase letter, one number, and one special character."
            );
        }

}
    private void validatePhoneNumber(String phoneNumber) {
        if (!phoneNumber.matches("\\d{9,}")) {
            throw new ValidationExceptionHandler("Phone number must contain only digits and be at least 9 characters long.");
        }
    }

}
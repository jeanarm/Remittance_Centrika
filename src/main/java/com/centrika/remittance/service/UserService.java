package com.centrika.remittance.service;

import com.centrika.remittance.dto.RegisterUserRequest;
import com.centrika.remittance.dto.VerifyOtpRequest;
import com.centrika.remittance.dto.SetPasswordRequest;
import com.centrika.remittance.model.Role;
import com.centrika.remittance.model.User;
import com.centrika.remittance.repository.RoleRepository;
import com.centrika.remittance.repository.UserRepository;
import com.centrika.remittance.util.OtpUtil;
import com.centrika.remittance.util.PasswordUtil;
import com.centrika.remittance.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.centrika.remittance.dto.UserResponse;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.authentication.BadCredentialsException;
import com.centrika.remittance.dto.LoginRequest;
import com.centrika.remittance.dto.LoginResponse;
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JavaMailSender mailSender;
    private final JwtUtil jwtUtil;
    /**
     * Step 1: Register User and Send OTP
     */
    @Transactional
    public UserResponse registerUser(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already registered!");
        }

        Role role = roleRepository.findByName("Business_Owner")
                .orElseThrow(() -> new IllegalStateException("Default role not found!"));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .dateOfBirth(request.getDateOfBirth())
                .telephoneNo(request.getTelephoneNo())
                .isVerified(false)
                .role(role)
                .build();

        generateAndSendOTP(user);
        User savedUser = userRepository.save(user);
        return new UserResponse(user.getEmail(), "User registered successfully and OTP sent.");
    }

    /**
     * Step 2: Verify OTP
     */
    public UserResponse verifyOtp(VerifyOtpRequest request) {
        User user = userRepository.findValidOtpUser(request.getEmail(), request.getOtpCode())
                .orElseThrow(() -> new IllegalStateException("Invalid OTP or expired!"));

        user.setVerified(true);
        user.setOtpCode(null);
        user.setOtpExpiryTime(null);

        User savedUser = userRepository.save(user);
        return new UserResponse(user.getEmail(), "User verified successfully");
    }

    /**
     * Step 3: Set Password
     */
    public UserResponse setPassword(SetPasswordRequest request) {
        User user = userRepository.findVerifiedUser(request.getEmail())
                .orElseThrow(() -> new IllegalStateException("User is not verified!"));

        user.setPassword(PasswordUtil.hashPassword(request.getPassword()));
        User savedUser = userRepository.save(user);
        return new UserResponse(user.getEmail(), "Password created successfully");
    }

    private void generateAndSendOTP(User user) {
        String otpCode = OtpUtil.generateOtp();
        user.setOtpCode(otpCode);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(60));
        userRepository.save(user);

        sendOtpEmail(user.getEmail(), otpCode);

    }

    private void sendOtpEmail(String email, String otpCode) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Verify Your Account");
        mailMessage.setText("Your OTP code is: " + otpCode);
        mailSender.send(mailMessage);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!PasswordUtil.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user);
        return new LoginResponse(user.getEmail(), token);
    }
}
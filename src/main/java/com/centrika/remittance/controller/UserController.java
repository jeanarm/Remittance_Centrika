package com.centrika.remittance.controller;

import com.centrika.remittance.dto.*;
import com.centrika.remittance.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Step 1: Register User and Send OTP
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody RegisterUserRequest request) {
        return ResponseEntity.ok(userService.registerUser(request));
    }

    /**
     * Step 2: Verify OTP
     */
    @PostMapping("/verify")
    public ResponseEntity<UserResponse> verifyOtp(@RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(userService.verifyOtp(request));
    }

    /**
     * Step 3: Set Password
     */
    @PostMapping("/set-password")
    public ResponseEntity<UserResponse> setPassword(@RequestBody SetPasswordRequest request) {
        return ResponseEntity.ok(userService.setPassword(request));
    }

    /**
     * Step 4: Initiate Login (2FA - Send OTP)
     */
    @PostMapping("/login/initiate")
    public ResponseEntity<UserResponse> initiateLogin(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.initiateLogin(request));
    }

    /**
     * Step 5: Complete Login (Verify OTP & Generate JWT)
     */
    @PostMapping("/login/complete")
    public ResponseEntity<LoginResponse> completeLogin(@RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(userService.completeLogin(request));
    }

    /**
     * Step 6: Forgot Password - Send Reset Token
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<UserResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(userService.forgotPassword(request));
    }

    /**
     * Step 7: Reset Password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<UserResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(userService.resetPassword(request));
    }
}
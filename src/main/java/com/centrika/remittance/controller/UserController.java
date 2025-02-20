package com.centrika.remittance.controller;

import com.centrika.remittance.dto.RegisterUserRequest;
import com.centrika.remittance.dto.VerifyOtpRequest;
import com.centrika.remittance.dto.SetPasswordRequest;
import com.centrika.remittance.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.centrika.remittance.dto.UserResponse;
import com.centrika.remittance.dto.LoginRequest;
import com.centrika.remittance.dto.LoginResponse;
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
        UserResponse response = userService.registerUser(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Step 2: Verify OTP
     */
    @PostMapping("/verify")
    public ResponseEntity<UserResponse> verifyOtp(@RequestBody VerifyOtpRequest request) {
        UserResponse response  =userService.verifyOtp(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Step 3: Set Password
     */
    @PostMapping("/set-password")
    public ResponseEntity<UserResponse> setPassword(@RequestBody SetPasswordRequest request) {
        UserResponse response = userService.setPassword(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}
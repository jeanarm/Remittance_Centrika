package com.centrika.remittance.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String resetToken;
    private String password;
    private String confirmPassword;
}
package com.centrika.remittance.dto;

import lombok.Value;

@Value
public class LoginResponse {
    String email;
    String token;
}

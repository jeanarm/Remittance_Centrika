package com.centrika.remittance.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetPasswordRequest {
    private String email;
    private String password;
}

package com.centrika.remittance.util;

import org.springframework.stereotype.Component;
import java.util.Random;

@Component
public class OtpUtil {
    private static final Random RANDOM = new Random();

    public static String generateOtp() {
        return String.valueOf(100000 + RANDOM.nextInt(900000));
    }
}
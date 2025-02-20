package com.centrika.remittance.service;
import com.centrika.remittance.dto.LoginRequest;
import com.centrika.remittance.model.User;
import com.centrika.remittance.repository.UserRepository;
import com.centrika.remittance.util.JwtUtil;
import com.centrika.remittance.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!PasswordUtil.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(user);
    }
}

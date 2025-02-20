package com.centrika.remittance.repository;

import com.centrika.remittance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Transactional
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.otpCode = :otpCode AND u.otpExpiryTime > CURRENT_TIMESTAMP")
    Optional<User> findValidOtpUser(String email, String otpCode);

    @Transactional
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isVerified = true")
    Optional<User> findVerifiedUser(String email);
}
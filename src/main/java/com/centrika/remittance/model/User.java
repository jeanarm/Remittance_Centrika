package com.centrika.remittance.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String dateOfBirth;

    @Column
    private String telephoneNo;

    @OneToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    private boolean isVerified;

    // OTP fields
    private String otpCode;
    private LocalDateTime otpExpiryTime;
}
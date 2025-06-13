package com.cb.carberus.user.model;

import com.cb.carberus.constants.UserRole;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "system_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String email;

    @Column(name = "hashed_password")
    private String password;

    @Column(name = "isActive", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole userRole;

    @PrePersist
    protected void onCreate() {
        if(this.isActive == null) {
            this.isActive = true;
        }

        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }


}

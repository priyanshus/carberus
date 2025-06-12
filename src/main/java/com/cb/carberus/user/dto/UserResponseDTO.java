package com.cb.carberus.user.dto;

import com.cb.carberus.constants.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDTO {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private Role role;
}

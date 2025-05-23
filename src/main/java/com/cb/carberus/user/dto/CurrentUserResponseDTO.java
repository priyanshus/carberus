package com.cb.carberus.user.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CurrentUserResponseDTO {
    private String id;
    private String email;
    private LocalDateTime createdAt;
    private List<String> roles;
}

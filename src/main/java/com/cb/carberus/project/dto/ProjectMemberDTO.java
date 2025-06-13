package com.cb.carberus.project.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProjectMemberDTO {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String projectRole;
    private LocalDateTime addedAt;
    private LocalDateTime updatedAt;

}

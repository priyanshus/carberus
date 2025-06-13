package com.cb.carberus.project.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ProjectDTO {
    private Long id;
    private String name;
    private String description;
    private String projectCode;
    private String status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ProjectMemberDTO> members;
}

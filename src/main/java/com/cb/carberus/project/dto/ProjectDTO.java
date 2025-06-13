package com.cb.carberus.project.dto;

import com.cb.carberus.project.model.ProjectStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProjectDTO {
    @Id
    private String id;
    private String name;
    private String description;
    private String prefix;
    private LocalDateTime createdAt;
    private String createdBy;
    private String lastModifiedBy;
    private ProjectStatus status;
    private List<ProjectMemberDTO> members;
}

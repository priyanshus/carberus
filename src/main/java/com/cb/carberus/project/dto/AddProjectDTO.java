package com.cb.carberus.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddProjectDTO {
    @NotBlank @Size(max=50, message = "Title must be at most 50 characters")
    private String name;

    @Size(max=100, message = "Description must be at most 100 characters")
    private String description;

    @NotBlank @Size(max=4, message = "Project Code must be at most 4 characters")
    private String projectCode;
}

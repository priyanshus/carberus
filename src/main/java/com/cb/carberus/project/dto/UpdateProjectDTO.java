package com.cb.carberus.project.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpdateProjectDTO {
    @Size(max=50, message = "Title must be at most 50 characters")
    private String name;

    @Size(max=100, message = "Description must be at most 100 characters")
    private String description;

    @NotNull(message = "Status must be present")
    private String status;
}

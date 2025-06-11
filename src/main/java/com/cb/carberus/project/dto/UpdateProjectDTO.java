package com.cb.carberus.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class UpdateProjectDTO {
    @Id
    @NotBlank(message = "Id must be present")
    private String id;

    @NotBlank
    @Size(max=50, message = "Title must be at most 50 characters")
    private String name;

    @Size(max=100, message = "Description must be at most 100 characters")
    private String description;



}

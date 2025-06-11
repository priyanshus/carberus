package com.cb.carberus.project.dto;

import com.cb.carberus.project.model.ProjectStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeProjectStatusDTO {
    @NotNull(message = "Project status must be present")
    private ProjectStatus projectStatus;
}

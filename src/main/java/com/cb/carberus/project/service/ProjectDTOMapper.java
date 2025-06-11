package com.cb.carberus.project.service;

import com.cb.carberus.project.dto.AddProjectDTO;
import com.cb.carberus.project.dto.UpdateProjectDTO;
import com.cb.carberus.project.model.Project;
import com.cb.carberus.project.model.ProjectStatus;

public class ProjectDTOMapper {

    public static Project toProject(AddProjectDTO dto) {
        Project project = new Project();

        project.setName(dto.getName());

        if (dto.getDescription() != null) {
            project.setDescription(dto.getDescription());
        }

        if (dto.getPrefix() != null) {
            project.setPrefix(dto.getPrefix());
        }

        project.setStatus(ProjectStatus.ACTIVE);

        return project;
    }

    public static Project toUpdateProject(UpdateProjectDTO dto) {
        Project project = new Project();

        project.setName(dto.getName());

        if (dto.getDescription() != null) {
            project.setDescription(dto.getDescription());
        }

        return project;
    }
}

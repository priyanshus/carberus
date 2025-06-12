package com.cb.carberus.project;

import com.cb.carberus.project.model.Project;
import com.cb.carberus.project.model.ProjectStatus;

import java.time.LocalDateTime;

public class ProjectTestUtil {

    public static Project setProject(ProjectStatus status) {
        Project project = new Project();
        project.setId("some-id");
        project.setName("some-name");
        project.setPrefix("prfx");
        project.setDescription("some-description");
        project.setCreatedAt(LocalDateTime.now());
        project.setStatus(status);

        return project;
    }
}

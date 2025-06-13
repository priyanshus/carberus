package com.cb.carberus.project.service;

import com.cb.carberus.authorization.service.AdminPermission;
import com.cb.carberus.config.UserContext;
import com.cb.carberus.constants.UserRole;
import com.cb.carberus.errorHandler.error.DomainException;
import com.cb.carberus.errorHandler.error.StandardApiException;
import com.cb.carberus.errorHandler.model.DomainErrorCode;
import com.cb.carberus.errorHandler.model.StandardErrorCode;
import com.cb.carberus.project.dto.AddProjectDTO;
import com.cb.carberus.project.dto.ChangeProjectStatusDTO;
import com.cb.carberus.project.dto.ProjectDTO;
import com.cb.carberus.project.dto.UpdateProjectDTO;
import com.cb.carberus.project.model.Project;
import com.cb.carberus.project.model.ProjectStatus;
import com.cb.carberus.project.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    @Autowired
    private final ProjectRepository projectRepository;
    private final UserContext userContext;
    private final AdminPermission adminPermission;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, UserContext userContext, AdminPermission adminPermission) {
        this.projectRepository = projectRepository;
        this.userContext = userContext;
        this.adminPermission = adminPermission;
    }

    private UserRole getCurrentRole() {
        return userContext.getRole();
    }

    private Long getUserId() {
        return userContext.getUserId();
    }

    public void addProject(AddProjectDTO dto) {
        var role = getCurrentRole();
        var canAdd = adminPermission.canAdd(role);

        if(!canAdd) {
            throw new StandardApiException(StandardErrorCode.UNAUTHORIZED);
        }

        Project project = ProjectDTOMapper.toProject(dto);
        List<Project> projectsInDb = projectRepository.findAll();

        boolean projectTitleAlreadyTaken = projectsInDb
                .stream()
                .anyMatch(p -> p.getName() != null && p.getName().equalsIgnoreCase(dto.getName()));

        boolean projectPrefixAlreadyTaken = projectsInDb
                .stream()
                .anyMatch(p -> p.getPrefix() != null && p.getPrefix().equalsIgnoreCase(dto.getPrefix()));

        if (projectTitleAlreadyTaken) {
            throw new DomainException(DomainErrorCode.PROJECT_NAME_ALREADY_EXIST);
        }

        if (projectPrefixAlreadyTaken) {
            throw new DomainException(DomainErrorCode.PROJECT_PREFIX_ALREADY_EXIST);
        }

        projectRepository.save(project);
    }

    public void updateProject(UpdateProjectDTO dto) {
        var role = getCurrentRole();
        var canAdd = adminPermission.canAdd(role);

        if(!canAdd) {
            throw new StandardApiException(StandardErrorCode.UNAUTHORIZED);
        }

        Project project = projectRepository.findById(dto.getId()).orElseThrow(
                () -> new StandardApiException(StandardErrorCode.NOT_FOUND));

        if (project.getStatus().equals(ProjectStatus.ARCHIVED)) {
            throw new DomainException(DomainErrorCode.PROJECT_IN_ARCHIVED_STATE);
        }

        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        projectRepository.save(project);
    }

    public void changeProjectStatus(String projectId, ChangeProjectStatusDTO dto) {
        var role = getCurrentRole();
        var canAdd = adminPermission.canAdd(role);

        if(!canAdd) {
            throw new StandardApiException(StandardErrorCode.UNAUTHORIZED);
        }

        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new StandardApiException(StandardErrorCode.NOT_FOUND));


        if (project.getStatus() != dto.getProjectStatus()) {
            project.setStatus(dto.getProjectStatus());
            projectRepository.save(project);
        }
    }

    public List<ProjectDTO> getProjects() {
        var role = getCurrentRole();
        var canView = adminPermission.canView(role);

        if(!canView) {
            throw new StandardApiException(StandardErrorCode.UNAUTHORIZED);
        }

        return List.of(new ProjectDTO());
        //return projectRepository.findAll();
    }

    public Project getProject(String id) {
        var role = getCurrentRole();
        var canView = adminPermission.canView(role);
        if(!canView) {
            throw new StandardApiException(StandardErrorCode.UNAUTHORIZED);
        }

        return projectRepository.findById(id)
                .orElseThrow(() -> new StandardApiException(StandardErrorCode.NOT_FOUND));
    }
}

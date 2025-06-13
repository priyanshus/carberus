package com.cb.carberus.project.service;

import com.cb.carberus.authorization.service.AdminPermission;
import com.cb.carberus.config.UserContext;
import com.cb.carberus.constants.UserRole;
import com.cb.carberus.errorHandler.error.DomainException;
import com.cb.carberus.errorHandler.error.StandardApiException;
import com.cb.carberus.errorHandler.model.DomainErrorCode;
import com.cb.carberus.errorHandler.model.StandardErrorCode;
import com.cb.carberus.project.dto.AddProjectDTO;
import com.cb.carberus.project.dto.ProjectDTO;
import com.cb.carberus.project.mapper.ProjectMapper;
import com.cb.carberus.project.model.Project;
import com.cb.carberus.project.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
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

    public List<ProjectDTO> getAllProjects() {
        Iterable<Project> projects = projectRepository.findAll();

        List<Project> projectList = StreamSupport.stream(projects.spliterator(), false).toList();

        if (projectList.isEmpty()) {
            throw new DomainException(DomainErrorCode.PROJECT_NOT_FOUND);
        }

        return projectList
                .stream()
                .map(ProjectMapper.INSTANCE::toProjectDTO)
                .toList();
    }

    public ProjectDTO getProject(String projectId) {
        log.info("Get Project: {}", projectId);
        Long id = Long.parseLong(projectId);
        Project project = projectRepository.findById(id)
                .orElseThrow( () -> new DomainException(DomainErrorCode.PROJECT_NOT_FOUND));

        return ProjectMapper.INSTANCE.toProjectDTO(project);
    }

    public ProjectDTO addProject(AddProjectDTO addProjectDTO) {
        log.info("Adding Project: {}", addProjectDTO.getName());
        if (adminPermission.canAdd(getCurrentRole())) {
            boolean isDuplicateName = projectRepository.findByName(addProjectDTO.getName()).isPresent();
            boolean isDuplicatedCode = projectRepository.findByProjectCode(addProjectDTO.getProjectCode()).isPresent();

            if (isDuplicatedCode) {
                throw new DomainException(DomainErrorCode.PROJECT_PREFIX_ALREADY_EXIST);
            }

            if (isDuplicateName) {
                throw new DomainException(DomainErrorCode.PROJECT_NAME_ALREADY_EXIST);
            }

            Project project = ProjectMapper.INSTANCE.toProject(addProjectDTO);
            project.setCreatedBy(userContext.getUser());
            Project savedProject =  projectRepository.save(project);
            log.info("Added Project: {}", savedProject.getName());
            return ProjectMapper.INSTANCE.toProjectDTO(savedProject);
        }

        log.info("Failed to add Project as {} is unauthorized", getCurrentRole());
        throw new StandardApiException(StandardErrorCode.UNAUTHORIZED);
    }
}

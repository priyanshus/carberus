package com.cb.carberus.project.service;

import com.cb.carberus.authorization.service.AdminPermission;
import com.cb.carberus.authorization.service.ProjectPermission;
import com.cb.carberus.config.UserContext;
import com.cb.carberus.constants.ProjectRole;
import com.cb.carberus.constants.UserRole;
import com.cb.carberus.errorHandler.error.DomainException;
import com.cb.carberus.errorHandler.error.StandardApiException;
import com.cb.carberus.errorHandler.model.DomainErrorCode;
import com.cb.carberus.errorHandler.model.StandardErrorCode;
import com.cb.carberus.project.dto.AddProjectDTO;
import com.cb.carberus.project.dto.ProjectDTO;
import com.cb.carberus.project.dto.ProjectMemberDTO;
import com.cb.carberus.project.mapper.ProjectMapper;
import com.cb.carberus.project.model.Project;
import com.cb.carberus.project.model.ProjectMember;
import com.cb.carberus.project.repository.ProjectRepository;
import com.cb.carberus.user.model.User;
import com.cb.carberus.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class ProjectService {
    @Autowired
    private final ProjectRepository projectRepository;
    private final UserContext userContext;
    private final AdminPermission adminPermission;
    private final ProjectPermission projectPermission;
    private final UserRepository userRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, UserContext userContext, AdminPermission adminPermission,
                          UserRepository userRepository, ProjectPermission projectPermission) {
        this.projectRepository = projectRepository;
        this.userContext = userContext;
        this.adminPermission = adminPermission;
        this.userRepository = userRepository;
        this.projectPermission = projectPermission;
    }

    private UserRole getCurrentRole() {
        return userContext.getRole();
    }

    private Long getUserId() {
        return userContext.getUserId();
    }

    public List<ProjectDTO> getAllProjects() {
        log.info("Get All Projects");
        Iterable<Project> projects = projectRepository.findAll();

        List<Project> projectList = StreamSupport.stream(projects.spliterator(), false).toList();

        return projectList
                .stream()
                .map(ProjectMapper.INSTANCE::toProjectDTO)
                .toList();
    }

    public ProjectDTO getProject(String projectId) {
        log.info("Get Project: {}", projectId);
        Long id = Long.parseLong(projectId);
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new DomainException(DomainErrorCode.PROJECT_NOT_FOUND));

        return ProjectMapper.INSTANCE.toProjectDTO(project);
    }

    public ProjectDTO addProject(AddProjectDTO addProjectDTO) {
        log.info("Adding Project: {}", addProjectDTO.getName());

        if (adminPermission.canAdd(getCurrentRole())) {
            boolean isDuplicateName = projectRepository.findByName(addProjectDTO.getName()).isPresent();
            boolean isDuplicatedCode = projectRepository.findByProjectCode(addProjectDTO.getProjectCode()).isPresent();

            if (isDuplicatedCode) {
                throw new DomainException(DomainErrorCode.PROJECT_CODE_ALREADY_EXIST);
            }

            if (isDuplicateName) {
                throw new DomainException(DomainErrorCode.PROJECT_NAME_ALREADY_EXIST);
            }

            Project project = ProjectMapper.INSTANCE.toProject(addProjectDTO);
            project.setCreatedBy(userContext.getUser());
            Project savedProject = projectRepository.save(project);
            log.info("Added Project: {}", savedProject.getName());
            return ProjectMapper.INSTANCE.toProjectDTO(savedProject);
        }

        log.info("Failed to add Project as {} is unauthorized", getCurrentRole());
        throw new StandardApiException(StandardErrorCode.UNAUTHORIZED);
    }

    public List<ProjectMemberDTO> getProjectMembers(String projectId) {
        Project project = findProjectByIdOrElseThrow(projectId);

        return project.getMembers()
                .stream()
                .map(ProjectMapper.INSTANCE::toProjectMemberDTO)
                .toList();
    }

    public List<ProjectMemberDTO> addProjectMember(String projectId, ProjectMemberDTO dto) {
        ProjectRole requestedRole = parseProjectRole(dto.getProjectRole());
        Project project = findProjectByIdOrElseThrow(projectId);

        boolean canAddProjectMember = getCurrentRole().equals(UserRole.ADMIN) ||
                checkRequesterRoleInProjectOrElseThrow(project, userContext.getUserId());

        if (!canAddProjectMember) {
            throw new StandardApiException(StandardErrorCode.UNAUTHORIZED);
        }

        User user = findUserOrElseThrow(dto.getUserId());
        Optional<ProjectMember> existingMember = findMemberInProject(project, dto.getUserId());

        if (existingMember.isPresent()) {
            ProjectMember member = existingMember.get();
            if (!member.getProjectRole().equals(requestedRole)) {
                member.setProjectRole(requestedRole);
                member.setUpdatedAt(LocalDateTime.now());
            }
        } else {
            ProjectMember newMember = new ProjectMember();
            newMember.setProject(project);
            newMember.setUser(user);
            newMember.setProjectRole(requestedRole);
            newMember.setAddedAt(LocalDateTime.now());
            newMember.setUpdatedAt(LocalDateTime.now());
            project.getMembers().add(newMember);
        }

        projectRepository.save(project);
        return project.getMembers().stream()
                .map(ProjectMapper.INSTANCE::toProjectMemberDTO)
                .collect(Collectors.toList());
    }

    private ProjectRole parseProjectRole(String role) {
        try {
            return ProjectRole.valueOf(role.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new DomainException(DomainErrorCode.INVALID_ROLE);
        }
    }

    private Project findProjectByIdOrElseThrow(String projectId) {
        return projectRepository.findById(Long.parseLong(projectId))
                .orElseThrow(() -> new DomainException(DomainErrorCode.PROJECT_NOT_FOUND));
    }

    private User findUserOrElseThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DomainException(DomainErrorCode.NO_USERS_FOUND));
    }

    private boolean checkRequesterRoleInProjectOrElseThrow(Project project, Long requesterId) {
        log.info("Im here to verify the permission");
        Optional<ProjectMember> projectMember = findMemberInProject(project, requesterId);

        if (projectMember.isEmpty() ||
                !projectPermission.canAddProjectMember(projectMember.get().getProjectRole())) {

            log.info("Project member {} {}",
                    projectMember.map(ProjectMember::getProjectRole).orElse(null),
                    projectMember.map(m -> m.getUser().getEmail()).orElse("Unknown"));

            throw new StandardApiException(StandardErrorCode.UNAUTHORIZED);
        }

        log.info("Do have permission");
        return true;
    }


    private Optional<ProjectMember> findMemberInProject(Project project, Long userId) {
        return project.getMembers().stream()
                .filter(m -> userId.equals(m.getUser().getId()))
                .findFirst();
    }

}

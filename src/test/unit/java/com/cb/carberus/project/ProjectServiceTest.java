package com.cb.carberus.project;

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
import com.cb.carberus.project.model.Project;
import com.cb.carberus.project.model.ProjectStatus;
import com.cb.carberus.project.repository.ProjectRepository;
import com.cb.carberus.project.service.ProjectService;
import com.cb.carberus.user.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private AdminPermission adminPermission;

    @InjectMocks
    private ProjectService projectService;

    private AddProjectDTO addProjectDTO;
    private Project existingProject;

    @BeforeEach
    void setUp() {
        addProjectDTO = AddProjectDTO.builder()
                .name("some-project")
                .description("some-description")
                .projectCode("SOME")
                .build();


        existingProject = new Project();
        existingProject.setId(Long.parseLong("1"));
        existingProject.setName("some-project");
        existingProject.setProjectCode("SOME");
        existingProject.setStatus(ProjectStatus.ACTIVE);
    }

    @Test
    void addProject_shouldSaveProject_whenValidAndAuthorized() {

        Mockito.when(userContext.getRole()).thenReturn(UserRole.ADMIN);
        Mockito.when(adminPermission.canAdd(Mockito.any()))
                .thenReturn(true);
        Mockito.when(projectRepository.findByName("some-project"))
                .thenReturn(Optional.empty());
        Mockito.when(projectRepository.findByProjectCode("SOME"))
                .thenReturn(Optional.empty());
        Mockito.when(userContext.getUser()).thenReturn(new User());
        Mockito.when(projectRepository.save(Mockito.any()))
                .thenReturn(existingProject);

        // Act
        ProjectDTO result = projectService.addProject(addProjectDTO);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals("some-project", result.getName());
        Mockito.verify(projectRepository).save(Mockito.any(Project.class));
    }
//
//    @Test
//    void addProject_shouldThrowError_WhenRoleIsNonAdmin() {
//        Mockito.when(userContext.getRole()).thenReturn(UserRole.NONADMIN);
//        Mockito.when(projectPermission.canAdd(ProjectRole.VIEWER)).thenReturn(false);
//
//        StandardApiException ex = Assertions.assertThrows(StandardApiException.class,
//                () -> projectService.addProject(addProjectDTO));
//
//        Assertions.assertEquals(StandardErrorCode.UNAUTHORIZED, ex.getErrorCode());
//    }
//
//    @Test
//    void addProject_shouldThrow_whenNameAlreadyExists() {
//        Mockito.when(userContext.getRole()).thenReturn(UserRole.ADMIN);
//        Mockito.when(projectPermission.canAdd(ProjectRole.VIEWER)).thenReturn(true);
//
//        Project duplicate = new Project();
//        duplicate.setName("New Project");
//        duplicate.setPrefix("OTHER");
//
//        Mockito.when(projectRepository.findAll()).thenReturn(List.of(duplicate));
//
//        DomainException ex = Assertions.assertThrows(DomainException.class,
//                () -> projectService.addProject(addProjectDTO));
//
//        Assertions.assertEquals(DomainErrorCode.PROJECT_NAME_ALREADY_EXIST, ex.getErrorCode());
//    }
//
//    @Test
//    void addProject_shouldThrow_whenPrefixAlreadyExists() {
//        Mockito.when(userContext.getRole()).thenReturn(UserRole.ADMIN);
//        Mockito.when(projectPermission.canAdd(ProjectRole.VIEWER)).thenReturn(true);
//
//        Project duplicate = new Project();
//        duplicate.setName("some-project");
//        duplicate.setPrefix("NP");
//
//        Mockito.when(projectRepository.findAll()).thenReturn(List.of(duplicate));
//
//        DomainException ex = Assertions.assertThrows(DomainException.class,
//                () -> projectService.addProject(addProjectDTO));
//
//        Assertions.assertEquals(DomainErrorCode.PROJECT_PREFIX_ALREADY_EXIST, ex.getErrorCode());
//    }
//
//
//    @Test
//    void updateProject_shouldUpdate_whenAuthorizedAndActive() {
//        Mockito.when(userContext.getRole()).thenReturn(UserRole.ADMIN);
//        Mockito.when(projectPermission.canAdd(ProjectRole.VIEWER)).thenReturn(true);
//        Mockito.when(projectRepository.findById("p1")).thenReturn(Optional.of(existingProject));
//
//        projectService.updateProject(updateProjectDTO);
//
//        Assertions.assertEquals("Updated Name", existingProject.getName());
//        Mockito.verify(projectRepository).save(existingProject);
//    }
//
//    @Test
//    void updateProject_shouldThrow_whenArchived() {
//        existingProject.setStatus(ProjectStatus.ARCHIVED);
//
//        Mockito.when(userContext.getRole()).thenReturn(UserRole.ADMIN);
//        Mockito.when(projectPermission.canAdd(ProjectRole.VIEWER)).thenReturn(true);
//        Mockito.when(projectRepository.findById("p1")).thenReturn(Optional.of(existingProject));
//
//        DomainException ex = Assertions.assertThrows(DomainException.class,
//                () -> projectService.updateProject(updateProjectDTO));
//
//        Assertions.assertEquals(DomainErrorCode.PROJECT_IN_ARCHIVED_STATE, ex.getErrorCode());
//    }
//
//    @Test
//    void changeProjectStatus_shouldUpdate_whenDifferent() {
//        Mockito.when(userContext.getRole()).thenReturn(UserRole.ADMIN);
//        Mockito.when(projectPermission.canAdd(ProjectRole.VIEWER)).thenReturn(true);
//        Mockito.when(projectRepository.findById("p1")).thenReturn(Optional.of(existingProject));
//
//        projectService.changeProjectStatus("p1", statusDTO);
//
//        Assertions.assertEquals(ProjectStatus.ARCHIVED, existingProject.getStatus());
//        Mockito.verify(projectRepository).save(existingProject);
//    }
//
//    @Test
//    void getProjects_shouldReturn_whenAuthorized() {
//        Mockito.when(userContext.getRole()).thenReturn(UserRole.ADMIN);
//        Mockito.when(projectPermission.canView(ProjectRole.VIEWER)).thenReturn(true);
//        Mockito.when(projectRepository.findAll()).thenReturn(List.of(existingProject));
//
//        List<ProjectDTO> projects = projectService.getProjects();
//        Assertions.assertEquals(1, projects.size());
//    }
//
//    @Test
//    void getProject_shouldReturn_whenExists() {
//        Mockito.when(userContext.getRole()).thenReturn(UserRole.ADMIN);
//        Mockito.when(projectPermission.canView(ProjectRole.VIEWER)).thenReturn(true);
//        Mockito.when(projectRepository.findById("p1")).thenReturn(Optional.of(existingProject));
//
//        Project p = projectService.getProject("p1");
//        Assertions.assertEquals("p1", p.getId());
//    }
//
//    @Test
//    void getProject_shouldThrow_whenNotFound() {
//        Mockito.when(userContext.getRole()).thenReturn(UserRole.ADMIN);
//        Mockito.when(projectPermission.canView(ProjectRole.VIEWER)).thenReturn(true);
//        Mockito.when(projectRepository.findById("bad-id")).thenReturn(Optional.empty());
//
//        Assertions.assertThrows(StandardApiException.class,
//                () -> projectService.getProject("bad-id"));
//    }
}

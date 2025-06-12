package com.cb.carberus.project;

import com.cb.carberus.authorization.service.ProjectPermission;
import com.cb.carberus.config.UserContext;
import com.cb.carberus.constants.Role;
import com.cb.carberus.errorHandler.error.DomainException;
import com.cb.carberus.errorHandler.error.StandardApiException;
import com.cb.carberus.errorHandler.model.DomainErrorCode;
import com.cb.carberus.errorHandler.model.StandardErrorCode;
import com.cb.carberus.project.dto.AddProjectDTO;
import com.cb.carberus.project.dto.ChangeProjectStatusDTO;
import com.cb.carberus.project.dto.UpdateProjectDTO;
import com.cb.carberus.project.model.Project;
import com.cb.carberus.project.model.ProjectStatus;
import com.cb.carberus.project.repository.ProjectRepository;
import com.cb.carberus.project.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock 
    private UserContext userContext;
    @Mock 
    private ProjectPermission projectPermission;

    @InjectMocks
    private ProjectService projectService;

    private AddProjectDTO addProjectDTO;
    private UpdateProjectDTO updateProjectDTO;
    private ChangeProjectStatusDTO statusDTO;
    private Project existingProject;

    @BeforeEach
    void setUp() {
        addProjectDTO = new AddProjectDTO();
        addProjectDTO.setName("New Project");
        addProjectDTO.setPrefix("NP");

        updateProjectDTO = new UpdateProjectDTO();
        updateProjectDTO.setId("p1");
        updateProjectDTO.setName("Updated Name");
        updateProjectDTO.setDescription("Updated desc");

        statusDTO = new ChangeProjectStatusDTO();
        statusDTO.setProjectStatus(ProjectStatus.ARCHIVED);

        existingProject = new Project();
        existingProject.setId("p1");
        existingProject.setName("Old Project");
        existingProject.setPrefix("OP");
        existingProject.setStatus(ProjectStatus.ACTIVE);
    }

    @Test
    void addProject_shouldSaveProject_whenValidAndAuthorized() {
        when(userContext.getRole()).thenReturn(Role.ADMIN);
        when(projectPermission.canAdd(Role.ADMIN)).thenReturn(true);
        when(projectRepository.findAll()).thenReturn(List.of());

        projectService.addProject(addProjectDTO);

        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void addProject_shouldThrowError_WhenRoleIsNonAdmin() {
        when(userContext.getRole()).thenReturn(Role.TESTMANAGER);
        when(projectPermission.canAdd(Role.TESTMANAGER)).thenReturn(false);

        StandardApiException ex = assertThrows(StandardApiException.class,
                () -> projectService.addProject(addProjectDTO));

        assertEquals(StandardErrorCode.UNAUTHORIZED, ex.getErrorCode());
    }

    @Test
    void addProject_shouldThrow_whenNameAlreadyExists() {
        when(userContext.getRole()).thenReturn(Role.ADMIN);
        when(projectPermission.canAdd(Role.ADMIN)).thenReturn(true);

        Project duplicate = new Project();
        duplicate.setName("New Project");
        duplicate.setPrefix("OTHER");

        when(projectRepository.findAll()).thenReturn(List.of(duplicate));

        DomainException ex = assertThrows(DomainException.class,
                () -> projectService.addProject(addProjectDTO));

        assertEquals(DomainErrorCode.PROJECT_NAME_ALREADY_EXIST, ex.getErrorCode());
    }

    @Test
    void addProject_shouldThrow_whenPrefixAlreadyExists() {
        when(userContext.getRole()).thenReturn(Role.ADMIN);
        when(projectPermission.canAdd(Role.ADMIN)).thenReturn(true);

        Project duplicate = new Project();
        duplicate.setName("some-project");
        duplicate.setPrefix("NP");

        when(projectRepository.findAll()).thenReturn(List.of(duplicate));

        DomainException ex = assertThrows(DomainException.class,
                () -> projectService.addProject(addProjectDTO));

        assertEquals(DomainErrorCode.PROJECT_PREFIX_ALREADY_EXIST, ex.getErrorCode());
    }


    @Test
    void updateProject_shouldUpdate_whenAuthorizedAndActive() {
        when(userContext.getRole()).thenReturn(Role.ADMIN);
        when(projectPermission.canAdd(Role.ADMIN)).thenReturn(true);
        when(projectRepository.findById("p1")).thenReturn(Optional.of(existingProject));

        projectService.updateProject(updateProjectDTO);

        assertEquals("Updated Name", existingProject.getName());
        verify(projectRepository).save(existingProject);
    }

    @Test
    void updateProject_shouldThrow_whenArchived() {
        existingProject.setStatus(ProjectStatus.ARCHIVED);

        when(userContext.getRole()).thenReturn(Role.ADMIN);
        when(projectPermission.canAdd(Role.ADMIN)).thenReturn(true);
        when(projectRepository.findById("p1")).thenReturn(Optional.of(existingProject));

        DomainException ex = assertThrows(DomainException.class,
                () -> projectService.updateProject(updateProjectDTO));

        assertEquals(DomainErrorCode.PROJECT_IN_ARCHIVED_STATE, ex.getErrorCode());
    }

    @Test
    void changeProjectStatus_shouldUpdate_whenDifferent() {
        when(userContext.getRole()).thenReturn(Role.ADMIN);
        when(projectPermission.canAdd(Role.ADMIN)).thenReturn(true);
        when(projectRepository.findById("p1")).thenReturn(Optional.of(existingProject));

        projectService.changeProjectStatus("p1", statusDTO);

        assertEquals(ProjectStatus.ARCHIVED, existingProject.getStatus());
        verify(projectRepository).save(existingProject);
    }

    @Test
    void getProjects_shouldReturn_whenAuthorized() {
        when(userContext.getRole()).thenReturn(Role.ADMIN);
        when(projectPermission.canView(Role.ADMIN)).thenReturn(true);
        when(projectRepository.findAll()).thenReturn(List.of(existingProject));

        List<Project> projects = projectService.getProjects();
        assertEquals(1, projects.size());
    }

    @Test
    void getProject_shouldReturn_whenExists() {
        when(userContext.getRole()).thenReturn(Role.ADMIN);
        when(projectPermission.canView(Role.ADMIN)).thenReturn(true);
        when(projectRepository.findById("p1")).thenReturn(Optional.of(existingProject));

        Project p = projectService.getProject("p1");
        assertEquals("p1", p.getId());
    }

    @Test
    void getProject_shouldThrow_whenNotFound() {
        when(userContext.getRole()).thenReturn(Role.ADMIN);
        when(projectPermission.canView(Role.ADMIN)).thenReturn(true);
        when(projectRepository.findById("bad-id")).thenReturn(Optional.empty());

        assertThrows(StandardApiException.class,
                () -> projectService.getProject("bad-id"));
    }
}

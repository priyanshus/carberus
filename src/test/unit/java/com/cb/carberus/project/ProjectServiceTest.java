package com.cb.carberus.project;

import com.cb.carberus.authorization.service.AdminPermission;
import com.cb.carberus.config.UserContext;
import com.cb.carberus.constants.UserRole;
import com.cb.carberus.errorHandler.error.DomainException;
import com.cb.carberus.errorHandler.error.StandardApiException;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;

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
        existingProject.setDescription("some-desc");
        existingProject.setCreatedAt(LocalDateTime.now());
        existingProject.setName("some-project");
        existingProject.setProjectCode("SOME");
        existingProject.setStatus(ProjectStatus.ACTIVE);
    }


    @Test
    void getProjects_ShouldBringAllProjects() {
        Project project = new Project();
        project.setName("some-project");
        project.setDescription("some-desc");

        Mockito.when(projectRepository.findAll()).thenReturn(
                List.of(project)
        );

        // Act
        List<ProjectDTO> projectDTOList = projectService.getAllProjects();

        // Assert
        Assertions.assertEquals(1, projectDTOList.size());
        Assertions.assertEquals("some-project", projectDTOList.getFirst().getName());
    }

    @Test
    void getProjects_ShouldBringNoProjectsWhenThereIsNoProjectInDB() {
        Mockito.when(projectRepository.findAll()).thenReturn(List.of());

        // Act
        List<ProjectDTO> projectDTOList = projectService.getAllProjects();

        // Assert
        Assertions.assertEquals(0, projectDTOList.size());
    }

    @Test
    void getProject_ShouldBringSingleProjectMatchedWithProjectId() {
        Project project = new Project();
        project.setName("some-project");
        project.setStatus(ProjectStatus.ACTIVE);
        project.setDescription("some-desc");

        Mockito.when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));

        // Act
        ProjectDTO projectDTO = projectService.getProject("1234");

        // Assert
        Assertions.assertEquals("some-project", projectDTO.getName());
        Assertions.assertEquals("some-desc", projectDTO.getDescription());
        Assertions.assertEquals("ACTIVE", projectDTO.getStatus());
    }

    @Test
    void getProject_ShouldThrowExceptionProjectNotFound_WhenProjectIDDoesNotMatch() {
        Mockito.when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Assert
        assertThrows(DomainException.class, () -> projectService.getProject("9183903"));
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
        Assertions.assertEquals("SOME", result.getProjectCode());
        Assertions.assertEquals("some-desc", result.getDescription());
        Assertions.assertEquals("ACTIVE", result.getStatus());
        Assertions.assertNotNull(result.getCreatedAt());

        Mockito.verify(projectRepository).save(Mockito.any(Project.class));
    }


    @Test
    void addProject_shouldThrowException_whenNonAdminSavesProject() {
        Mockito.when(userContext.getRole()).thenReturn(UserRole.NONADMIN);
        Mockito.when(adminPermission.canAdd(Mockito.any()))
                .thenReturn(false);


        // Act
        Exception ex = assertThrows(StandardApiException.class, () -> projectService.addProject(addProjectDTO));

        // Assert
        assertTrue(ex.getMessage().contains("UNAUTHORIZED"));
    }

    @Test
    void addProject_shouldThrowException_whenDuplicateNameUsed() {
        Mockito.when(userContext.getRole()).thenReturn(UserRole.ADMIN);
        Mockito.when(adminPermission.canAdd(Mockito.any()))
                .thenReturn(true);

        Mockito.when(projectRepository.findByName("some-project"))
                .thenReturn(Optional.of(existingProject));


        // Act
        Exception ex = assertThrows(DomainException.class, () -> projectService.addProject(addProjectDTO));

        // Assert
        assertTrue(ex.getMessage().contains("NAME"));
    }

    @Test
    void addProject_shouldThrowException_whenDuplicateProjectCodeUsed() {
        Mockito.when(userContext.getRole()).thenReturn(UserRole.ADMIN);
        Mockito.when(adminPermission.canAdd(Mockito.any()))
                .thenReturn(true);

        Mockito.when(projectRepository.findByProjectCode("SOME"))
                .thenReturn(Optional.of(existingProject));

        // Act
        Exception ex = assertThrows(DomainException.class, () -> projectService.addProject(addProjectDTO));

        // Assert
        assertTrue(ex.getMessage().contains("PREFIX_ALREADY_EXIST"));
    }
}

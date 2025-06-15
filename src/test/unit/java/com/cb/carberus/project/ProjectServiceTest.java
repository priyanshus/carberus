package com.cb.carberus.project;

import com.cb.carberus.authorization.service.AdminPermission;
import com.cb.carberus.authorization.service.ProjectPermission;
import com.cb.carberus.config.UserContext;
import com.cb.carberus.constants.ProjectRole;
import com.cb.carberus.constants.UserRole;
import com.cb.carberus.errorHandler.error.DomainException;
import com.cb.carberus.errorHandler.error.StandardApiException;
import com.cb.carberus.project.dto.AddProjectDTO;
import com.cb.carberus.project.dto.ProjectDTO;
import com.cb.carberus.project.dto.ProjectMemberDTO;
import com.cb.carberus.project.model.Project;
import com.cb.carberus.project.model.ProjectMember;
import com.cb.carberus.project.model.ProjectStatus;
import com.cb.carberus.project.repository.ProjectRepository;
import com.cb.carberus.project.service.ProjectService;
import com.cb.carberus.user.model.User;
import com.cb.carberus.user.repository.UserRepository;
import com.cb.carberus.util.TestMockObjectUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private AdminPermission adminPermission;

    @Mock
    private ProjectPermission projectPermission;

    @InjectMocks
    private ProjectService projectService;

    private AddProjectDTO addProjectDTO;
    private ProjectMemberDTO projectMemberDTO;
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
        existingProject.setMembers(new ArrayList<>());

        projectMemberDTO = ProjectMemberDTO
                .builder()
                .userId(10L)
                .projectRole("tester")
                .build();

    }


    @Test
    void getProjects_ShouldBringAllProjects_whenUserIsSystemAdmin() {
        when(userContext.getRole()).thenReturn(UserRole.ADMIN);

        when(projectRepository.findAll()).thenReturn(
                TestMockObjectUtil.getProjects()
        );

        // Act
        List<ProjectDTO> projectDTOList = projectService.getAllProjects();

        // Assert
        Assertions.assertEquals(2, projectDTOList.size());
        Assertions.assertEquals("project-name1", projectDTOList.getFirst().getName());
        Assertions.assertEquals("project-name2", projectDTOList.get(1).getName());

    }

    @Test
    void getProjects_ShouldBringAllProjects_whenUserIsNonAdmin() {
        when(userContext.getRole()).thenReturn(UserRole.NONADMIN);

        when(projectRepository.findByMembers_User_Id(anyLong())).thenReturn(
                TestMockObjectUtil.getProjects()
        );

        // Act
        List<ProjectDTO> projectDTOList = projectService.getAllProjects();

        // Assert
        Assertions.assertEquals(2, projectDTOList.size());
        Assertions.assertEquals("project-name1", projectDTOList.getFirst().getName());
        Assertions.assertEquals("project-name2", projectDTOList.get(1).getName());
        verify(projectRepository, times(1)).findByMembers_User_Id(anyLong());
    }

    @Test
    void getProjects_ShouldBringNoProjectsWhenThereIsNoProjectInDB() {
        when(userContext.getRole()).thenReturn(UserRole.NONADMIN);

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

        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));

        // Act
        ProjectDTO projectDTO = projectService.getProject("1234");

        // Assert
        Assertions.assertEquals("some-project", projectDTO.getName());
        Assertions.assertEquals("some-desc", projectDTO.getDescription());
        Assertions.assertEquals("ACTIVE", projectDTO.getStatus());
    }

    @Test
    void getProject_ShouldThrowExceptionProjectNotFound_WhenProjectIDDoesNotMatch() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Assert
        assertThrows(DomainException.class, () -> projectService.getProject("9183903"));
    }


    @Test
    void addProject_shouldSaveProject_whenValidAndAuthorized() {
        when(userContext.getRole()).thenReturn(UserRole.ADMIN);
        when(adminPermission.canAdd(any()))
                .thenReturn(true);
        when(projectRepository.findByName("some-project"))
                .thenReturn(Optional.empty());
        when(projectRepository.findByProjectCode("SOME"))
                .thenReturn(Optional.empty());
        when(userContext.getUser()).thenReturn(new User());
        when(projectRepository.save(any()))
                .thenReturn(existingProject);

        // Act
        ProjectDTO result = projectService.addProject(addProjectDTO);

        // Assert
        assertNotNull(result);
        Assertions.assertEquals("some-project", result.getName());
        Assertions.assertEquals("SOME", result.getProjectCode());
        Assertions.assertEquals("some-desc", result.getDescription());
        Assertions.assertEquals("ACTIVE", result.getStatus());
        assertNotNull(result.getCreatedAt());

        verify(projectRepository).save(any(Project.class));
    }


    @Test
    void addProject_shouldThrowException_whenNonAdminSavesProject() {
        when(userContext.getRole()).thenReturn(UserRole.NONADMIN);
        when(adminPermission.canAdd(any()))
                .thenReturn(false);

        // Act
        Exception ex = assertThrows(StandardApiException.class, () -> projectService.addProject(addProjectDTO));

        // Assert
        assertTrue(ex.getMessage().contains("UNAUTHORIZED"));
    }

    @Test
    void addProject_shouldThrowException_whenDuplicateNameUsed() {
        when(userContext.getRole()).thenReturn(UserRole.ADMIN);
        when(adminPermission.canAdd(any()))
                .thenReturn(true);

        when(projectRepository.findByName("some-project"))
                .thenReturn(Optional.of(existingProject));


        // Act
        Exception ex = assertThrows(DomainException.class, () -> projectService.addProject(addProjectDTO));

        // Assert
        assertTrue(ex.getMessage().contains("NAME"));
    }

    @Test
    void addProject_shouldThrowException_whenDuplicateProjectCodeUsed() {
        when(userContext.getRole()).thenReturn(UserRole.ADMIN);
        when(adminPermission.canAdd(any()))
                .thenReturn(true);

        when(projectRepository.findByProjectCode("SOME"))
                .thenReturn(Optional.of(existingProject));

        // Act
        Exception ex = assertThrows(DomainException.class, () -> projectService.addProject(addProjectDTO));

        // Assert
        assertTrue(ex.getMessage().contains("PREFIX_ALREADY_EXIST"));
    }

    @Test
    void addProjectMember_shouldSaveProject_whenRequesterIsSystemAdmin() {
        // Arrange
        when(userContext.getRole()).thenReturn(UserRole.ADMIN);
        when(projectRepository.findById(100L)).thenReturn(Optional.of(existingProject));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(TestMockObjectUtil.getUser()));

        // Act
        List<ProjectMemberDTO> result = projectService.addProjectMember("100", projectMemberDTO);

        // Assert
        assertNotNull(result, "Returned member list should not be null");
        assertEquals(1, result.size(), "Should contain exactly one project member");

        verify(projectRepository, times(1)).save(any(Project.class));
        verify(userRepository, times(1)).findById(projectMemberDTO.getUserId());
        verify(projectRepository).findById(100L);
    }

    @Test
    void addProjectMember_shouldSaveProject_whenRequesterIsProjectAdmin() {
        when(projectPermission.canAddProjectMember(any())).thenReturn(true);
        when(userContext.getUserId()).thenReturn(123L);
        when(userContext.getRole()).thenReturn(UserRole.NONADMIN);

        ProjectMember adminMember = TestMockObjectUtil.projectMember(
                ProjectRole.ADMIN,
                TestMockObjectUtil.getUser(123L, "requester@mail.com", UserRole.NONADMIN)
        );

        ProjectMember existingMember = TestMockObjectUtil.projectMember(
                ProjectRole.TESTER,
                TestMockObjectUtil.getUser(125L, "existing@mail.com", UserRole.NONADMIN)
        );

        Project project = TestMockObjectUtil.getProjectWithMembers(adminMember, existingMember);

        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(TestMockObjectUtil.getUser()));

        // Act
        List<ProjectMemberDTO> result = projectService.addProjectMember("100", projectMemberDTO);


        // Assert
        assertNotNull(result, "Returned member list should not be null");
        assertEquals(3, result.size(), "Should contain exactly three project members");

        verify(projectRepository, times(1)).save(any(Project.class));
        verify(userRepository, times(1)).findById(projectMemberDTO.getUserId());
        verify(projectRepository).findById(100L);
        verify(projectPermission).canAddProjectMember(ProjectRole.ADMIN);
    }


    @Test
    void addProjectMember_shouldSaveProject_whenRequesterIsProjectManager() {
        when(projectPermission.canAddProjectMember(any())).thenReturn(true);
        when(userContext.getUserId()).thenReturn(123L);
        when(userContext.getRole()).thenReturn(UserRole.NONADMIN);

        ProjectMember adminMember = TestMockObjectUtil.projectMember(
                ProjectRole.MANAGER,
                TestMockObjectUtil.getUser(123L, "requester@mail.com", UserRole.NONADMIN)
        );

        ProjectMember existingMember = TestMockObjectUtil.projectMember(
                ProjectRole.TESTER,
                TestMockObjectUtil.getUser(125L, "existing@mail.com", UserRole.NONADMIN)
        );

        Project project = TestMockObjectUtil.getProjectWithMembers(adminMember, existingMember);

        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(TestMockObjectUtil.getUser()));

        // Act
        List<ProjectMemberDTO> result = projectService.addProjectMember("100", projectMemberDTO);


        // Assert
        assertNotNull(result, "Returned member list should not be null");
        assertEquals(3, result.size(), "Should contain exactly three project members");

        verify(projectRepository, times(1)).save(any(Project.class));
        verify(userRepository, times(1)).findById(projectMemberDTO.getUserId());
        verify(projectRepository).findById(100L);
        verify(projectPermission).canAddProjectMember(ProjectRole.MANAGER);
    }

    @Test
    void addProjectMember_shouldNotSaveProject_whenRequesterIsNotProjectManager() {
        when(userContext.getUserId()).thenReturn(123L);
        when(userContext.getRole()).thenReturn(UserRole.NONADMIN);

        ProjectMember adminMember = TestMockObjectUtil.projectMember(
                ProjectRole.TESTER,
                TestMockObjectUtil.getUser(123L, "requester@mail.com", UserRole.NONADMIN)
        );


        Project project = TestMockObjectUtil.getProjectWithMembers(adminMember);

        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));

        // Act
        StandardApiException exception = assertThrows(StandardApiException.class, () -> {
            projectService.addProjectMember("100", projectMemberDTO);
        });

        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void addProjectMember_shouldNotSaveProject_whenNewMemberRoleIsInvalid() {
        projectMemberDTO.setProjectRole("invalidRole");

        ProjectMember adminMember = TestMockObjectUtil.projectMember(
                ProjectRole.TESTER,
                TestMockObjectUtil.getUser(123L, "requester@mail.com", UserRole.NONADMIN)
        );


        Project project = TestMockObjectUtil.getProjectWithMembers(adminMember);


        // Act
        DomainException exception = assertThrows(DomainException.class, () -> {
            projectService.addProjectMember("100", projectMemberDTO);
        });

        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void addProjectMember_shouldNotSaveProject_whenNewMemberIdIsInvalid() {
        when(projectPermission.canAddProjectMember(any())).thenReturn(true);
        when(userContext.getUserId()).thenReturn(123L);
        when(userContext.getRole()).thenReturn(UserRole.NONADMIN);

        ProjectMember adminMember = TestMockObjectUtil.projectMember(
                ProjectRole.MANAGER,
                TestMockObjectUtil.getUser(123L, "requester@mail.com", UserRole.NONADMIN)
        );

        Project project = TestMockObjectUtil.getProjectWithMembers(adminMember);

        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        DomainException exception = assertThrows(DomainException.class, () -> {
            projectService.addProjectMember("100", projectMemberDTO);
        });

        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void addProjectMember_shouldNotSaveProject_whenProjectIdIsInvalid() {


        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        DomainException exception = assertThrows(DomainException.class, () -> {
            projectService.addProjectMember("10191", projectMemberDTO);
        });

        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void patchProjectMember_shouldUpdateProjectRole_whenMemberAlreadyExist() {
        projectMemberDTO.setUserId(125L);
        projectMemberDTO.setProjectRole("admin");
        when(projectPermission.canAddProjectMember(any())).thenReturn(true);
        when(userContext.getUserId()).thenReturn(123L);
        when(userContext.getRole()).thenReturn(UserRole.NONADMIN);

        ProjectMember adminMember = TestMockObjectUtil.projectMember(
                ProjectRole.ADMIN,
                TestMockObjectUtil.getUser(123L, "requester@mail.com", UserRole.NONADMIN)
        );

        ProjectMember existingMember = TestMockObjectUtil.projectMember(
                ProjectRole.TESTER,
                TestMockObjectUtil.getUser(125L, "existing@mail.com", UserRole.NONADMIN)
        );

        Project project = TestMockObjectUtil.getProjectWithMembers(adminMember, existingMember);

        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(TestMockObjectUtil.getUser()));

        // Act
        List<ProjectMemberDTO> result = projectService.updateProjectMemberRole("100", projectMemberDTO);


        // Assert
        assertNotNull(result, "Returned member list should not be null");
        assertEquals(2, result.size(), "Should contain exactly three project members");

        Optional<ProjectMemberDTO> response = result.stream()
                .filter(m -> m.getUserId().equals(125L))
                .findFirst();

        assertTrue(response.isPresent());
        assertEquals("ADMIN", response.get().getProjectRole());

        verify(projectRepository, times(1)).save(any(Project.class));
        verify(userRepository, times(1)).findById(projectMemberDTO.getUserId());
        verify(projectRepository).findById(100L);
        verify(projectPermission).canAddProjectMember(ProjectRole.ADMIN);
    }

    @Test
    void patchProjectMember_shouldThrowException_whenMemberDoesNotExist() {
        projectMemberDTO.setUserId(126L);
        projectMemberDTO.setProjectRole("admin");
        when(userContext.getRole()).thenReturn(UserRole.ADMIN);

        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(TestMockObjectUtil.getProjectWithMember()));
        // Act
        DomainException exception = assertThrows(DomainException.class, () -> {
            projectService.updateProjectMemberRole("100", projectMemberDTO);
        });
    }

}

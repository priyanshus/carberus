package com.cb.carberus.project;

import com.cb.carberus.auth.service.AuthUserDetailsService;
import com.cb.carberus.config.CustomUserDetails;
import com.cb.carberus.config.UserContext;
import com.cb.carberus.errorHandler.error.DomainException;
import com.cb.carberus.errorHandler.model.DomainErrorCode;
import com.cb.carberus.project.controller.ProjectController;
import com.cb.carberus.project.dto.AddProjectDTO;
import com.cb.carberus.project.dto.ProjectDTO;
import com.cb.carberus.project.dto.ProjectMemberDTO;
import com.cb.carberus.project.repository.ProjectRepository;
import com.cb.carberus.project.service.ProjectService;
import com.cb.carberus.security.config.JwtAuthorizationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProjectControllerTest {

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private AuthUserDetailsService authUserDetailsService;

    @MockitoBean
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @MockitoBean
    private CustomUserDetails customUserDetails;
    //
    @MockitoBean
    private UserContext userContext;

    @MockitoBean
    private ProjectRepository projectRepository;


    @Autowired
    private MockMvc mockMvc;


    @Test
    void GET_GetProjects_ShouldSuccess() throws Exception {
        when(projectService.getAllProjects())
                .thenReturn(List.of(ProjectDTO.builder().build()));

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk());
    }

    @Test
    void GET_GetProject_ShouldSuccess() throws Exception {
        when(projectService.getProject("123"))
                .thenReturn(ProjectDTO.builder().build());

        mockMvc.perform(get("/projects/1234"))
                .andExpect(status().isOk());
    }

    @Test
    void GET_GetProject_ShouldFail_ForInvalidProjectId() throws Exception {
        when(projectService.getProject(any(String.class)))
                .thenThrow(new DomainException(DomainErrorCode.PROJECT_NOT_FOUND));

        mockMvc.perform(get("/projects/1234"))
                .andExpect(status().isNotFound());
    }

    @Test
    void POST_createProject_ShouldSuccess() throws Exception {
        AddProjectDTO addProjectDTO = AddProjectDTO
                .builder()
                .name("some-name")
                .description("desc")
                .projectCode("SOME")
                .build();

        when(projectService.getAllProjects())
                .thenReturn(List.of(ProjectDTO.builder().build()));

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(addProjectDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void POST_createProject_ShouldFail_WhenNameIsMissing() throws Exception {
        AddProjectDTO addProjectDTO = AddProjectDTO
                .builder()
                .description("desc")
                .projectCode("SOME")
                .build();

        when(projectService.getAllProjects())
                .thenReturn(List.of(ProjectDTO.builder().build()));

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(addProjectDTO)))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void POST_createProject_ShouldFail_WhenProjectCodeIsMissing() throws Exception {
        AddProjectDTO addProjectDTO = AddProjectDTO
                .builder()
                .name("name")
                .description("desc")
                .build();

        when(projectService.getAllProjects())
                .thenReturn(List.of(ProjectDTO.builder().build()));

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(addProjectDTO)))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void GET_getProjectMembers_shouldSuccess() throws Exception {
        ProjectMemberDTO projectMemberDTO = ProjectMemberDTO.builder()
                .userId(1L)
                .email("some@mail.com")
                .firstName("some")
                .lastName("name")
                .build();

        when(projectService.getProjectMembers(any(String.class)))
                .thenReturn(List.of(projectMemberDTO));

        mockMvc.perform(get("/projects/1/members"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.[0].email").value("some@mail.com"));
    }

    @Test
    void GET_getProjectMembers_shouldFail_whenProjectDoesNotExist() throws Exception {
        ProjectMemberDTO projectMemberDTO = ProjectMemberDTO.builder()
                .userId(1L)
                .email("some@mail.com")
                .firstName("some")
                .lastName("name")
                .build();


        when(projectService.getProjectMembers("123"))
                .thenThrow(new DomainException(DomainErrorCode.PROJECT_NOT_FOUND));


        mockMvc.perform(get("/projects/123/members"))
                .andExpect(status().isNotFound());
    }

    @Test
    void POST_addProjectMembers_shouldSuccess_whenUserIsSystemAdmin() throws Exception {
        ProjectMemberDTO projectMemberDTO = ProjectMemberDTO.builder()
                .userId(1L)
                .projectRole("tester")
                .build();


        when(projectService.addProjectMember("123", projectMemberDTO))
                .thenReturn(List.of(projectMemberDTO));


        mockMvc.perform(post("/projects/123/members")
                        .content(new ObjectMapper().writeValueAsString(projectMemberDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.[0].userId").value("1"));
    }

}

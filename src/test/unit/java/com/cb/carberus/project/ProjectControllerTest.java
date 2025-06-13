package com.cb.carberus.project;

import com.cb.carberus.auth.service.AuthUserDetailsService;
import com.cb.carberus.config.CustomUserDetails;
import com.cb.carberus.config.UserContext;
import com.cb.carberus.constants.UserRole;
import com.cb.carberus.errorHandler.error.DomainException;
import com.cb.carberus.errorHandler.model.DomainErrorCode;
import com.cb.carberus.project.controller.ProjectController;
import com.cb.carberus.project.dto.AddProjectDTO;
import com.cb.carberus.project.dto.ProjectDTO;
import com.cb.carberus.project.repository.ProjectRepository;
import com.cb.carberus.project.service.ProjectService;
import com.cb.carberus.security.config.JwtAuthorizationFilter;
import com.cb.carberus.user.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;


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
        Mockito.when(projectService.getAllProjects())
                .thenReturn(List.of(ProjectDTO.builder().build()));

        mockMvc.perform(MockMvcRequestBuilders.get("/projects"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void GET_GetProject_ShouldSuccess() throws Exception {
        Mockito.when(projectService.getProject("123"))
                .thenReturn(ProjectDTO.builder().build());

        mockMvc.perform(MockMvcRequestBuilders.get("/projects/1234"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void GET_GetProject_ShouldFail_ForInvalidProjectId() throws Exception {
        Mockito.when(projectService.getProject(any(String.class)))
                .thenThrow(new DomainException(DomainErrorCode.PROJECT_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.get("/projects/1234"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void POST_createProject_ShouldSuccess() throws Exception {
        AddProjectDTO addProjectDTO = AddProjectDTO
                .builder()
                .name("some-name")
                .description("desc")
                .projectCode("SOME")
                .build();

        Mockito.when(projectService.getAllProjects())
                .thenReturn(List.of(ProjectDTO.builder().build()));

        mockMvc.perform(MockMvcRequestBuilders.post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(addProjectDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void POST_createProject_ShouldFail_WhenNameIsMissing() throws Exception {
        AddProjectDTO addProjectDTO = AddProjectDTO
                .builder()
                .description("desc")
                .projectCode("SOME")
                .build();

        Mockito.when(projectService.getAllProjects())
                .thenReturn(List.of(ProjectDTO.builder().build()));

        mockMvc.perform(MockMvcRequestBuilders.post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(addProjectDTO)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value("INVALID_INPUT"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void POST_createProject_ShouldFail_WhenProjectCodeIsMissing() throws Exception {
        AddProjectDTO addProjectDTO = AddProjectDTO
                .builder()
                .name("name")
                .description("desc")
                .build();

        Mockito.when(projectService.getAllProjects())
                .thenReturn(List.of(ProjectDTO.builder().build()));

        mockMvc.perform(MockMvcRequestBuilders.post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(addProjectDTO)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value("INVALID_INPUT"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

}

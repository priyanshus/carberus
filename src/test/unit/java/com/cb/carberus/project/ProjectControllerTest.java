package com.cb.carberus.project;

import com.cb.carberus.auth.service.AuthUserDetailsService;
import com.cb.carberus.config.CustomUserDetails;
import com.cb.carberus.config.UserContext;
import com.cb.carberus.errorHandler.error.DomainException;
import com.cb.carberus.errorHandler.model.DomainErrorCode;
import com.cb.carberus.project.controller.ProjectController;
import com.cb.carberus.project.dto.AddProjectDTO;
import com.cb.carberus.project.dto.ChangeProjectStatusDTO;
import com.cb.carberus.project.dto.ProjectDTO;
import com.cb.carberus.project.dto.UpdateProjectDTO;
import com.cb.carberus.project.model.ProjectStatus;
import com.cb.carberus.project.repository.ProjectRepository;
import com.cb.carberus.project.service.ProjectService;
import com.cb.carberus.security.config.JwtAuthorizationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    void getProjects_ShouldReturnSuccess() throws Exception {
        Mockito.when(projectService.getProjects()).thenReturn(List.of(new ProjectDTO()));

        mockMvc.perform(MockMvcRequestBuilders.get("/projects"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    void createProject_ShouldReturnSuccess() throws Exception {
        AddProjectDTO dto = new AddProjectDTO();
        dto.setName("Test Project");
        dto.setPrefix("TP");
        dto.setDescription("Some description");

        Mockito.doNothing().when(projectService).addProject(ArgumentMatchers.any(AddProjectDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void createProject_ShouldReturn400_WhenMissingFields() throws Exception {
        AddProjectDTO dto = new AddProjectDTO();

        mockMvc.perform(MockMvcRequestBuilders.post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.prefix").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").exists());
    }

    @Test
    void createProject_ShouldReturnError_WhenDuplicateName() throws Exception {
        Mockito.doThrow(new DomainException(DomainErrorCode.PROJECT_NAME_ALREADY_EXIST))
                .when(projectService)
                .addProject(ArgumentMatchers.any(AddProjectDTO.class));


        mockMvc.perform(MockMvcRequestBuilders.post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new AddProjectDTO())))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void createProject_ShouldReturnError_WhenDuplicatePrefix() throws Exception {
        Mockito.doThrow(new DomainException(DomainErrorCode.PROJECT_PREFIX_ALREADY_EXIST))
                .when(projectService)
                .addProject(ArgumentMatchers.any(AddProjectDTO.class));


        mockMvc.perform(MockMvcRequestBuilders.post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new AddProjectDTO())))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void createProject_ShouldReturnError_WhenNameIsMoreThan50Chars() throws Exception {
        AddProjectDTO dto = new AddProjectDTO();
        dto.setName("Test Project with More than 50 characters. It should fail badly.");
        dto.setPrefix("TP");
        dto.setDescription("Some description");

        Mockito.doNothing().when(projectService).addProject(ArgumentMatchers.any(AddProjectDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void createProject_ShouldReturnError_WhenPrefixIsMoreThan4Chars() throws Exception {
        AddProjectDTO dto = new AddProjectDTO();
        dto.setName("Test Project");
        dto.setPrefix("TPXY1");
        dto.setDescription("Some description");

        Mockito.doNothing().when(projectService).addProject(ArgumentMatchers.any(AddProjectDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void updateProject_ShouldReturn201_WhenValid() throws Exception {
        UpdateProjectDTO dto = new UpdateProjectDTO();
        dto.setId("proj-123");
        dto.setName("Updated Name");
        dto.setDescription("Updated desc");

        Mockito.doNothing().when(projectService).updateProject(ArgumentMatchers.any(UpdateProjectDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.put("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }



    @Test
    void updateProject_ShouldReturn400_WhenInvalid() throws Exception {
        UpdateProjectDTO dto = new UpdateProjectDTO(); // missing id & name

        mockMvc.perform(MockMvcRequestBuilders.put("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void changeProjectStatus_ShouldReturn204_WhenValid() throws Exception {
        ChangeProjectStatusDTO dto = new ChangeProjectStatusDTO();
        dto.setProjectStatus(ProjectStatus.ARCHIVED);

        Mockito.doNothing().when(projectService).changeProjectStatus(ArgumentMatchers.eq("proj-123"), ArgumentMatchers.any(ChangeProjectStatusDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.patch("/projects/proj-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void changeProjectStatus_ShouldReturn400_WhenInvalid() throws Exception {
        ChangeProjectStatusDTO dto = new ChangeProjectStatusDTO(); // missing status

        mockMvc.perform(MockMvcRequestBuilders.patch("/projects/proj-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

}

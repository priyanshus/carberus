package com.cb.carberus.user;

import com.cb.carberus.auth.service.AuthUserDetailsService;
import com.cb.carberus.config.CustomUserDetails;
import com.cb.carberus.config.UserContext;
import com.cb.carberus.constants.UserRole;
import com.cb.carberus.errorHandler.error.UserAlreadyExistException;
import com.cb.carberus.errorHandler.error.UserNotFoundException;
import com.cb.carberus.security.jwt.JwtUtil;
import com.cb.carberus.user.controller.UserController;
import com.cb.carberus.user.dto.AddUserDTO;
import com.cb.carberus.user.dto.UpdateUserRoleDTO;
import com.cb.carberus.user.dto.UserDTO;
import com.cb.carberus.user.model.User;
import com.cb.carberus.user.service.UserService;
import com.cb.carberus.util.TestJwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserContext userContext;

    @MockitoBean
    private AuthUserDetailsService authUserDetailsService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private MockMvc mockMvc;

    User user;
    String jwtToken;
    UserDTO userDTO;

    @BeforeEach
    void setupAuthContext() {
        var auth = new UsernamePasswordAuthenticationToken("a@a.com", null, List.of(() -> "STUDENT"));
        SecurityContextHolder.getContext().setAuthentication(auth);

        user = new User();
        user.setId(Long.getLong("12343"));
        user.setEmail("a@a.com");
        user.setUserRole(UserRole.ADMIN);
        user.setCreatedAt(LocalDateTime.now());

        jwtToken = TestJwtUtil.createToken("a@a.com");
        User user = new User();
        user.setUserRole(UserRole.ADMIN);
        user.setId(Long.getLong("12345"));
        user.setEmail("a@a.com");
        CustomUserDetails mockUser = new CustomUserDetails(user);

        userDTO = new UserDTO();
        userDTO.setUserRole(UserRole.ADMIN);
        userDTO.setEmail("a@a.com");
        userDTO.setId("1233");
        userDTO.setFirstName("firstName");
        userDTO.setLastName("lastName");

        Mockito.when(authUserDetailsService.loadUserByUsername("a@a.com")).thenReturn(mockUser);
        Mockito.when(jwtUtil.validateToken(jwtToken)).thenReturn(Map.of("subject", "a@a.com"));
    }

    @Test
    void shouldSuccess_When_MeEndpointCalled() throws Exception {
        Mockito.when(userService.getCurrentUser()).thenReturn(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/me"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("a@a.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1233"));
    }


    @Test
    void shouldSuccess_WhenGetUsersAPICalled() throws Exception {

        Mockito.when(userService.getUsers()).thenReturn(List.of(userDTO));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users")
                        .cookie(new Cookie("token", jwtToken)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("1233"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value("a@a.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].userRole").value("ADMIN"));
    }

    @Test
    void shouldReturnUser_WhenGetUserAPICalled() throws Exception {
        String userId = "1233";

        Mockito.when(userService.getUser(userId)).thenReturn(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/api/v1/users/%s", userId))
                        .cookie(new Cookie("token", jwtToken)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1233"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("a@a.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userRole").value("ADMIN"));
    }

    @Test
    void shouldAddUser_WhenAddUserAPICalled() throws Exception {
        AddUserDTO addUserDTO = new AddUserDTO();
        addUserDTO.setEmail("b@b.com");
        addUserDTO.setFirstName("FName");
        addUserDTO.setLastName("lName");
        addUserDTO.setUserRole(UserRole.NONADMIN);
        addUserDTO.setPassword("somepassword");

        Mockito.when(userService.addUser(addUserDTO)).thenReturn(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(addUserDTO))
                        .cookie(new Cookie("token", jwtToken)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty());
    }

    @Test
    void shouldReturnError_WhenAddUserCalledWithExistingEmail() throws Exception {

        AddUserDTO addUserDTO = new AddUserDTO();
        addUserDTO.setEmail("b@b.com");
        addUserDTO.setFirstName("FName");
        addUserDTO.setLastName("lName");
        addUserDTO.setPassword("somepassword");
        addUserDTO.setUserRole(UserRole.NONADMIN);

        Mockito.doThrow(new UserAlreadyExistException())
                .when(userService).addUser(ArgumentMatchers.any(AddUserDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(addUserDTO)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void shouldFail_WhenEmailIsMissingToAddUserPayload() throws Exception {
        AddUserDTO addUserDTO = new AddUserDTO();
        addUserDTO.setFirstName("FName");
        addUserDTO.setLastName("lName");
        addUserDTO.setPassword("somepassword");
        addUserDTO.setUserRole(UserRole.NONADMIN);

        Mockito.when(userService.addUser(addUserDTO)).thenReturn(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(addUserDTO)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void shouldFail_WhenPasswordIsMissingToAddUserPayload() throws Exception {
        AddUserDTO addUserDTO = new AddUserDTO();
        addUserDTO.setEmail("a@a.com");
        addUserDTO.setFirstName("FName");
        addUserDTO.setLastName("lName");
        addUserDTO.setUserRole(UserRole.NONADMIN);

        Mockito.when(userService.addUser(addUserDTO)).thenReturn(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(addUserDTO)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void shouldFail_WhenRoleIsMissingToAddUserPayload() throws Exception {
        AddUserDTO addUserDTO = new AddUserDTO();
        addUserDTO.setEmail("a@a.com");
        addUserDTO.setFirstName("FName");
        addUserDTO.setLastName("lName");

        Mockito.doThrow(new UserAlreadyExistException())
                .when(userService).addUser(ArgumentMatchers.any(AddUserDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(addUserDTO)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void shouldReturnSuccess_WhenDeleteUser() throws Exception {
        String userId = "some-id";

        Mockito.when(userService.deleteUser(userId)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/api/v1/users/%s", userId))
                        .cookie(new Cookie("token", jwtToken)))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    void shouldReturnError_WhenDeleteUserCalledWithNonExistingUserEmail() throws Exception {
        String userId = "some-id";

        Mockito.doThrow(UserNotFoundException.class).when(userService).deleteUser(ArgumentMatchers.any(String.class));

        mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/api/v1/users/%s", userId))
                        .cookie(new Cookie("token", jwtToken)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void shouldReturnError_WhenDeleteUserCalledByNonAdminUser() throws Exception {
        String userId = "some-id";

        Mockito.when(userContext.getRole()).thenReturn(UserRole.NONADMIN);

        mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/users/%s", userId))
                        .cookie(new Cookie("token", jwtToken)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());

    }

    @Test
    void shouldSuccess_whenUserRoleUpdate() throws Exception {
        int userId = 12;

        UpdateUserRoleDTO dto = new UpdateUserRoleDTO();
        dto.setUserRole(UserRole.NONADMIN);

        Mockito.when(userContext.getRole()).thenReturn(UserRole.ADMIN);

        mockMvc.perform(MockMvcRequestBuilders.put(String.format("/api/v1/users/%d", userId))
                        .cookie(new Cookie("token", jwtToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

    }
}

package com.cb.carberus.user;

import com.cb.carberus.auth.service.AuthUserDetailsService;
import com.cb.carberus.config.UserContext;
import com.cb.carberus.constants.Role;
import com.cb.carberus.errorHandler.error.UserAlreadyExistException;
import com.cb.carberus.errorHandler.error.UserNotFoundException;
import com.cb.carberus.security.jwt.JwtUtil;
import com.cb.carberus.user.controller.UserController;
import com.cb.carberus.user.dto.AddUserDTO;
import com.cb.carberus.user.dto.UserResponseDTO;
import com.cb.carberus.user.service.UserService;
import com.cb.carberus.util.TestJwtUtil;
import com.cb.carberus.util.TestSecurityFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(TestSecurityFilter.class)
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

    UserResponseDTO userResponseDTO;
    String jwtToken;

    @BeforeEach
    void setupAuthContext() {
        var auth = new UsernamePasswordAuthenticationToken("a@a.com", null, List.of(() -> "STUDENT"));
        SecurityContextHolder.getContext().setAuthentication(auth);

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId("some-id");
        userResponseDTO.setEmail("a@a.com");
        userResponseDTO.setRoles(List.of("STUDENT"));
        userResponseDTO.setCreatedAt(LocalDateTime.now());

        jwtToken = TestJwtUtil.createToken("a@a.com");
        UserDetails mockUser = new User("a@a.com", "", List.of(() -> "STUDENT"));

        when(authUserDetailsService.loadUserByUsername("a@a.com")).thenReturn(mockUser);
        when(jwtUtil.validateToken(jwtToken)).thenReturn(Map.of("subject", "a@a.com"));
    }

    @Test
    void shouldReturnCurrentUser() throws Exception {
        when(userService.getCurrentUser()).thenReturn(userResponseDTO);

        mockMvc.perform(get("/me")
                        .cookie(new Cookie("token", jwtToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("a@a.com"))
                .andExpect(jsonPath("$.id").value("some-id"));
    }

    @Test
    void shouldFailWhenThereIsNotAuthToken() throws Exception {
        String token = TestJwtUtil.createToken("a@a.com");
        UserDetails mockUser = new User("a@a.com", "", List.of(() -> "STUDENT"));
        when(authUserDetailsService.loadUserByUsername("a@a.com")).thenReturn(mockUser);
        when(jwtUtil.validateToken(token)).thenReturn(Map.of("subject", "a@a.com"));
        when(userService.getCurrentUser()).thenReturn(userResponseDTO);

        mockMvc.perform(get("/me"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldReturnUsers_WhenGetUsersAPICalled() throws Exception {
        UserResponseDTO[] userResponseDTOs = new UserResponseDTO[]{
                userResponseDTO
        };

        when(userService.getUsers()).thenReturn(userResponseDTOs);

        mockMvc.perform(get("/users")
                        .cookie(new Cookie("token", jwtToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("some-id"))
                .andExpect(jsonPath("$[0].email").value("a@a.com"))
                .andExpect(jsonPath("$[0].roles").value("STUDENT"));
    }

    @Test
    void shouldReturnUser_WhenGetUserAPICalled() throws Exception {
        String userId = "some-id";

        when(userService.getUser(userId)).thenReturn(userResponseDTO);

        mockMvc.perform(get(String.format("/users/%s", userId))
                        .cookie(new Cookie("token", jwtToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("some-id"))
                .andExpect(jsonPath("$.email").value("a@a.com"))
                .andExpect(jsonPath("$.roles").value("STUDENT"));
    }

    @Test
    void shouldAddUser_WhenAddUserAPICalled() throws Exception {

        AddUserDTO addUserDTO = new AddUserDTO();
        addUserDTO.setEmail("b@b.com");
        addUserDTO.setFirstName("FName");
        addUserDTO.setLastName("lName");
        addUserDTO.setRole(Role.TESTER);
        addUserDTO.setPassword("somepassword");

        doNothing().when(userService).addUser(any(AddUserDTO.class));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(addUserDTO))
                        .cookie(new Cookie("token", jwtToken)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnError_WhenAddUserCalledWithExistingEmail() throws Exception {

        AddUserDTO addUserDTO = new AddUserDTO();
        addUserDTO.setEmail("b@b.com");
        addUserDTO.setFirstName("FName");
        addUserDTO.setLastName("lName");
        addUserDTO.setPassword("somepassword");
        addUserDTO.setRole(Role.TESTER);

        doThrow(new UserAlreadyExistException())
                .when(userService).addUser(any(AddUserDTO.class));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(addUserDTO))
                        .cookie(new Cookie("token", jwtToken)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldReturnSuccess_WhenDeleteUser() throws Exception {
        String userId = "some-id";

        when(userService.deleteUser(any(String.class))).thenReturn(true);

        mockMvc.perform(delete(String.format("/users/%s", userId))
                        .cookie(new Cookie("token", jwtToken)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void shouldReturnError_WhenDeleteUserCalledWithNonExistingUserEmail() throws Exception {
        String userId = "some-id";

        doThrow(UserNotFoundException.class).when(userService).deleteUser(any(String.class));

        mockMvc.perform(delete(String.format("/users/%s", userId))
                        .cookie(new Cookie("token", jwtToken)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldReturnError_WhenDeleteUserCalledByNonAdminUser() throws Exception {
        String userId = "some-id";

        when(userContext.getRoles()).thenReturn(List.of(Role.STUDENT));

        mockMvc.perform(delete(String.format("/users/%s", userId))
                        .cookie(new Cookie("token", jwtToken)))
                .andExpect(status().is4xxClientError());

    }
}

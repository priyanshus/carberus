package com.cb.carberus.user;

import com.cb.carberus.config.UserContext;
import com.cb.carberus.constants.UserRole;
import com.cb.carberus.errorHandler.error.DomainException;
import com.cb.carberus.errorHandler.error.StandardApiException;
import com.cb.carberus.errorHandler.error.UserNotFoundException;
import com.cb.carberus.user.dto.AddUserDTO;
import com.cb.carberus.user.dto.UserDTO;
import com.cb.carberus.user.model.User;
import com.cb.carberus.user.repository.UserRepository;
import com.cb.carberus.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;


public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private BCryptPasswordEncoder encoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUser_found() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO result = userService.getUser("1");

        Assertions.assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testGetUser_notFound() {
        Mockito.when(userRepository.findById("1")).thenReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.getUser("1"));
    }

    @Test
    void testGetCurrentUser_success() {
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("test@example.com");

        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getPrincipal()).thenReturn(userDetails);

        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        User user = new User();
        user.setEmail("test@example.com");

        Mockito.when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDTO result = userService.getCurrentUser();
        Assertions.assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testGetUsers_nonEmpty() {
        User user = new User();
        user.setEmail("u@example.com");

        Mockito.when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDTO> result = userService.getUsers();
        Assertions.assertEquals(1, result.size());
    }

    @Test
    void testGetUsers_empty() {
        Mockito.when(userRepository.findAll()).thenReturn(List.of());
        Assertions.assertThrows(DomainException.class, () -> userService.getUsers());
    }

    @Test
    void testAddUser_success() {
        AddUserDTO dto = new AddUserDTO();
        dto.setEmail("new@example.com");
        dto.setPassword("password");

        Mockito.when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        Mockito.when(encoder.encode("password")).thenReturn("hashed");

        userService.addUser(dto);
        Mockito.verify(userRepository).save(ArgumentMatchers.any(User.class));
    }

    @Test
    void testAddUser_duplicateEmail() {
        AddUserDTO dto = new AddUserDTO();
        dto.setEmail("existing@example.com");

        Mockito.when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(new User()));

        Assertions.assertThrows(DomainException.class, () -> userService.addUser(dto));
    }

    @Test
    void testDeleteUser_asAdmin_success() {
        User user = new User();
        user.setId(1L);

        Mockito.when(userContext.getRole()).thenReturn(UserRole.ADMIN);
        Mockito.when(userRepository.findById("1")).thenReturn(Optional.of(user));

        boolean result = userService.deleteUser("1");

        Assertions.assertTrue(result);
        Mockito.verify(userRepository).delete(user);
    }

    @Test
    void testDeleteUser_notAdmin() {
        Mockito.when(userContext.getRole()).thenReturn(UserRole.TESTER);
        Assertions.assertThrows(StandardApiException.class, () -> userService.deleteUser("1"));
    }

    @Test
    void testUpdateUserRole_asAdmin_success() {
        User user = new User();
        user.setId(1L);

        Mockito.when(userContext.getRole()).thenReturn(UserRole.ADMIN);
        Mockito.when(userRepository.findById("1")).thenReturn(Optional.of(user));

        userService.updateUserRole("1", UserRole.ADMIN);
        Mockito.verify(userRepository).save(user);
    }

    @Test
    void testUpdateUserRole_notAdmin() {
        Mockito.when(userContext.getRole()).thenReturn(UserRole.TESTER);
        Assertions.assertThrows(StandardApiException.class, () -> userService.updateUserRole("1", UserRole.ADMIN));
    }
}

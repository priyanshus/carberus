package com.cb.carberus.auth.service;

import com.cb.carberus.config.UserContext;
import com.cb.carberus.constants.UserRole;
import com.cb.carberus.errorHandler.error.UserNotFoundException;
import com.cb.carberus.user.model.User;
import com.cb.carberus.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthUserDetailsServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    UserContext userContext;

    @Test
    void shouldLoadUserByUsernameSuccessfullyWhenUserExistsAndHasRoles() {
        User mockUser = new User();
        mockUser.setEmail("user@example.com");
        mockUser.setPassword("encodedPassword");
        mockUser.setUserRole(UserRole.ADMIN);

        Mockito.when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(mockUser));

        UserDetails userDetails =
                new AuthUserDetailsService(userRepository).loadUserByUsername("user@example.com");

        Assertions.assertEquals("user@example.com", userDetails.getUsername());
        Assertions.assertEquals("encodedPassword", userDetails.getPassword());
        Assertions.assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN")));
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        Mockito.when(userRepository.findByEmail("notfound@example.com"))
                .thenReturn(Optional.empty());

        AuthUserDetailsService service = new AuthUserDetailsService(userRepository);

        Assertions.assertThrows(UserNotFoundException.class,
                () -> service.loadUserByUsername("notfound@example.com")
        );
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserHasNoRoles() {
        User mockUser = new User();
        mockUser.setEmail("user@example.com");
        mockUser.setPassword("encodedPassword");

        Mockito.when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(mockUser));

        AuthUserDetailsService service = new AuthUserDetailsService(userRepository);

        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> service.loadUserByUsername("user@example.com")
        );
    }
}

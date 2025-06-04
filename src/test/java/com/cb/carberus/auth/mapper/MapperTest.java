package com.cb.carberus.auth.mapper;

import com.cb.carberus.auth.dto.SignupRequestDTO;
import com.cb.carberus.constants.Role;
import com.cb.carberus.user.dto.UserResponseDTO;
import com.cb.carberus.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MapperTest {
    @Test
    void shouldMapSignupRequestDtoToUserWithEncodedPasswordAndRoles() {
        SignupRequestDTO dto = new SignupRequestDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("plainPassword");
        dto.setRoles(List.of("STUDENT", "ADMIN"));

        BCryptPasswordEncoder encoder =
                mock(BCryptPasswordEncoder.class);
        when(encoder.encode("plainPassword")).thenReturn("encodedPassword");

        User user = Mapper.toUser(dto, encoder);

        Assertions.assertEquals("user@example.com", user.getEmail());
        Assertions.assertEquals("encodedPassword", user.getPassword());
        Assertions.assertNotNull(user.getCreatedAt());
        Assertions.assertTrue(user.getRoles().contains(Role.STUDENT));
        Assertions.assertTrue(user.getRoles().contains(Role.ADMIN));
    }

    @Test
    void shouldMapUserToCurrentUserResponseDtoCorrectly() {
        User user = new User();
        user.setId("some-id");
        user.setEmail("user@example.com");
        user.setCreatedAt(java.time.LocalDateTime.now());
        user.setRoles(List.of(Role.STUDENT, Role.ADMIN));

        UserResponseDTO dto = Mapper.toCurrentUserResponse(user);

        Assertions.assertEquals("some-id", dto.getId());
        Assertions.assertEquals("user@example.com", dto.getEmail());
        Assertions.assertEquals(user.getCreatedAt(), dto.getCreatedAt());
        Assertions.assertTrue(dto.getRoles().contains("STUDENT"));
        Assertions.assertTrue(dto.getRoles().contains("ADMIN"));
    }

    @Test
    void shouldHandleEmptyRolesListInSignupRequestDto() {
        SignupRequestDTO dto = new SignupRequestDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("password");
        dto.setRoles(java.util.Collections.emptyList());

        BCryptPasswordEncoder encoder =
                mock(BCryptPasswordEncoder.class);
        when(encoder.encode("password")).thenReturn("encoded");

        User user = Mapper.toUser(dto, encoder);

        Assertions.assertTrue(user.getRoles().isEmpty());
    }

    @Test
    void shouldThrowExceptionForInvalidRoleString() {
        SignupRequestDTO dto = new SignupRequestDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("password");
        dto.setRoles(List.of("INVALID_ROLE"));

       BCryptPasswordEncoder encoder =
                mock(BCryptPasswordEncoder.class);
        when(encoder.encode("password")).thenReturn("encoded");

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Mapper.toUser(dto, encoder);
        });
    }
}

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
        dto.setRole("ADMIN");

        BCryptPasswordEncoder encoder =
                mock(BCryptPasswordEncoder.class);
        when(encoder.encode("plainPassword")).thenReturn("encodedPassword");

        User user = Mapper.toUser(dto, encoder);

        Assertions.assertEquals("user@example.com", user.getEmail());
        Assertions.assertEquals("encodedPassword", user.getPassword());
        Assertions.assertNotNull(user.getCreatedAt());
        Assertions.assertEquals(user.getRole(), Role.ADMIN);
    }

    @Test
    void shouldMapUserToCurrentUserResponseDtoCorrectly() {
        User user = new User();
        user.setId("some-id");
        user.setEmail("user@example.com");
        user.setCreatedAt(java.time.LocalDateTime.now());
        user.setRole(Role.ADMIN);

        UserResponseDTO dto = Mapper.toCurrentUserResponse(user);

        Assertions.assertEquals("some-id", dto.getId());
        Assertions.assertEquals("user@example.com", dto.getEmail());
        Assertions.assertEquals(user.getCreatedAt(), dto.getCreatedAt());
        Assertions.assertTrue(dto.getRole().contains("ADMIN"));
    }

    @Test
    void shouldHandleEmptyRolesListInSignupRequestDto() {
        SignupRequestDTO dto = new SignupRequestDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("password");
        dto.setRole("");

        BCryptPasswordEncoder encoder =
                mock(BCryptPasswordEncoder.class);
        when(encoder.encode("password")).thenReturn("encoded");

        Assertions.assertThrows(IllegalArgumentException.class, () -> Mapper.toUser(dto, encoder));
    }

    @Test
    void shouldThrowExceptionForInvalidRoleString() {
        SignupRequestDTO dto = new SignupRequestDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("password");
        dto.setRole("INVALID_ROLE");

       BCryptPasswordEncoder encoder =
                mock(BCryptPasswordEncoder.class);
        when(encoder.encode("password")).thenReturn("encoded");

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Mapper.toUser(dto, encoder);
        });
    }
}

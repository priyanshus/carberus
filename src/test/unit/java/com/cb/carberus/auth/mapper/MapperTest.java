package com.cb.carberus.auth.mapper;

import com.cb.carberus.auth.dto.SignupRequestDTO;
import com.cb.carberus.constants.UserRole;
import com.cb.carberus.user.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class MapperTest {
    @Test
    void shouldMapSignupRequestDtoToUserWithEncodedPasswordAndRoles() {
        SignupRequestDTO dto = new SignupRequestDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("plainPassword");
        dto.setRole("ADMIN");

        BCryptPasswordEncoder encoder =
                Mockito.mock(BCryptPasswordEncoder.class);
        Mockito.when(encoder.encode("plainPassword")).thenReturn("encodedPassword");

        com.cb.carberus.user.model.User user = Mapper.toUser(dto, encoder);

        Assertions.assertEquals("user@example.com", user.getEmail());
        Assertions.assertEquals("encodedPassword", user.getPassword());
        Assertions.assertNotNull(user.getCreatedAt());
        Assertions.assertEquals(user.getUserRole(), UserRole.ADMIN);
    }

    @Test
    void shouldMapUserToCurrentUserResponseDtoCorrectly() {
        com.cb.carberus.user.model.User user = new com.cb.carberus.user.model.User();
        user.setId(Long.getLong("12343"));
        user.setEmail("user@example.com");
        user.setCreatedAt(java.time.LocalDateTime.now());
        user.setUserRole(UserRole.ADMIN);

        User dto = Mapper.toCurrentUserResponse(user);

        Assertions.assertEquals("some-id", dto.getId());
        Assertions.assertEquals("user@example.com", dto.getEmail());
        Assertions.assertEquals(user.getCreatedAt(), dto.getCreatedAt());
        Assertions.assertEquals(dto.getUserRole(), UserRole.ADMIN);
    }

    @Test
    void shouldHandleEmptyRolesListInSignupRequestDto() {
        SignupRequestDTO dto = new SignupRequestDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("password");
        dto.setRole("");

        BCryptPasswordEncoder encoder =
                Mockito.mock(BCryptPasswordEncoder.class);
        Mockito.when(encoder.encode("password")).thenReturn("encoded");

        Assertions.assertThrows(IllegalArgumentException.class, () -> Mapper.toUser(dto, encoder));
    }

    @Test
    void shouldThrowExceptionForInvalidRoleString() {
        SignupRequestDTO dto = new SignupRequestDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("password");
        dto.setRole("INVALID_ROLE");

       BCryptPasswordEncoder encoder =
                Mockito.mock(BCryptPasswordEncoder.class);
        Mockito.when(encoder.encode("password")).thenReturn("encoded");

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Mapper.toUser(dto, encoder);
        });
    }
}

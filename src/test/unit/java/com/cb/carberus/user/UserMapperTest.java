package com.cb.carberus.user;

import com.cb.carberus.constants.UserRole;
import com.cb.carberus.user.dto.AddUserDTO;
import com.cb.carberus.user.dto.UserDTO;
import com.cb.carberus.user.mapper.UserMapper;
import com.cb.carberus.user.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserMapperTest {
    private final UserMapper mapper = UserMapper.INSTANCE;

    @Test
    void testToUserDTO() {
        User user = new User();
        user.setId(1L);
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUserRole(UserRole.ADMIN);
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());

        UserDTO dto = mapper.toUserDTO(user);

        Assertions.assertEquals(user.getEmail(), dto.getEmail());
        Assertions.assertEquals(user.getFirstName(), dto.getFirstName());
        Assertions.assertEquals(user.getLastName(), dto.getLastName());
        Assertions.assertEquals(user.getUserRole(), dto.getUserRole());
    }

    @Test
    void testToUser() {
        AddUserDTO dto = new AddUserDTO();
        dto.setEmail("alice@example.com");
        dto.setFirstName("Alice");
        dto.setLastName("Smith");
        dto.setPassword("hashed_password");

        User user = mapper.toUser(dto);

        Assertions.assertNull(user.getId());
        Assertions.assertEquals("alice@example.com", user.getEmail());
        Assertions.assertEquals("Alice", user.getFirstName());
        Assertions.assertEquals("Smith", user.getLastName());
        Assertions.assertEquals("hashed_password", user.getPassword());
        Assertions.assertNull(user.getCreatedAt());
        Assertions.assertNull(user.getIsActive());
    }
}

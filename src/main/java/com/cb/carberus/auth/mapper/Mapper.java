package com.cb.carberus.auth.mapper;

import com.cb.carberus.user.dto.AddUserDTO;
import com.cb.carberus.user.dto.UserResponseDTO;
import com.cb.carberus.auth.dto.SignupRequestDTO;
import com.cb.carberus.constants.Role;
import com.cb.carberus.user.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

public class Mapper {
    public static User toUser(SignupRequestDTO dto, BCryptPasswordEncoder encoder) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(toEnumRole(dto.getRole()));
        return user;
    }

    public static UserResponseDTO toCurrentUserResponse(User user) {
        UserResponseDTO currentUserResponseDTO = new UserResponseDTO();
        currentUserResponseDTO.setEmail(user.getEmail());
        currentUserResponseDTO.setFirstName(user.getFirstName());
        currentUserResponseDTO.setLastName(user.getLastName());

        if (user.getRole() != null) {
            currentUserResponseDTO.setRole(user.getRole());
        }
        currentUserResponseDTO.setId(user.getId());
        currentUserResponseDTO.setCreatedAt(user.getCreatedAt());
        return currentUserResponseDTO;
    }

    public static User toUser(AddUserDTO dto, BCryptPasswordEncoder encoder) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(dto.getRole());
        return user;
    }

    private static Role toEnumRole(String role) {
        return Role.valueOf(role.toUpperCase());
    }

}

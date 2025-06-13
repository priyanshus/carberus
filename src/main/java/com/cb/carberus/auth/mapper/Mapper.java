package com.cb.carberus.auth.mapper;

import com.cb.carberus.auth.dto.SignupRequestDTO;
import com.cb.carberus.constants.UserRole;
import com.cb.carberus.user.dto.AddUserDTO;
import com.cb.carberus.user.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

public class Mapper {
    public static com.cb.carberus.user.model.User toUser(SignupRequestDTO dto, BCryptPasswordEncoder encoder) {
        com.cb.carberus.user.model.User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUserRole(toEnumRole(dto.getRole()));
        return user;
    }

    public static User toCurrentUserResponse(User user) {
        User currentUserResponseDTO = new User();
        currentUserResponseDTO.setEmail(user.getEmail());
        currentUserResponseDTO.setFirstName(user.getFirstName());
        currentUserResponseDTO.setLastName(user.getLastName());

        if (user.getUserRole() != null) {
            currentUserResponseDTO.setUserRole(user.getUserRole());
        }
        currentUserResponseDTO.setId(user.getId());
        currentUserResponseDTO.setCreatedAt(user.getCreatedAt());
        return currentUserResponseDTO;
    }

    public static com.cb.carberus.user.model.User toUser(AddUserDTO dto, BCryptPasswordEncoder encoder) {
        com.cb.carberus.user.model.User user = new com.cb.carberus.user.model.User();
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUserRole(dto.getUserRole());
        return user;
    }

    private static UserRole toEnumRole(String role) {
        return UserRole.valueOf(role.toUpperCase());
    }

}

package com.cb.carberus.auth.mapper;

import com.cb.carberus.user.dto.CurrentUserResponseDTO;
import com.cb.carberus.auth.dto.SignupRequestDTO;
import com.cb.carberus.constants.Role;
import com.cb.carberus.user.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class Mapper {
    public static User toUser(SignupRequestDTO dto, BCryptPasswordEncoder encoder) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setRoles(toEnumRoles(dto.getRoles()));
        return user;
    }

    public static CurrentUserResponseDTO toCurrentUserResponse(User user) {
        CurrentUserResponseDTO currentUserResponseDTO = new CurrentUserResponseDTO();
        currentUserResponseDTO.setEmail(user.getEmail());
        currentUserResponseDTO.setRoles(toStringRoles(user.getRoles()));
        currentUserResponseDTO.setId(user.getId());
        currentUserResponseDTO.setCreatedAt(user.getCreatedAt());
        return currentUserResponseDTO;
    }

    private static List<Role> toEnumRoles(List<String> roles) {
        return roles.stream()
                .map(role -> Role.valueOf(role.toUpperCase()))
                .collect(Collectors.toList());
    };

    private static List<String> toStringRoles(List<Role> roles) {
        return roles.stream()
                .map(Role::name)
                .collect(Collectors.toList());
    };
}

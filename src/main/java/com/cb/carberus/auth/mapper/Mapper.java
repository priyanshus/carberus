package com.cb.carberus.auth.mapper;

import com.cb.carberus.auth.dto.CurrentUserResponseDTO;
import com.cb.carberus.auth.dto.SignupRequestDTO;
import com.cb.carberus.model.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

public class Mapper {
    public static User toUser(SignupRequestDTO dto, BCryptPasswordEncoder encoder) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setRoles(dto.getRoles());
        return user;
    }

    public static CurrentUserResponseDTO toCurrentUserResponse(User user) {
        CurrentUserResponseDTO currentUserResponseDTO = new CurrentUserResponseDTO();
        currentUserResponseDTO.setEmail(user.getEmail());
        currentUserResponseDTO.setRoles(user.getRoles());
        currentUserResponseDTO.setId(user.getId());
        currentUserResponseDTO.setCreatedAt(user.getCreatedAt());
        return currentUserResponseDTO;
    }
}

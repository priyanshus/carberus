package com.cb.carberus.auth.mapper;

import com.cb.carberus.auth.dto.SignupRequestDTO;
import com.cb.carberus.constants.UserRole;
import com.cb.carberus.model.user.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

public class SignupMapper {
    public static User toUser(SignupRequestDTO dto, BCryptPasswordEncoder encoder) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setRoles(dto.getRoles());
        return user;
    }
}

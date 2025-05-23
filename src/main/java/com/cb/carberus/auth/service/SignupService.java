package com.cb.carberus.auth.service;

import com.cb.carberus.auth.dto.SignupRequestDTO;
import com.cb.carberus.auth.dto.SignupResponseDTO;
import com.cb.carberus.auth.mapper.Mapper;
import com.cb.carberus.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SignupService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public SignupService(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public SignupResponseDTO handleSignup(SignupRequestDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            return new SignupResponseDTO("Email already exists", dto.getEmail(), "");
        }

        var user = Mapper.toUser(dto, encoder); // converts DTO to model
        System.out.println(user);
        userRepository.save(user);

        return new SignupResponseDTO("Signup successful", dto.getEmail(), user.getId());
    }
}

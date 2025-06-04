package com.cb.carberus.user.service;

import com.cb.carberus.config.UserContext;
import com.cb.carberus.constants.Role;
import com.cb.carberus.errorHandler.error.UnauthorizedAccessException;
import com.cb.carberus.errorHandler.error.UserAlreadyExistException;
import com.cb.carberus.errorHandler.error.UserNotFoundException;
import com.cb.carberus.user.dto.AddUserDTO;
import com.cb.carberus.user.dto.UserResponseDTO;
import com.cb.carberus.auth.mapper.Mapper;
import com.cb.carberus.user.model.User;
import com.cb.carberus.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserContext userContext;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public UserService(UserRepository userRepository, UserContext userContext, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.userContext = userContext;
        this.encoder = encoder;
    }

    public UserResponseDTO getUser(String userId) {
        var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        return Mapper.toCurrentUserResponse(user);
    }

    public UserResponseDTO getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userDetails = (UserDetails) authentication.getPrincipal();
        var email = userDetails.getUsername();
        var user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        return Mapper.toCurrentUserResponse(user);
    }

    public UserResponseDTO[] getUsers() {
        List<User> users = userRepository.findAll();


        return users.stream()
                .map(Mapper::toCurrentUserResponse)
                .toArray(UserResponseDTO[]::new);
    }

    public void addUser(AddUserDTO addUserDTO) {
        if (userRepository.findByEmail(addUserDTO.getEmail()).isPresent()) {
            throw new UserAlreadyExistException();
        }

        User user = Mapper.toUser(addUserDTO, encoder);
        userRepository.save(user);
    }

    public boolean deleteUser(String id) {
        if (userContext.getRoles().contains(Role.ADMIN)) {
            var user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
            userRepository.delete(user);
            return true;
        }

        throw new UnauthorizedAccessException();
    }

    public void updateUserRole(String userId, Role role) {
        var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.setRoles(List.of(role));
        userRepository.save(user);
    }
}

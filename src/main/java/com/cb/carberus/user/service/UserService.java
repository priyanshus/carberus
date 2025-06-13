package com.cb.carberus.user.service;

import com.cb.carberus.config.UserContext;
import com.cb.carberus.constants.UserRole;
import com.cb.carberus.errorHandler.error.*;
import com.cb.carberus.errorHandler.model.DomainErrorCode;
import com.cb.carberus.errorHandler.model.StandardErrorCode;
import com.cb.carberus.user.dto.AddUserDTO;
import com.cb.carberus.user.dto.UserDTO;
import com.cb.carberus.user.mapper.UserMapper;
import com.cb.carberus.user.model.User;
import com.cb.carberus.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

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

    public UserDTO getUser(String userId) {
        Long id = Long.parseLong(userId);
        var user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        return UserMapper.INSTANCE.toUserDTO(user);
    }

    public UserDTO getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userDetails = (UserDetails) authentication.getPrincipal();
        var email = userDetails.getUsername();
        var user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        return UserMapper.INSTANCE.toUserDTO(user);
    }

    public List<UserDTO> getUsers() {
        Iterable<User> users = userRepository.findAll();
        List<User> userList = StreamSupport
                .stream(users.spliterator(), false)
                .toList();

        if (userList.isEmpty()) {
            throw new DomainException(DomainErrorCode.NO_USERS_FOUND);
        }

        return userList
                .stream()
                .map(UserMapper.INSTANCE::toUserDTO)
                .toList();
    }

    public UserDTO addUser(AddUserDTO addUserDTO) {
        if (!userContext.getRole().equals(UserRole.ADMIN)) {
            throw new StandardApiException(StandardErrorCode.UNAUTHORIZED);
        }

        if (userRepository.findByEmail(addUserDTO.getEmail()).isPresent()) {
            throw new DomainException(DomainErrorCode.EMAIL_ALREADY_TAKEN);
        }

        String rawPassword = addUserDTO.getPassword();
        String encodedPassword = encoder.encode(rawPassword);

        addUserDTO.setPassword(encodedPassword);

        User user = UserMapper.INSTANCE.toUser(addUserDTO);
        var userSaved = userRepository.save(user);

        return UserMapper.INSTANCE.toUserDTO(userSaved);
    }

    public boolean deleteUser(String id) {

        if (userContext.getRole().equals(UserRole.ADMIN)) {
            var user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
            userRepository.delete(user);
            return true;
        }

        throw new StandardApiException(StandardErrorCode.UNAUTHORIZED);
    }

    public void updateUserRole(String userId, UserRole role) {
        if (userContext.getRole().equals(UserRole.ADMIN)) {
            var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
            user.setUserRole(role);
            userRepository.save(user);
            return;
        }

        throw new StandardApiException(StandardErrorCode.UNAUTHORIZED);
    }
}

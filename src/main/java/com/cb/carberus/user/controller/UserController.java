package com.cb.carberus.user.controller;

import com.cb.carberus.user.dto.AddUserDTO;
import com.cb.carberus.user.dto.UpdateUserRoleDTO;
import com.cb.carberus.user.dto.UserResponseDTO;
import com.cb.carberus.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMe() {
        UserResponseDTO user = userService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        boolean deleted = userService.deleteUser(userId);
        return deleted
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/users")
    public ResponseEntity<UserResponseDTO[]> getUsers() {
        var users =  userService.getUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users")
    public ResponseEntity<Void> addUser(@Valid  @RequestBody AddUserDTO addUserDTO) {
        System.out.println(addUserDTO);
        userService.addUser(addUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable String userId) {
        var user = userService.getUser(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<Void> updateUser(@Valid
            @PathVariable String userId,
            @RequestBody UpdateUserRoleDTO request
    ) {
        userService.updateUserRole(userId, request.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

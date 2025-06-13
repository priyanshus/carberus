package com.cb.carberus.user.controller;

import com.cb.carberus.user.dto.AddUserDTO;
import com.cb.carberus.user.dto.UpdateUserRoleDTO;
import com.cb.carberus.user.dto.UserDTO;
import com.cb.carberus.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMe() {
        var user = userService.getCurrentUser();
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
    public ResponseEntity<List<UserDTO>> getUsers() {
        var users =  userService.getUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users")
    public ResponseEntity<UserDTO> addUser(@Valid  @RequestBody AddUserDTO addUserDTO) {
        UserDTO userDTO = userService.addUser(addUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String userId) {
        var user = userService.getUser(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<Void> updateUser(@Valid
            @PathVariable String userId,
            @RequestBody UpdateUserRoleDTO request
    ) {
        userService.updateUserRole(userId, request.getUserRole());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
